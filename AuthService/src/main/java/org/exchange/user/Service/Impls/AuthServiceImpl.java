package org.exchange.user.Service.Impls;


import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Advice.Error;
import org.exchange.library.Dto.Authentication.*;
import org.exchange.library.Enums.MfaType;
import org.exchange.library.Exception.Authentication.InvalidCredentialsException;
import org.exchange.library.Exception.Authentication.UnableToInitiateMfaException;
import org.exchange.library.Exception.Authorization.InvalidJwtException;
import org.exchange.library.Exception.Authorization.JwtExpiredException;
import org.exchange.library.Exception.BadRequest.InvalidStateException;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.Exception.IO.ConnectionFailureException;
import org.exchange.user.Model.PrincipalRevoked;
import org.exchange.user.Repository.Postgres.AdminRepo;
import org.exchange.user.Repository.Postgres.ClientRepo;
import org.exchange.user.Security.Authentication.Admin.AdminAuthenticationManager;
import org.exchange.user.Security.Authentication.Admin.AdminDetails;
import org.exchange.user.Security.Authentication.Client.ClientAuthenticationManager;
import org.exchange.user.Security.Authentication.Client.ClientDetails;
import org.exchange.user.Service.AuthService;
import org.exchange.user.Service.JwtService;
import org.exchange.user.Service.MfaService;
import org.exchange.user.Utils.CookieUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {


    private final ReactiveJwtDecoder decoder;
    private final ClientAuthenticationManager CLIENT_AUTH_MANAGER;

    private final AdminRepo adminRepo;
    private final AdminAuthenticationManager ADMIN_AUTH_MANAGER;
    private final JwtService jwtService;

    private final MfaService mfaService;
    private final ClientRepo clientRepo;
    private final CookieUtils cookieUtils;

    public AuthServiceImpl(final ReactiveJwtDecoder decoder,
                           @Qualifier("CLIENT_AUTH_MANAGER") final ClientAuthenticationManager CLIENT_AUTH_MANAGER,
                           final AdminRepo adminRepo,
                           @Qualifier("ADMIN_AUTH_MANAGER") final AdminAuthenticationManager ADMIN_AUTH_MANAGER,
                           final JwtService jwtService,
                           final MfaService mfaService,
                           final ClientRepo clientRepo, CookieUtils cookieUtils) {
        this.adminRepo = adminRepo;

        this.ADMIN_AUTH_MANAGER = ADMIN_AUTH_MANAGER;
        this.CLIENT_AUTH_MANAGER = CLIENT_AUTH_MANAGER;
        this.jwtService = jwtService;
        this.decoder = decoder;
        this.mfaService = mfaService;
        this.clientRepo = clientRepo;
        this.cookieUtils = cookieUtils;
    }


    @Override
    public Mono<String> renewJwt(ServerHttpRequest request) {
        System.out.println("Renew Jwt triggered");
        HttpCookie cookie = request.getCookies().getFirst("REFRESH_TOKEN");

        if(cookie == null) return Mono.error(InvalidJwtException::new);

        String refreshToken = cookie.getValue();
        log.debug("Inside Renew Jwt");

        return decoder.decode(refreshToken)
                .flatMap(jwt -> {
                    System.out.println("Decoded jwt");
                    return findPrincipalValidationFromDB(jwt.getClaim("authority"), jwt.getSubject())
                            .switchIfEmpty(Mono.error(new InvalidJwtException(Error.INVALID_JWT)))
                            .flatMap(principalValidation -> Mono.zip(Mono.just(jwt), Mono.just(principalValidation)));
                })

                .flatMap(objects -> {

                    Jwt jwt = objects.getT1();
                    PrincipalRevoked validation = objects.getT2();

                    log.info("Fetched validation : {}", validation);

                    if (validation.getRevoked()) {
                        return Mono.error(new InvalidCredentialsException("The account has been revoked"));
                    }


//                    Instant expiry = Instant.now().plus(1, ChronoUnit.HOURS);
                    Instant expiry = Instant.now();

                    /*  Refresh token's should only be used for access token renewal, thus
                     *  Refresh Tokens are decoded with a different authority which will
                     *  restrict the user from accessing other endpoints */

                    String authority = jwt.getClaim("authority").toString();
                    authority = authority.substring(0, authority.length() - 14);


                    return jwtService.generateJwt(jwt.getSubject(), authority, expiry);

                })
                .onErrorResume(throwable -> {
                    log.error("Exception : {}", throwable.getMessage());

                    return Mono.error(new InvalidJwtException(
                            "BadRequest or expired refresh token provided",
                            HttpStatus.UNAUTHORIZED,
                            Error.INVALID_REFRESH_TOKEN
                    ));
                });
    }

    private Mono<PrincipalRevoked> findPrincipalValidationFromDB(String authority, String subject) {
        log.info("Went to database, {}, {}!", authority, subject);
        return
                authority.equals("CLIENT_REFRESH_TOKEN") ?
                clientRepo.getPrincipalValidationForClient(UUID.fromString(subject)) :

                authority.equals("ADMIN_REFRESH_TOKEN") ?
                getPrincipalValidationForAdmin(subject) :

                Mono.error(InvalidJwtException::new);
    }

    private Mono<PrincipalRevoked> getPrincipalValidationForAdmin(String subject) {
        log.info("Validation is for Admin!");
        return adminRepo.getPrincipalValidation(subject)
                .map(aBoolean -> PrincipalRevoked.builder()
                        .revoked(aBoolean).build());
    }


    @Override
    public Mono<ClientAuthResponse> loginClient(AuthRequest request, ServerWebExchange webExchange) {

        log.info("Authenticate method called!");
        return CLIENT_AUTH_MANAGER.authenticate(getAuthToken(request.getIdentifier(), request.getPassword()))
                .doOnError(throwable -> {
                    if (throwable instanceof InvalidCredentialsException) throw (InvalidCredentialsException) throwable;
                    throw new InvalidCredentialsException();
                })


                .flatMap(authentication -> {
                    Optional<String> authority = (authentication).getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).findFirst();

                    if (authority.isEmpty()) return Mono.error(InvalidStateException::new);

                    Instant expiry = Instant.now();

                    Mono<JwtResponse> jwtResponseMono=  getJwtResponse(
                            authentication.getName(),
                            authority.get(),
                            expiry.plus(15, ChronoUnit.MINUTES),
                            expiry.plus(100, ChronoUnit.DAYS)
                    );

                    ClientDetails clientDetails = (ClientDetails) authentication.getPrincipal();

                    return jwtResponseMono.map(jwtResponse -> {
                        cookieUtils.bakeCookies(webExchange, jwtResponse);
                        return new ClientAuthResponse(
                                clientDetails.getUsername(), //will return ucc
                                clientDetails.getUsername(),
                                clientDetails.getEmail(),
                                clientDetails.getPhoneNumber()
                        );
                    });
                })

                .onErrorResume(e -> {
                    log.error("Exception : {}", e.getMessage());
                    if (e instanceof InvalidCredentialsException) throw (InvalidCredentialsException) e;
                    else throw new InvalidCredentialsException();
                });
    }

    /* The Admin if exists will be pulled from the DB and will be validated. */
    @Override
    public Mono<AdminAuthResponse> loginAdmin(AuthRequest request, ServerWebExchange webExchange) {
        return ADMIN_AUTH_MANAGER.authenticate(getAuthToken(request.getIdentifier(), request.getPassword()))
                .handle((authentication, sink) -> {
                    log.debug("Finished Authentication : {}", authentication);
                    if (!authentication.isAuthenticated()) {
                        sink.error(new InvalidCredentialsException());
                        return;
                    }
                    AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
                    if (adminDetails.getMfaType() != MfaType.DISABLED) {
                        log.info("Mfa enabled for Admin");
                        try {
                            sink.next(initiateMfa(adminDetails));
                            return;
                        } catch (Exception e) {
                            sink.error(new UnableToInitiateMfaException());
                        }
                        return;
                    }
                    Instant instant = Instant.now();

                    Mono<AdminAuthResponse> adminAuthResponseMono = getJwtResponse(
                            adminDetails.getUsername(),
                            adminDetails.getRole(),
                            instant.plus(1, ChronoUnit.MINUTES),
                            instant.plus(100, ChronoUnit.DAYS)
                    ).map(jwtResponse -> {
                        cookieUtils.bakeCookies(webExchange, jwtResponse);
                        return new AdminAuthResponse(adminDetails.getUsername());
                    });
                    sink.next(adminAuthResponseMono);
                })

                .flatMap(o -> (Mono<AdminAuthResponse>) o)

                .onErrorResume(throwable -> {

                    log.error("Exception caught : {}", throwable.getMessage());

                    if (throwable instanceof InvalidCredentialsException ||
                            throwable instanceof UnableToInitiateMfaException)
                        throw (GlobalException) throwable;

                    throw new ConnectionFailureException();
                });


    }


    private Mono<JwtResponse> initiateMfa(AdminDetails adminDetails) {
        return mfaService.switchAndStartMfa(adminDetails.getMfaType(),
                        adminDetails.getUsername())
                .map(message ->
                        new JwtResponse(null, null, message)
                );
    }


    public Mono<JwtResponse> getJwtResponse(String ucc, String authority, Instant forAccess, Instant forRefresh) {
        return jwtService.getAuthResponse(ucc, authority, forAccess, forRefresh);
    }

    @Override
    public Mono<TokenMeta> validateToken(ServerWebExchange webExchange) {
        HttpCookie accessToken = webExchange.getRequest().getCookies().getFirst("ACCESS_TOKEN");

        if(accessToken == null) return Mono.error(InvalidJwtException::new);
        return decoder.decode(accessToken.getValue())
                .map(jwt -> {
                    System.out.println("Now : " + Instant.now());
                    System.out.println("Expires " + jwt.getExpiresAt());
                    String authority = jwt.getClaim("authority");
                    String subject = jwt.getSubject();
                    return new TokenMeta(subject, subject, authority);
                })
                .onErrorResume(throwable -> {
                    if(throwable.getMessage().startsWith("Jwt expired")) return Mono.error(JwtExpiredException::new);
                    return Mono.error(InvalidJwtException::new);
                });
    }


    private Authentication getAuthToken(String username, String password) {
        System.out.println(username + " " + password);
        return new UsernamePasswordAuthenticationToken(username, password);
    }


}
