package com.exchange.riskmanagement.Service.impls;

import com.exchange.riskmanagement.Mapper.Mapper;
import com.exchange.riskmanagement.Repository.Symbol;
import com.exchange.riskmanagement.Service.SymbolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Dto.Symbol.SymbolRequest;
import org.exchange.library.Dto.Symbol.SymbolResponse;
import org.exchange.library.Exception.BadRequest.EntityAlreadyExistsException;
import org.exchange.library.Exception.IO.ConnectionFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SymbolServiceImpl implements SymbolService {

    private final Symbol symbol;
    private final Mapper mapper;

    @Override
    public Flux<SymbolResponse> findAllSymbols() {
        return symbol.findAllFromDB()
                .onErrorResume(throwable ->
                        Mono.error(new ConnectionFailureException())
                );
    }

    @Override
    public Mono<SymbolResponse> saveSymbol(SymbolRequest request) {
        return symbol.save(mapper.toSymbolMeta(request))
                .map(mapper::toSymbolResponse)
                .onErrorResume(e -> {
                    log.error("Exception caught : {}", e.getMessage());
                    if (e instanceof DuplicateKeyException)
                        return Mono.error(new EntityAlreadyExistsException(
                                request.getTradingSymbol(), "Symbol"
                        ));
                    return Mono.error(new ConnectionFailureException());
                });
    }
}
