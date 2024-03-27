package org.exchange.user.Service;

import org.exchange.library.Enums.MfaType;
import reactor.core.publisher.Mono;

public interface MfaService {
    Mono<String> switchAndStartMfa(MfaType getMfaType, String name);
}
