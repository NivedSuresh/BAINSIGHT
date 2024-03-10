package org.exchange.user.Service;


import org.exchange.library.Dto.Authentication.AdminAuthResponse;
import org.exchange.library.Dto.Authentication.AuthRequest;
import org.exchange.library.Enums.MfaType;
import org.exchange.user.Model.Admin;
import org.exchange.user.Model.PrincipalRevoked;
import org.exchange.user.Repository.Postgres.AdminRepo;
import org.exchange.user.Repository.Postgres.ClientRepo;
import org.exchange.user.Security.Authentication.Admin.AdminAuthenticationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpCookie;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureWebTestClient
public class AuthServiceTests {


    @Autowired
    @Qualifier("ADMIN_AUTH_MANAGER")
    private AdminAuthenticationManager adminAuthenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    ReactiveJwtDecoder decoder;

    @MockBean
    private AdminRepo adminRepo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    ClientRepo clientRepo;

    @Test
    public void testSuccessfulAuthentication() {
        final var email = "niveds406@gmail.com";
        final var password = "12345";

        AuthRequest request = new AuthRequest(email, password);


        Admin admin = Admin.builder().id(1L).is_banned(false)
                .mfa_type(MfaType.DISABLED)
                .password(encoder.encode("12345"))
                .email("niveds406@gmail.com")
                .authority("ADMIN")
                .build();

        when(adminRepo.findByEmail(email)).thenReturn(Mono.just(admin));


        ExchangeResult result = webTestClient.post()
                .uri("/api/bainsight/auth/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(AdminAuthResponse.class)
                .returnResult();

        ResponseCookie refreshToken = result.getResponseCookies().getFirst("REFRESH_TOKEN");

        Assertions.assertNotNull(refreshToken);

        Jwt jwt = decoder.decode(refreshToken.getValue()).block();

        assert jwt != null;
        Assertions.assertEquals(jwt.getSubject(), "niveds406@gmail.com");
        Assertions.assertEquals(jwt.getClaim("authority"), "ADMIN_REFRESH_TOKEN");

    }

    @Test
    void testRenewJwt() {
        UUID uuid = UUID.randomUUID();
        String jwt = jwtService.generateJwt(uuid.toString(), "CLIENT_REFRESH_TOKEN", Instant.now().plus(100, ChronoUnit.DAYS)).block();

        PrincipalRevoked revoked = PrincipalRevoked.builder().revoked(false).username(uuid.toString()).build();
        when(clientRepo.getPrincipalValidationForClient(uuid)).thenReturn(Mono.just(revoked));

        Assertions.assertNotNull(jwt);
        ExchangeResult result = webTestClient.get()
                .uri("/api/bainsight/auth/refresh")
                .cookie("REFRESH_TOKEN", jwt)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(String.class)
                .returnResult();

        ResponseCookie accessToken = result.getResponseCookies().getFirst("ACCESS_TOKEN");

        Assertions.assertNotNull(accessToken);

        Jwt newJwt = decoder.decode(accessToken.getValue()).block();

        assert newJwt != null;
        Assertions.assertEquals(newJwt.getSubject(), uuid.toString());

        Assertions.assertEquals(newJwt.getClaim("authority"), "CLIENT");


    }

    @Test
    void failRenewJwt() {
        UUID uuid = UUID.randomUUID();
        PrincipalRevoked revoked = PrincipalRevoked.builder().revoked(false).username(uuid.toString()).build();
        when(clientRepo.getPrincipalValidationForClient(uuid)).thenReturn(Mono.just(revoked));

        webTestClient.get()
                .uri("/api/bainsight/token")
                .header("Authorization", "Bearer fake-token-djcbdjchbvdjcdvjcvdjcvdjcvdjhcvdjc djc dhc dhc dhjcdbcjdh cjd cjdcdjcijhiewgfeuygfuewvf")
                .exchange()
                .expectStatus().is5xxServerError() //Nimbus Jwt Decoder will throw INTERNAL_SERVER_ERROR
                .expectBody(String.class);

    }


}
