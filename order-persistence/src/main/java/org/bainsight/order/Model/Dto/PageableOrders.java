package org.bainsight.order.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.exchange.library.Dto.Utils.BainsightPage;

import java.util.List;

@AllArgsConstructor
@Data
public class PageableOrders {
    private List<OrderDto> orders;
    private BainsightPage page;
}
