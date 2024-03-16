package org.exchange.user.Controller;

import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Authentication.JwtResponse;
import org.exchange.user.Utils.CookieUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RequestMapping("/api/bainsight/logout")
@RestController
@RequiredArgsConstructor
public class LogoutController {

    private final CookieUtils cookieUtils;

    @GetMapping
    public ResponseEntity<Void> logoutUser(ServerWebExchange webExchange){
        cookieUtils.bakeCookies(webExchange, new JwtResponse("", "", ""));
        return ResponseEntity.ok().build();
    }


}
