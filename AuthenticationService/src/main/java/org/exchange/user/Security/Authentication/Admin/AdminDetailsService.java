package org.exchange.user.Security.Authentication.Admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Exception.Authentication.InvalidCredentialsException;
import org.exchange.library.Exception.IO.ServiceUnavailableException;
import org.exchange.user.Repository.Postgres.AdminRepo;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
@Primary
public class AdminDetailsService implements ReactiveUserDetailsService {

    private final AdminRepo adminRepo;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return adminRepo.findByEmail(email)
                .onErrorResume(throwable -> {
                    log.error("Exception : {}", throwable.getMessage());
                    throw new ServiceUnavailableException();
                })
                .handle((admin, sink) -> {
                    if (admin == null || !admin.getAuthority().equals("ADMIN")) {
                        sink.error(new InvalidCredentialsException());
                        return;
                    }

                    System.out.println(admin.getMfa_type());

                    sink.next(AdminDetails.builder()
                            .email(email)
                            .banned(admin.getIs_banned())
                            .role(admin.getAuthority())
                            .password(admin.getPassword())
                            .mfaType(admin.getMfa_type())
                            .build()
                    );

                });
    }

}
