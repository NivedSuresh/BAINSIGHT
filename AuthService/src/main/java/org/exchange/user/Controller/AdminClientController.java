package org.exchange.user.Controller;


import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Client.ClientDto;
import org.exchange.library.Exception.BadRequest.NotImplementedException;
import org.exchange.library.Utils.WebTrimmer;
import org.exchange.user.Model.PrincipalRevoked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bainsight/admin/client")
public class AdminClientController {


    @InitBinder
    public void removeWhiteSpaces(WebDataBinder binder) {
        WebTrimmer.setCustomEditorForWebBinder(binder);
    }


    @PutMapping("/revoke/{ucc}")
    public ResponseEntity<Mono<ClientDto>> revokeClient(@PathVariable UUID ucc) {
        throw new NotImplementedException();
    }
}
