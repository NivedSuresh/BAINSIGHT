package org.bainsight.history.Controller;


import lombok.RequiredArgsConstructor;
import org.bainsight.history.Data.HistoryServiceImpl;
import org.bainsight.history.Models.Dto.CandleStickDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bainsight/history")
public class HistoryController {

    private final HistoryServiceImpl historyService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{timeSpace}/{symbol}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CandleStickDto> getSticksForTheDay(@PathVariable String timeSpace,
                                                   @PathVariable String symbol) {

        symbol = symbol.toUpperCase();
        timeSpace = timeSpace.toUpperCase();

        if(timeSpace.equals("1D")) return this.historyService.fetchSticksForTheDay(symbol);
        else return this.historyService.fetchByTime(symbol, timeSpace);
    }



}
