package org.exchange.user.Controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Utils.WebTrimmer;
import org.exchange.user.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/bainsight/token")
public class TokenController {

    private final AuthService authService;

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder binder) {
        WebTrimmer.setCustomEditorForWebBinder(binder);
    }

    @GetMapping
    public ResponseEntity<Mono<String>> renewJwt(@RequestHeader("Authorization") String refreshToken) {
        log.info("Inside renew method controller");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.renewJwt(refreshToken));
    }

}
