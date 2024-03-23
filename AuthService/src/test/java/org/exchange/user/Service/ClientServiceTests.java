package org.exchange.user.Service;

import org.exchange.library.Dto.Authentication.ClientAuthResponse;
import org.exchange.library.Dto.Authentication.ClientSignupRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest
//Don't autoconfigure the database but use the test database provided
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureWebTestClient
public class ClientServiceTests {

    @Autowired
    private WebTestClient testClient;

    @Autowired
    ReactiveJwtDecoder jwtDecoder;

    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");


    @Test
    public void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void testSignup() {
        ClientSignupRequest request = ClientSignupRequest.builder()
                .email("nived@gmail.com")
                .username("nived")
                .phoneNumber("0987654321")
                .password("12345")
                .confirmPassword("12345")
                .build();

        ExchangeResult result = testClient.post().uri("/api/bainsight/auth/client/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClientAuthResponse.class)
                .value(authResponse -> {
                    assert authResponse != null;
                    Assertions.assertEquals(request.getPhoneNumber(), authResponse.getPhoneNumber());
                })
                .returnResult();

        ResponseCookie refreshToken = result.getResponseCookies().getFirst("REFRESH_TOKEN");
        Assertions.assertNotNull(refreshToken);

        Jwt jwt = jwtDecoder.decode(refreshToken.getValue()).block();
        assert jwt != null;
        Assertions.assertNotNull(jwt.getSubject());
        Assertions.assertEquals("CLIENT_REFRESH_TOKEN", jwt.getClaim("authority"));
    }

}
