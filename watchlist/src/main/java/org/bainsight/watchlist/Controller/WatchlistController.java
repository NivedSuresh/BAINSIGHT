package org.bainsight.watchlist.Controller;

import lombok.RequiredArgsConstructor;
import org.bainsight.watchlist.CandleStick.Data.CandleStickService;
import org.bainsight.watchlist.CandleStick.Entity.CandleStick;
import org.bainsight.watchlist.Mapper.Mapper;
import org.bainsight.watchlist.Payload.AddToWatchlist;
import org.bainsight.watchlist.Payload.CreateWatchlist;
import org.bainsight.watchlist.Payload.RemoveSymbol;
import org.bainsight.watchlist.Payload.WatchlistDto;
import org.bainsight.watchlist.Watchlist.Data.WatchlistService;
import org.bainsight.watchlist.Watchlist.Model.Watchlist;
import org.bainsight.watchlist.Watchlist.Model.WatchlistMeta;
import org.exchange.library.Dto.Utils.Page;
import org.exchange.library.Utils.WebTrimmer;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bainsight/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final Mapper mapper;
    private final CandleStickService candleStickService;


    @InitBinder
    public void removeWhiteSpaces(WebDataBinder binder) {
        WebTrimmer.setCustomEditorForWebBinder(binder);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public WatchlistDto fetchPinnedWatchlistForUser(@RequestHeader("x-auth-user-id") final String ucc,
                                                    @RequestParam(value = "page", required = false) Short page,
                                                    @RequestParam(value = "count", required = false) Short count) {

        System.out.println("Page: " + page + " Count: " + count);

        if(page == null) page = 1;
        if(count == null) count = 5;

        Watchlist watchlist = this.watchlistService.findPinnedWatchListByUCC(ucc);

        if(watchlist.getSymbols().isEmpty())
        {
            return mapper.toWatchlistDto(watchlist, new ArrayList<>(), new Page((short) 1, false, false));
        }
        List<CandleStick> candleSticks = this.candleStickService.fetchCurrentSymbolState(watchlist.getSymbols(), page, count);

        Page pageable = this.mapper.getPageable(page, count, watchlist.getSymbols().size());


        return mapper.toWatchlistDto(watchlist, candleSticks, pageable);
    }


    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistDto addToWatchlist(@RequestHeader("x-auth-user-id") final String ucc,
                                       @Validated @RequestBody final AddToWatchlist request,
                                       @RequestParam(required = false) final Short page){

        this.candleStickService.checkIfStickValidElseThrow(request.symbol());

        Watchlist watchlist = this.watchlistService.computeWatchlistIfAbsent(ucc, request);

        List<CandleStick> candleSticks = this.candleStickService.fetchCurrentSymbolState(watchlist.getSymbols(), page, 10);

        Page pageable = new Page(page, page * 5 < watchlist.getSymbols().size(), page > 1);
        return mapper.toWatchlistDto(watchlist, candleSticks, pageable);
    }


    @PutMapping("/pin")
    @ResponseStatus(HttpStatus.OK)
    public void pinWatchlist(@RequestHeader("x-auth-user-id") final String ucc,
                             @RequestParam final Long id){

        this.watchlistService.updateCurrentlyPinned(ucc, id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistDto createWatchlist(@RequestHeader("x-auth-user-id") final String ucc,
                                        @Validated @RequestBody final CreateWatchlist request){

        Watchlist watchlist = this.watchlistService.createNewWatchlist(ucc, request.watchlistName());

        Page pageable = new Page((short) 1, false, false);
        return mapper.toWatchlistDto(watchlist, new ArrayList<>(), pageable);
    }


    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteFromWatchlist(@RequestHeader("x-auth-user-id") final String ucc,
                                    @Validated @RequestBody RemoveSymbol request){
        this.watchlistService.watchlistExistsByUccAndId(ucc, request.watchlistId(), true);
        this.watchlistService.removeSymbolFromWatchlist(request);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/meta")
    public WatchlistMeta fetchWatchlistWithTags(@RequestHeader("x-auth-user-id") final String ucc,
                                                @RequestParam(value = "page", required = false) Short page,
                                                @RequestParam(value = "tag", required = false) final String watchlistName,
                                                @RequestParam(value = "count", required = false) Short count){

        if(page == null) page = 1;
        if(count == null) count = 10;

        List<String> tags = this.watchlistService.fetchAllWatchlistNamesForUcc(ucc);

        if(watchlistName == null)
        {
            WatchlistDto watchlistDto = this.fetchPinnedWatchlistForUser(ucc, page, count);
            return new WatchlistMeta(watchlistDto, tags);
        }

        if(tags.isEmpty())
        {
            return new WatchlistMeta(null, null);
        }

        Watchlist watchlist = this.watchlistService.findWatchlistByUccAndName(ucc, watchlistName);

        List<CandleStick> sticks = this.candleStickService.fetchCurrentSymbolState(watchlist.getSymbols(), page, 10);

        Page pageable = this.mapper.getPageable(page, 10, watchlist.getSymbols().size());
        WatchlistDto watchlistDto = this.mapper.toWatchlistDto(watchlist, sticks, pageable);

        return new WatchlistMeta(watchlistDto, tags);
    }
}
