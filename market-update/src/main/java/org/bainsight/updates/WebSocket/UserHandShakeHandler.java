package org.bainsight.updates.WebSocket;

import com.sun.security.auth.UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;



@RequiredArgsConstructor
@Component
@Slf4j
public class UserHandShakeHandler extends DefaultHandshakeHandler {


    private final JwtDecoder jwtDecoder;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        List<String> cookiesAsString = request.getHeaders().get(HttpHeaders.COOKIE);

        if(cookiesAsString == null) return new AnonymousAuthenticationToken("", null, null);

        final String key = "ACCESS_TOKEN";

        for(String str: cookiesAsString){
            if(str.startsWith(key))
            {
                if(str.length() < 14) new AnonymousAuthenticationToken("", null, null); /* Avoid exception */

                try
                {
                    String token = str.substring(13);
                    Jwt jwt = jwtDecoder.decode(token);
                    log.info("User connected: {}",jwt.getSubject());
                    return new UserPrincipal(jwt.getSubject());
                }
                catch (JwtException ex)
                {
                    return new AnonymousAuthenticationToken("", null, null);
                }
            }
        }

        return new AnonymousAuthenticationToken("", null, null);
    }
}
