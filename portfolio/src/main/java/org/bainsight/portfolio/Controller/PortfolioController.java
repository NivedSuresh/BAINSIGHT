package org.bainsight.portfolio.Controller;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.bainsight.portfolio.Data.Portfolio.PortfolioService;
import org.bainsight.portfolio.Model.Dto.PagedPortfolioSymbols;
import org.exchange.library.Exception.IO.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bainsight/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @CircuitBreaker(name = "portfolio-service")
    @Retry(name = "portfolio-service", fallbackMethod = "fallback")
    public PagedPortfolioSymbols getPortfolio(@RequestHeader("x-auth-user-id") String ucc,
                                              @RequestParam(value = "page", required = false) Integer page){
        if(page == null) page = 1;
        return this.portfolioService.fetchPortfolioSymbolsAsPage(UUID.fromString(ucc), page);
    }

    public PagedPortfolioSymbols fallback(String ucc, Integer page, Throwable throwable){
        System.out.println("Inside");
        throw new ServiceUnavailableException();
    }


}
