package org.bainsight.liquidity.Listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.RingBuffer;
import jakarta.annotation.PostConstruct;
import org.bainsight.liquidity.Model.Events.TickReceivedEvent;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ExecutorService;

@Component
public class MessageReceiveBuffer {

    private static final int ORDER_BOOK_FETCH_RETRY_THRESHOLD = 2;
    private final ObjectMapper mapper;
    private final RingBuffer<TickReceivedEvent> receiverBuffer;
    private final WebClient webClient;
    private final ExecutorService orderBookExecutor;
    private final ExecutorService greenExecutor;

    @Value("${exchange.orderbook.url}")
    private String[] ORDER_BOOKS_URLS;

    MessageReceiveBuffer(final ObjectMapper mapper,
                         final RingBuffer<TickReceivedEvent> receiverBuffer,
                         final WebClient.Builder builder,
                         final ExecutorService orderBookExecutor,
                         final ExecutorService greenExecutor)
    {
        this.mapper = mapper;
        this.receiverBuffer = receiverBuffer;
        this.webClient = builder.build();
        this.orderBookExecutor = orderBookExecutor;
        this.greenExecutor = greenExecutor;

    }


    /**
    *  Exchange provides URI for the broker to fetch the order book in case
    *  if the broker joined the channel late.
    *  - The same URI can be used to restore state on Node restart.
    *  - FUNCTION RECURSIVELY RETRIES UNTIL IT REACHES THE RETRY THRESHOLD, ie 2 FTM
    * */
    /* TODO: ??FETCH CURRENT DAY ORDER-BOOK ON START OF TRADING DAY TO GET AN IDEA OF
    *   TRADABLE TICKERS??
    *   NB: EXCHANGE WILL PUBLISH THE TRADABLE TICKERS FOR THE DAY ANYHOW, CHECK IF NEEDED
    * */

    @PostConstruct
    public void fetchBookOnConstruct() {

        orderBookExecutor.execute(() -> {
            while (ORDER_BOOKS_URLS == null || ORDER_BOOKS_URLS.length == 0);

            for (int currentFetch = 0; currentFetch < ORDER_BOOKS_URLS.length; currentFetch++) {
                final int fetching = currentFetch;
                greenExecutor.execute(() -> fetchBookFrom(ORDER_BOOKS_URLS[fetching], 1));
            }

        });
    }

    private void fetchBookFrom(String orderBookUrl, int tryCount) {
        /* TODO: IMPLEMENT JOURNALING */
        if (tryCount > ORDER_BOOK_FETCH_RETRY_THRESHOLD) {
//            System.out.println("FAILED TO FETCH ORDER-BOOK FROM THE URL " + orderBookUrl + ". MAX RETRY LIMIT REACHED!");
            return;
        }
        this.webClient.get()
                .uri(orderBookUrl)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(byte[].class)
                .doOnError(throwable -> {
                    /* TODO: IMPLEMENT JOURNALING */
//                    System.out.println("EXCEPTION WHILE FETCHING ORDER_BOOK: TRY COUNT - " + tryCount + " FOR " + orderBookUrl + ". RETRYING THE SAME!");
                    fetchBookFrom(orderBookUrl, tryCount + 1);
                })
                .onErrorResume(throwable -> Mono.empty())
                .subscribe(this::offer);
    }





    /**
     *  The messages are serialized and added to the RingBuffer.
     *  Messages are verified based on {ticker: last received sequence number}
     *  and are only persisted if:
     *  the newly received sequence number for the symbol:exchange
     *                         >
     *  last persisted sequence number for the symbol:exchange
     *
     *  ie:
     *      if {NSE:AAPL - 1987346 } > {NSE:AAPL - 1782906} PERSIST
     *      else IGNORE
     * */
    public void offer(byte[] data)
    {
        receiverBuffer.publishEvent((event, seq) -> {
            Tick tick = deserialize(data);
            if(tick == null) return;
            event.setTick(tick);
        });
    }

    /* TODO: CONVERT TO KYRO AS JACKSON IS EXTREMELY SLOW ACCORDING TO PROFILER */
    private Tick deserialize(byte[] bytes)
    {
        try{ return mapper.readValue(bytes, Tick.class); }
        catch (Exception e){ return null; }
    }


}
