package com.bainsight.risk.Data;


import com.bainsight.risk.Model.Entity.CandleStick;
import lombok.RequiredArgsConstructor;
import org.exchange.library.Exception.NotFound.SymbolNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandleStickService {

    private final CandleStickRepo candleStickRepo;


    public CandleStick fetchCandleStick(String symbol) {
        if(symbol == null) throw new SymbolNotFoundException();
        symbol = symbol.toUpperCase();
        return this.candleStickRepo.findById(symbol).orElseThrow(SymbolNotFoundException::new);
    }

}
