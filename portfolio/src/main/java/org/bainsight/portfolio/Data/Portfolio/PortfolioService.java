package org.bainsight.portfolio.Data.Portfolio;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.portfolio.Debug.Debugger;
import org.bainsight.portfolio.Exceptions.NotEnoughAvailableSharesToTradeException;
import org.bainsight.portfolio.Exceptions.SymbolNotFoundInPortfolioException;
import org.bainsight.portfolio.Mapper.Mapper;
import org.bainsight.portfolio.Model.Dto.PagedPortfolioSymbols;
import org.bainsight.portfolio.Model.Dto.PortfolioSymbolDto;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.bainsight.portfolio.Model.Entity.Portfolio;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.exchange.library.Dto.Utils.BainsightPage;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.KafkaEvent.RollbackEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {


    private final PortfolioRepo portfolioRepo;
    private final PortfolioSymbolRepo portfolioSymbolRepo;
    private final Debugger DEBUGGER;
    private final Mapper mapper;



    public Portfolio save(Portfolio portfolio){
        return this.portfolioRepo.save(portfolio);
    }


    /**
     * Called when Order is being processed.
     * */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updatePortfolioAfterBidMatch(final PortfolioUpdateRequest request){
        log.info("Portfolio Update Request: {}", request);
        PortfolioSymbol portfolioSymbol = this.fetchUserPortfolioSymbolForBid(request);

        long quantity = request.quantity() + portfolioSymbol.getQuantity();
        portfolioSymbol.setQuantity(quantity);

        double invested = portfolioSymbol.getInvestedAmount() + request.price();
        portfolioSymbol.setInvestedAmount(invested);

        this.portfolioSymbolRepo.save(portfolioSymbol);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    PortfolioSymbol fetchUserPortfolioSymbolForBid(PortfolioUpdateRequest request) {

        UUID unqiueId = UUID.fromString(request.ucc());

        return this.portfolioSymbolRepo.findByUccAndSymbolForUpdate(unqiueId, request.symbol()).orElseGet(() -> {
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


    @Transactional(propagation = Propagation.REQUIRED)
    public PortfolioSymbol fetchUserPortfolioSymbol(final String ucc, final  String symbol) {
        return this.portfolioSymbolRepo.findByUccAndSymbolForUpdate(UUID.fromString(ucc), symbol)
                .orElseThrow(SymbolNotFoundInPortfolioException::new);
    }


    @Transactional
    public void rollbackPortfolio(RollbackEvent riskRequest) {
        PortfolioUpdateRequest build = PortfolioUpdateRequest.builder()
                .symbol(riskRequest.getSymbol())
                .ucc(riskRequest.getUcc())
                .quantity(-riskRequest.getQuantity())
                .build();
        try{
            this.updatePortfolioAfterAsk(build);
        }
        catch (RuntimeException ex){
            if(ex instanceof GlobalException){
                /* TODO: IMPLEMENT LOGGING*/
            }
            throw ex;
        }
    }

    public List<PortfolioSymbol> fetchPortfolioSymbols(UUID ucc, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        return this.portfolioSymbolRepo.findByUcc(ucc, pageRequest).orElse(List.of());
    }

    public PagedPortfolioSymbols fetchPortfolioSymbolsAsPage(UUID ucc, Integer page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 5);
        Optional<Page<PortfolioSymbol>> byUccAsPage = this.portfolioSymbolRepo.findByUccAsPage(ucc, pageRequest);
        if(byUccAsPage.isEmpty()) return new PagedPortfolioSymbols(List.of(), new BainsightPage(page.shortValue(), false, page > 0));


        Page<PortfolioSymbol> portfolioSymbols = byUccAsPage.get();
        boolean hasPrev = portfolioSymbols.hasPrevious();
        boolean next = portfolioSymbols.hasNext();
        BainsightPage bainsightPage = new BainsightPage(page.shortValue(), next, hasPrev);

        List<PortfolioSymbolDto> portfolioSymbolDtos = portfolioSymbols.getContent().stream().map(mapper::toPortfolioSymbolDto).toList();
        return new PagedPortfolioSymbols(portfolioSymbolDtos, bainsightPage);
    }

    @Transactional
    public void resetOpenOrders() {
        this.portfolioSymbolRepo.resetOpenOrderCount();
    }
}
