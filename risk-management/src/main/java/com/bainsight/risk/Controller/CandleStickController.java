package com.bainsight.risk.Controller;

import com.bainsight.risk.Data.CandleStickService;
import com.bainsight.risk.Mapper.Mapper;
import com.bainsight.risk.Model.Entity.CandleStick;
import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Symbol.CandleStickDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bainsight/risk")
public class CandleStickController {

    private final CandleStickService candleStickService;
    private final Mapper mapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stick/{symbol}")
    public CandleStickDto fetchCandleStick(@PathVariable final String symbol){
        CandleStick candleStick = this.candleStickService.fetchCandleStick(symbol);
        System.out.println(candleStick);
        return this.mapper.toCandleStickDto(candleStick);
    }


}
