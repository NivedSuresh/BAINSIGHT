package org.bainsight.watchlist.CandleStick.Data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.watchlist.CandleStick.Entity.CandleStick;
import org.exchange.library.Exception.NotFound.SymbolNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandleStickService {

    private final CandleStickRepo candleStickRepo;


    public List<CandleStick> fetchCurrentSymbolState(List<String> symbols, Short page, int count) {

        if(symbols.isEmpty()) return List.of();

        int start =  symbols.size() < count ? 0 : (page - 1) * count;
        int end = Math.min(start + count, symbols.size());

        symbols = symbols.subList(start, end);

        return this.candleStickRepo.findAllById(symbols);
    }

    public void checkIfStickValidElseThrow(String symbol) {
        if(!this.candleStickRepo.existsById(symbol)) throw new SymbolNotFoundException();
    }

}
