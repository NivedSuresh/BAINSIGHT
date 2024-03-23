package org.exchange.library.Dto.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Enums.OrderType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZOrder {
//    @Column("order_id")
    private Long orderId;

//    @Column("guid")
    private UUID guid; /* Globally Unique Identifier */

//    @Column("exchange_order_id") /* ID given for the order from the Exchange */
    public String exchangeOrderId;

//    @Column("disclosed_quantity")
    private String disclosedQuantity;

//    @Column("validity")
    private String validity;

//    @Column("symbol")
    private String symbol; /* The symbol or ticker of the security being traded */

//    @Column("variety")
    private String orderVariety;

//    @Column("order_type")
    private OrderType orderType;

//    @Column("trigger_price")
    private String triggerPrice;

//    @Column("status_message")
    private String statusMessage; /* Any additional information about the order status. */

//    @Column("price")
    private String price;

//    @Column("status")
    private OrderStatus status; /* The current status of the order (e.g., open, pending, filled, cancelled). */

//    @Column("ucc")
    private String ucc;

//    @Column("exchange")
    private String exchange;

//    @Column("quantity")
    private long quantity; /* The amount of the security to be bought or sold. */

//    @Column("filled_quantity")
    private String filledQuantity;

//    @Column("pending_quantity")
    private String pendingQuantity;

//    @Column("order_timestamp")
    private LocalDateTime orderTimestamp; /* The time when the order was placed */

//    @Column("exchange_timestamp")
    private LocalDateTime exchangeTimestamp; /* The time when the order was received by the exchange. */

//    @Column("exchange_update_timestamp")
    private LocalDateTime exchangeUpdateTimestamp; /* The time of the last update received from the exchange. */

//    @Column("average_price")
    private String averagePrice;

//    @Column("transaction_type")
    private String transactionType; /* The type of transaction (buy or sell) */


//    @Column("parent_order_id")
    private String parentOrderId;

//    @Column("tag")
    private String tag;

//    @Column("validity_ttl")
    private int validityTTL;

//    @Column("meta")
    private Map<String, Object> meta;

//    @Column("auction_number")
    private String auctionNumber;
}
