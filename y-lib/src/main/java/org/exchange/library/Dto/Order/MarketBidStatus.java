package org.exchange.library.Dto.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.exchange.library.Enums.MatchStatus;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MarketBidStatus {

    private long matchId;

    private MatchStatus matchStatus;


}
