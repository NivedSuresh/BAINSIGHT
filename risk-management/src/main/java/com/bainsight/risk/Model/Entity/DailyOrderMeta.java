package com.bainsight.risk.Model.Entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@RedisHash
public class DailyOrderMeta {

    @Id
    private String ucc;
    private Integer openOrderCount;
    private Double totalAmountSpent;

}
