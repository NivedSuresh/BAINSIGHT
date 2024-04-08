package org.exchange.user.Security.Authentication.Admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Exception.Authentication.BadBindException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import reactor.core.publisher.Mono;


//@Primary
//@Component(value = "ADMIN_AUTH_MANAGER")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthenticationManager implements ReactiveAuthenticationManager {

    private final AdminDetailsService adminDetailsService;
    private final PasswordEncoder encoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return adminDetailsService.findByUsername(authentication.getName())
                .switchIfEmpty(Mono.error(BadBindException::new))
                .handle((adminDetails, sink) -> {
                    if (!encoder.matches(authentication.getCredentials().toString(), adminDetails.getPassword())) {
                        sink.error(new BadBindException());
                        log.error("BadRequest Password entry");
                        return;
                    }
                    //Will verify if account is banned/revoked
                    try {
                        new AccountStatusUserDetailsChecker().check(adminDetails);
                    } catch (Exception e) {
                        sink.error(new BadBindException("License expired/revoked!"));
                        return;
                    }
                    log.info("Authentication object has been returned");
                    sink.next(new PreAuthenticatedAuthenticationToken(
                            adminDetails, authentication.getCredentials(), adminDetails.getAuthorities()
                    ));
                });
    }
}
