package org.exchange.order.Controller;


import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Order.OrderDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequestMapping("/api/bainsight/persist/admin/order")
@RestController
@RequiredArgsConstructor
public class AdminOrderController {
}
