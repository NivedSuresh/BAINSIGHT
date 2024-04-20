package org.exchange.library.KafkaEvent;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DailyOrderMetaEvent {

    private String ucc;
    private Double totalAmountSpent;

}
