package org.bainsight.portfolio;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.bainsight.portfolio.Data.Wallet.WalletService;
import org.bainsight.portfolio.Model.Dto.NewTransaction;
import org.bainsight.portfolio.Model.Dto.WalletDto;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.bainsight.portfolio.Model.Enums.WalletTransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest
/* Don't autoconfigure the database but use the test database provided */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureWebTestClient
@AutoConfigureMockMvc
public class WalletTests {

    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private WalletService walletService;


    @Test
    void connectionEstablished() {
        postgres.start();
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void fetchWalletTest() throws Exception {
        String ucc = UUID.randomUUID().toString();
        WalletDto walletDto = getWalletDto(ucc);

        Assertions.assertEquals(0.0, walletDto.getCurrentBalance());
        Assertions.assertEquals(walletDto.getWithdrawableBalance(), 0.0);


        NewTransaction newTransaction = NewTransaction.builder()
                .walletTransactionType(WalletTransactionType.CREDIT)
                .amount(100.0)
                .ucc(ucc)
                .build();

        this.walletService.addNewTransaction(newTransaction);

        walletDto = getWalletDto(ucc);

        Assertions.assertEquals(100.0, walletDto.getWithdrawableBalance());
        Assertions.assertEquals(walletDto.getCurrentBalance(), 100.0);

    }

    @Test
    void testBalanceUpdates() {

        UUID ucc = UUID.randomUUID();

        Wallet wallet = this.walletService.fetchWallet(ucc.toString());

        this.walletService.updateWalletBalance(ucc, new WalletUpdateRequest(100.0, 100.0));

        this.walletService.updateWalletBalance(ucc, new WalletUpdateRequest(0.0, -50.0));

        wallet = this.walletService.fetchWallet(ucc.toString());

        Assertions.assertEquals(wallet.getCurrentBalance(), 100);
        Assertions.assertEquals(wallet.getAvailableBalance(), 50);

        this.walletService.updateWalletBalance(ucc ,new WalletUpdateRequest(-50.0, -50.0));
        wallet = this.walletService.fetchWallet(ucc.toString());

        Assertions.assertEquals(wallet.getAvailableBalance(), 0);
        Assertions.assertEquals(wallet.getCurrentBalance(), 50);
    }

    private WalletDto getWalletDto(String ucc) throws Exception {
        MockHttpServletResponse response = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/bainsight/wallet")
                        .header("x-auth-user-id", ucc))
                .andReturn()
                .getResponse();
        return this.mapper.readValue(response.getContentAsString(), WalletDto.class);

    }


}
