package org.bainsight.portfolio.Controller;


import lombok.RequiredArgsConstructor;
import org.bainsight.portfolio.Data.Portfolio.PortfolioService;
import org.bainsight.portfolio.Mapper.Mapper;
import org.bainsight.portfolio.Model.Dto.PagedPortfolioSymbols;
import org.bainsight.portfolio.Model.Dto.PortfolioDto;
import org.bainsight.portfolio.Model.Dto.PortfolioSymbolDto;
import org.bainsight.portfolio.Model.Entity.Portfolio;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bainsight/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final Mapper mapper;


//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping
//    public List<PortfolioSymbolDto> getPortfolio(@RequestHeader("x-auth-user-id") String ucc,
//                                                 @RequestParam(value = "page", required = false) Integer page){
//        if(page == null) page = 0;
//        List<PortfolioSymbol> portfolioSymbols = this.portfolioService.fetchPortfolioSymbols(UUID.fromString(ucc), page);
//
//        return portfolioSymbols.stream().map(mapper::toPortfolioSymbolDto).toList();
//    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public PagedPortfolioSymbols getPortfolio(@RequestHeader("x-auth-user-id") String ucc,
                                              @RequestParam(value = "page", required = false) Integer page){
        if(page == null) page = 1;
        return this.portfolioService.fetchPortfolioSymbolsAsPage(UUID.fromString(ucc), page);
    }

}
