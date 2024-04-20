package org.exchange.user.Service.Impls;

import org.exchange.library.Advice.Error;
import org.exchange.library.Enums.MfaType;
import org.exchange.library.Exception.BadRequest.InvalidStateException;
import org.exchange.user.Service.MfaService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MfaServiceImpl implements MfaService {
    @Override
    public Mono<String> switchAndStartMfa(MfaType mfaType, String name) {
        switch (mfaType) {
            case MfaType.EMAIL_OTP -> {
                return generateAndSendOtpToEmail(name);
            }
            case MfaType.GOOGLE_AUTHENTICATOR_OTP -> {
                return Mono.just("Please check your Authentication application for the code");
            }
            default -> throw new InvalidStateException(
                    "An error occurred will processing Multi Factor Authentication",
                    Error.MFA_INITIAL_FAILURE
            );
        }
    }

    private Mono<String> generateAndSendOtpToEmail(String name) {
        return Mono.just("Otp has been sent to ".concat(name));
    }
}
