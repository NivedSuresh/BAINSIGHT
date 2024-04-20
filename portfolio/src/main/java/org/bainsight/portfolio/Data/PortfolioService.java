package org.bainsight.portfolio.Data;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.portfolio.Dubug.Debugger;
import org.bainsight.portfolio.Exceptions.NotEnoughAvailableSharesToTradeException;
import org.bainsight.portfolio.Exceptions.SymbolNotFoundInPortfolioException;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.bainsight.portfolio.Model.Entity.Portfolio;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.exchange.library.Exception.BadRequest.InvalidStateException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {


    private final PortfolioRepo portfolioRepo;
    private final PortfolioSymbolRepo portfolioSymbolRepo;
    private final Debugger DEBUGGER;



    /**
     * Called when Order is being processed.
     * */
    public void updatePortfolioAfterAsk(final PortfolioUpdateRequest request){
        PortfolioSymbol portfolioSymbol = this.fetchUserPortfolioSymbol(request.ucc(), request.symbol());

        DEBUGGER.DEBUG(log, "PortfolioSymbol has been fetched: {}", portfolioSymbol);

        long openQuantity = portfolioSymbol.getOpenQuantity() + request.quantity();

        if(portfolioSymbol.getQuantity() - openQuantity < 0){
            DEBUGGER.DEBUG(log, "Open Quantity cannot be greater than quantity thus the order is being cancelled. Open Quantity:{} - Quantity: {}", openQuantity, portfolioSymbol.getQuantity());
            throw new NotEnoughAvailableSharesToTradeException();
        }

        portfolioSymbol.setOpenQuantity(openQuantity);
        this.portfolioSymbolRepo.save(portfolioSymbol);
        DEBUGGER.DEBUG(log, "Open quantity has been updated the new open quantity is {}.", openQuantity);
    }



    /**
     * Invoked when a sell request gets matched
     * */
    public void updatePortfolioAfterAskMatch(final PortfolioUpdateRequest request){
        PortfolioSymbol portfolioSymbol = this.fetchUserPortfolioSymbol(request.ucc(), request.symbol());

        long quantity = portfolioSymbol.getQuantity() - request.quantity();
        if(quantity < 0) throw new NotEnoughAvailableSharesToTradeException();

        long openQuantity = portfolioSymbol.getOpenQuantity() - request.quantity();
        if(openQuantity < 0) throw new NotEnoughAvailableSharesToTradeException();

        portfolioSymbol.setOpenQuantity(openQuantity);
        portfolioSymbol.setQuantity(quantity);
        portfolioSymbol.setSoldAmount(portfolioSymbol.getSoldAmount() + request.price());

        this.portfolioSymbolRepo.save(portfolioSymbol);
    }



    /**
     * Is evoked when a bid is matched.
     * */
    public void updatePortfolioAfterBidMatch(final PortfolioUpdateRequest request){
        PortfolioSymbol portfolioSymbol = this.fetchUserPortfolioSymbolForBid(request);

        long quantity = request.quantity() + portfolioSymbol.getQuantity();
        portfolioSymbol.setQuantity(quantity);

        double invested = portfolioSymbol.getInvestedAmount() + request.price();
        portfolioSymbol.setInvestedAmount(invested);

        this.portfolioSymbolRepo.save(portfolioSymbol);
    }

    private PortfolioSymbol fetchUserPortfolioSymbolForBid(PortfolioUpdateRequest request) {
        UUID unqiueId = UUID.fromString(request.ucc());

        return this.portfolioRepo.fetchPortfolioSymbol(unqiueId, request.symbol()).orElseGet(() -> {
            Portfolio portfolio = this.fetchUserPortfolio(request.ucc());

            PortfolioSymbol portfolioSymbol = PortfolioSymbol.builder()
                    .soldAmount(0.0)
                    .investedAmount(0.0)
                    .symbol(request.symbol())
                    .portfolio(portfolio)
                    .openQuantity(0L)
                    .quantity(0L)
                    .build();

            return this.portfolioSymbolRepo.save(portfolioSymbol);
        });
    }


    public Portfolio fetchUserPortfolio(final String ucc){

        final UUID uniqueClientId = UUID.fromString(ucc);

        return this.portfolioRepo.findByUcc(uniqueClientId).orElseGet(() -> {

            Portfolio portfolio = new Portfolio();
            portfolio.setUcc(uniqueClientId);
            portfolio.setPortfolioSymbols(new ArrayList<>());

            return portfolioRepo.save(portfolio);
        });
    }


    public PortfolioSymbol fetchUserPortfolioSymbol(final String ucc, final  String symbol) {
        return this.portfolioRepo.fetchPortfolioSymbol(UUID.fromString(ucc), symbol)
                .orElseThrow(SymbolNotFoundInPortfolioException::new);
    }

}
