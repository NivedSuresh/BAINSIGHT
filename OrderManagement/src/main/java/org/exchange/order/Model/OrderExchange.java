package org.exchange.order.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_exchanges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderExchange {
    @Id
    private Long id;
    private String exchange;
    private Long quantity;
}
