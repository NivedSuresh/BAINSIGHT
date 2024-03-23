package org.exchange.user.Service.Impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Advice.Error;
import org.exchange.library.Dto.Authentication.ClientAuthResponse;
import org.exchange.library.Dto.Authentication.ClientSignupRequest;
import org.exchange.library.Exception.Authentication.ConfirmPasswordMismatchException;
import org.exchange.library.Exception.BadRequest.EntityAlreadyExistsException;
import org.exchange.library.Exception.BadRequest.InvalidUpdateRequestException;
import org.exchange.library.Exception.IO.ConnectionFailureException;
import org.exchange.user.Mapper.Mapper;
import org.exchange.user.Model.Client;
import org.exchange.user.Repository.Postgres.ClientRepo;
import org.exchange.user.Service.ClientService;
import org.exchange.user.Service.JwtService;
import org.exchange.user.Service.KafkaService;
import org.exchange.user.Utils.CookieUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepo clientRepo;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaService kafkaService;
    private final CookieUtils cookieUtils;

    @Override
    public Mono<Client> findByPhone(String phoneNumber) {
        System.out.println("find by phone called");
        return clientRepo.findByPhoneNumber(phoneNumber)
                .onErrorResume(e -> {
                    log.error("Failed to fetch user: {}", e.getMessage());
                    return Mono.error(new ConnectionFailureException());
                });
    }

    /*
     If ID is present then it's an update request.
     For update request you have to fetch the password from the database and append it.
     Admin shouldn't be allowed to change Client's password but Client.
    */
    @Override
    @Transactional
    public Mono<ClientAuthResponse> save(ClientSignupRequest request, ServerWebExchange webExchange) {

        return clientRepo.existsByPhoneNumber(request.getPhoneNumber())
                .flatMap(exists -> {
                    if (exists) return Mono.error(new EntityAlreadyExistsException("User ", request.getPhoneNumber()));

                    if (request.getPassword() == null || !Objects.equals(request.getPassword(), request.getConfirmPassword())) {
                        return Mono.error(new ConfirmPasswordMismatchException(Error.CONFIRM_PASSWORD_MISMATCH));
                    }
                    request.setPassword(passwordEncoder.encode(request.getPassword()));

                    return saveUserAndUpdateMeta(request);
                })


                .zipWhen(client -> {
                    Instant instant = Instant.now();
                    return jwtService.getAuthResponse(client.getUcc().toString(), "CLIENT", instant.plus(1000, ChronoUnit.DAYS), instant.plus(1000, ChronoUnit.DAYS));
                })

                .map(tuple -> {
                    cookieUtils.bakeCookies(webExchange, tuple.getT2());
                    return mapper.getClientDto(tuple.getT1());
                })


                .onErrorResume(throwable -> {
                    throwable.printStackTrace();
                    log.error(throwable.getMessage());
                    if (throwable instanceof InvalidUpdateRequestException ||
                        throwable instanceof EntityAlreadyExistsException){
                        return Mono.error(throwable);
                    }


                    return Mono.error(ConnectionFailureException::new);
                });
    }

    @Transactional
    public Mono<Client> saveUserAndUpdateMeta(ClientSignupRequest request) {
        return clientRepo.save(mapper.requestToClient(request))
                .doOnNext(client -> {
                    kafkaService.updateClientMeta(client.getUcc(), 0.0);
                })
                .onErrorResume(throwable -> {
                    log.error(throwable.getMessage());
                    if(throwable instanceof DuplicateKeyException){
                        return Mono.error(new EntityAlreadyExistsException("User", request.getEmail()));
                    }
                    return Mono.error(ConnectionFailureException::new);
                });

    }

}
