package com.exchange.riskmanagement.Controller;

import com.exchange.riskmanagement.Service.SymbolService;
import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Symbol.SymbolRequest;
import org.exchange.library.Dto.Symbol.SymbolResponse;
import org.exchange.library.Mapper.ValidationErrorMapper;
import org.exchange.library.Utils.WebTrimmer;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bainsight/risk/admin/symbol")
public class AdminSymbolController {

    private final SymbolService symbolService;

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder binder) {
        WebTrimmer.setCustomEditorForWebBinder(binder);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Flux<SymbolResponse> getAllSymbols() {
        return symbolService.findAllSymbols();
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SymbolResponse> saveSymbol(@Validated @RequestBody SymbolRequest request) {
        return symbolService.saveSymbol(request)
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebExchangeBindException bindException) {
                        return Mono.error(ValidationErrorMapper.fetchFirstError(bindException));
                    }
                    return Mono.error(throwable);
                });
    }


}
