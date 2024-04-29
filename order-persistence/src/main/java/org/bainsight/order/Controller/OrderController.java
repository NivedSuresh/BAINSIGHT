package org.bainsight.order.Controller;


import lombok.RequiredArgsConstructor;
import org.bainsight.order.GrpcService.OrderPersistance.GrpcOrderService;
import org.bainsight.order.Mapper.ModelMapper;
import org.bainsight.order.Model.Dto.PageableOrders;
import org.bainsight.order.Model.Entity.Order;
import org.bainsight.order.Model.Dto.OrderDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bainsight/persistence")
public class OrderController {


    private final GrpcOrderService orderService;
    private final ModelMapper mapper;

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<OrderDto> fetchOrders(@RequestHeader("x-auth-user-id") String ucc,
//                                      @RequestParam(value = "page", required = false) Integer page){
//        if(page == null) page = 0;
//        List<Order> orders = this.orderService.findOrdersByPageAndUcc(UUID.fromString(ucc), page);
//        return orders.stream().map(this.mapper::toOrderDto).toList();
//    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageableOrders fetchOrders(@RequestHeader("x-auth-user-id") String ucc,
                                      @RequestParam(value = "page", required = false) Integer page){
        if(page == null) page = 1;
        return this.orderService.findOrdersByPageAndUccWithPage(UUID.fromString(ucc), page);
    }


}
