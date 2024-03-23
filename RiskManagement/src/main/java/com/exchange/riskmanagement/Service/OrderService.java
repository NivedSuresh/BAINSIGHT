package com.exchange.riskmanagement.Service;

import org.exchange.library.Dto.Order.OrderRequest;
import org.exchange.library.Dto.Order.RiskOrderResponse;
import reactor.core.publisher.Mono;



//i.  Price Check: This means that when brokers use algorithms to place orders, they
//    must ensure that these orders are within the acceptable marketPrice ranges set by the
//    stock exchange for each security. They can't place orders at prices that are too
//    high or too low compared to the current market prices.
//
//ii. Quantity Check: Brokers need to make sure that the quantity of shares they're
//    ordering through algorithms doesn't exceed the limits set by the stock exchange.
//    This prevents brokers from placing excessively large orders that could disrupt the market.
//
//iii.Order Value Check: Similar to the quantity check, brokers must also ensure
//    that the total value of the orders placed through algorithms doesn't exceed
//    certain limits defined by the stock exchange. This helps control the size of orders
//    and reduces the risk of large market moves due to algorithmic trading.
//
//iv. Cumulative Open Order Value Check: Brokers may set limits on the total value
//    of unexecuted orders (orders that have been placed but not yet filled) for each client.
//    This helps manage the risk exposure of individual clients and prevents them from
//    accumulating too many open orders.
//
//v.  Automated Execution Check: Algorithms must keep track of all the orders they've placed,
//    whether they've been executed (filled), are still pending (unexecuted), or haven't been
//    confirmed yet. Additionally, algorithms must have safeguards in place to automatically stop
//    trading if they encounter any unexpected situations, such as placing orders in a loop or
//    excessively rapid trading.
//
//vi. Order Tagging: Each order placed through algorithms must be marked with a unique identifier
//    provided by the stock exchange. This helps regulators track and audit algorithmic trading
//    activity to ensure compliance with regulations and detect any irregularities or abuses.
public interface OrderService {
    Mono<? extends RiskOrderResponse> createOrder(OrderRequest orderRequest, String email);
}
