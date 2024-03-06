package org.exchange.order.Controller;


import lombok.RequiredArgsConstructor;
import org.exchange.order.Service.PersistentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bainsight/persist")
public class OrderController {

    private final PersistentService persistentService;

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Mono<Void>> cancelOrder(@RequestHeader("x-auth-user-id") String broker,
                                                  @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(persistentService.cancelOrder(broker, id));
    }


}
