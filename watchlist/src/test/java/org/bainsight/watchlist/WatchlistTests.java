package org.bainsight.watchlist;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.bainsight.watchlist.Payload.AddToWatchlist;
import org.bainsight.watchlist.Payload.RemoveSymbol;
import org.bainsight.watchlist.Payload.WatchlistDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.lifecycle.Startables;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




/**
 * TestContainers were avoided as @EnableRedisEnhancedRepositories was throwing error
 * while being used with testcontainers and that issue is still open in their GitHub.
 * */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class WatchlistTests {


    @Container
	private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");


    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private final String  ucc = UUID.randomUUID().toString();

    @DynamicPropertySource
    static void updateRedis(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgres).join();

        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5433/mydatabase");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "secret");
    }


    @Test
    @Order(0)
    void connectionEstablished() {
        postgres.start();
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }


    public ResultActions addToListRequest() throws Exception {
        AddToWatchlist req = new AddToWatchlist("w1", "GOOGL");
        return this.mockMvc.perform(put("http://localhost:8088/api/bainsight/watchlist?page=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))
                .header("x-auth-user-id", ucc));
    }



    @Test
    @Order(1)
    void addToListAndRemoveSymbol() throws Exception {
        AddToWatchlist req = new AddToWatchlist("w1", "GOOGL");
        AtomicLong watchlistId = new AtomicLong();
        this.addToListRequest()
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    WatchlistDto watchlistDto = mapper.readValue(contentAsString, WatchlistDto.class);
                    Assertions.assertEquals(watchlistDto.watchlistName(), req.watchlistName());
                    Assertions.assertEquals(watchlistDto.sticks().getFirst().getSymbol(), req.symbol());

                    watchlistId.set(watchlistDto.watchlistId());
                });

        /* Fail as the same symbol cannot be added twice */
        this.addToListRequest().andExpect(status().isBadRequest());


        /* Considering the fact that this is the user's first ever watchlist, it should
        *  be default pinned */
        this.checkIfWatchlistIsPinned(watchlistId.get(), ucc);
        /* Remove the newly added symbol */
        this.removeSymbol(watchlistId.get());
    }

    /* LINE: 103 */
    private void checkIfWatchlistIsPinned(long watchlistId, String ucc) throws Exception {
        this.mockMvc.perform(get("http://localhost:8088/api/bainsight/watchlist?page=2")
                .header("x-auth-user-id", ucc))
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    WatchlistDto watchlistDto = mapper.readValue(contentAsString, WatchlistDto.class);
                    System.out.println(watchlistDto);
                    Assertions.assertEquals(watchlistId, watchlistDto.watchlistId());
                });
    }


    /* LINE: 106 */
    void removeSymbol(Long watchlistId) throws Exception {
        /* watchlist id gets value from prev test */
        RemoveSymbol removeSymbol = new RemoveSymbol(watchlistId, "GOOGL");
        this.mockMvc.perform(delete("http://localhost:8088/api/bainsight/watchlist?page=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(removeSymbol))
                .header("x-auth-user-id", ucc))
                .andExpect(status().isOk());
    }



}
