package org.bainsight.portfolio.Controller;


import lombok.RequiredArgsConstructor;
import org.bainsight.portfolio.Data.PortfolioService;
import org.bainsight.portfolio.Mapper.Mapper;
import org.bainsight.portfolio.Model.Dto.PortfolioDto;
import org.bainsight.portfolio.Model.Entity.Portfolio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bainsight/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final Mapper mapper;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public PortfolioDto getPortfolio(@RequestHeader("x-auth-user-id") String ucc){
        Portfolio portfolio = this.portfolioService.fetchUserPortfolio(ucc);
        return mapper.portfolioEntityToDto(portfolio);
    }

}
