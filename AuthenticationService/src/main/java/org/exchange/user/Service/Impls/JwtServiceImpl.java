package org.exchange.user.Service.Impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Dto.Authentication.JwtResponse;
import org.exchange.user.Service.JwtService;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;

    @Override
    public Mono<String> generateJwt(String identifier, String authority, Instant expiry) {
        log.debug("Jwt generate method has been triggered!");

        //TODO: CHANGE EXPIRY BEFORE HOSTING
        JwtClaimsSet claimsSet = JwtClaimsSet.builder().issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1000, ChronoUnit.DAYS))
                .issuer("self")
                .subject(identifier)
                .claim("authority", authority)
                .build();

        log.debug("Mono<String> jwt has been returned from generateJwt()");
        return Mono.just(this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue());
    }

    @Override
    public Mono<JwtResponse> getAuthResponse(String ucc, String authority, Instant forAccess, Instant forRefresh) {
        return Mono.zip(
                generateJwt(ucc, authority, Instant.now().minus(50, ChronoUnit.SECONDS)),
                generateJwt(ucc, authority.concat("_REFRESH_TOKEN"), forRefresh)
        )
        .map(tokens ->  new JwtResponse(tokens.getT1(), tokens.getT2(), null));
    }
}
