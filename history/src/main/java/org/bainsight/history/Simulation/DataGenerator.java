package org.bainsight.history.Simulation;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.history.Data.HistoryServiceImpl;
import org.bainsight.history.Models.Entity.CandleStickEntity;
import org.exchange.library.Exception.BadRequest.InvalidStateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;


/**
 * The market is open from 9:00 AM to 3:30 PM. If a snapshot is taken
 * every minute during market hours, that means there would be 390 snapshots each day.
 * <p>
 * There are approximately 3,000 tradable tickers, and each one is
 * around 100 bytes in size.
 * <p>
 * We need to store this data for five years.
 * <p>
 * Total storage required: 3,000 tickers/snapshot * 390 snapshots/day * 365 days/year * 5 years * 100 bytes/ticker = 198.86 GB (approximately)
 */

@Component
@Slf4j
@EnableScheduling
@Profile("sim")
class DataGenerator {


    @Getter(AccessLevel.PACKAGE)
    private final Queue<CandleStickEntity> entities = new LinkedList<>();

    private final List<String> symbols;

    private final HistoryServiceImpl historyService;


    private final byte  DAYS_TO_SKIP_FOR_5Y = 25,
                        DAYS_TO_SKIP_FOR_3Y = 15,
                        DAYS_TO_SKIP_FOR_1Y = 5,
                        HOURS_TO_SKIP_FOR_1M = 2,
                        MINUTES_TO_SKIP_FOR_5D = 30,
                        MINUTES_TO_SKIP_FOR_1D = 5;


    private static final Random random = new Random();

    private static final LocalDateTime NOW;

    static
    {
        LocalDateTime now = LocalDateTime.now();
        if(now.getDayOfWeek() == DayOfWeek.SUNDAY){
            NOW = LocalDateTime.now().minusDays(1);
        }
        else NOW = now;
    }


    DataGenerator(HistoryServiceImpl historyService, @Value("${history.symbols}") final List<String> symbols) {
        this.historyService = historyService;
        this.symbols = symbols;
    }



    @Scheduled(initialDelay = 10000)
    public void generate(){
        this.fillQueue();
        while(!this.entities.isEmpty()){
            CandleStickEntity stick = this.entities.poll();
            this.generateSticks(stick, MINUTES_TO_SKIP_FOR_1D, TimeUnit.MIN);
        }
    }

    private List<String> getSymbols(){
        return this.symbols;
    }
    private List<SymbolMeta> getSymbolMeta() {

        return getSymbols().stream()
                .map(s -> new SymbolMeta(s, random.nextDouble(0, 1), random.nextDouble(100, 200)))
                .toList();
    }

    public void fillQueue(){


        getSymbolMeta().forEach(meta -> {

            LocalDateTime before3years;
            CandleStickEntity candleStick = this.historyService.fetchLatestTimeStamp(meta.symbol());
            log.info("Fetched latest timestamp from repo: {}", candleStick);
            if(candleStick == null || candleStick.getKey() == null || candleStick.getKey().getTimestamp() == null)
            {
                log.info("Failed to find latest timestamp from repo, thus a complete write will be done.");
                ZonedDateTime mod = ZonedDateTime.of(
                        NOW.getYear() - 3, NOW.getMonthValue(),
                        NOW.getDayOfMonth(), 9,
                        0, 0, 0,
                        ZoneId.of("Asia/Kolkata")
                );
                before3years = mod.toLocalDateTime();
            }
            else
            {
                before3years = candleStick.getKey().getTimestamp();
                CandleStickEntity entity = this.historyService.findByKey(new CandleStickEntity.Key(meta.symbol(), before3years));
                this.entities.offer(entity);
                return;
            }

            if(before3years.isEqual(LocalDateTime.of(NOW.getYear(), NOW.getMonth(), NOW.getDayOfMonth(), 15, 30))||
               before3years.isAfter(LocalDateTime.of(NOW.getYear(), NOW.getMonth(), NOW.getDayOfMonth(), 15, 30))){
                log.info("No need to add any new sticks as DB is already upto date.");
                return;
            }


            long volume = random.nextLong(100000, 100000000);
            double open = meta.startPrice();
            double low = meta.startPrice() + random.nextDouble(-10, 0);
            double high = meta.startPrice() + random.nextDouble(0, 10);
            double close = (low + high) / 2;
            double change = close - open;

            CandleStickEntity.Key key = new CandleStickEntity.Key(meta.symbol(), before3years);


            CandleStickEntity entity = CandleStickEntity.builder()
                    .key(key)
                    .low(low)
                    .high(high)
                    .open(open)
                    .close(close)
                    .volume(volume)
                    .change(change)
                    .build();

            this.entities.offer(entity);
        });
    }



    public void generateSticks(CandleStickEntity entity, byte time, TimeUnit timeUnit){

        this.historyService.saveCandleStick(entity);
        int bullRun;
        while (entity.getKey().getTimestamp().isBefore(NOW))
        {
            bullRun = getBullRun(timeUnit);
            if(entity.getKey().getTimestamp().getDayOfWeek() == DayOfWeek.SUNDAY){
                LocalDateTime dateTime = entity.getKey().getTimestamp().plusDays(1);
                entity.getKey().setTimestamp(dateTime);
            }
            while (bullRun != 0 && entity.getKey().getTimestamp().isBefore(NOW))
            {
                try{ updateEntity(entity, bullRun > 0, time, timeUnit); }
                catch (InvalidStateException e){return;}

                   /* TODO TODO TODO TODO */
                if(entity.getKey().getTimestamp().getDayOfWeek() == DayOfWeek.SUNDAY){
                    LocalDateTime dateTime = entity.getKey().getTimestamp().plusDays(1);
                    entity.getKey().setTimestamp(dateTime);
                }
                if(entity.getKey().getTimestamp().isBefore(NOW))
                {
                    historyService.saveCandleStick(entity);
                }


                if(entity.getLow() < random.nextInt(50, 100)){
                    bullRun = 101;
                }
                bullRun = bullRun < 0 ? bullRun + 1 : bullRun - 1;
            }
        }
    }

    private int getBullRun(TimeUnit timeUnit) {
        switch (timeUnit)
        {
            case DAY -> { return random.nextInt(-20, 20); }
            case HOUR -> { return random.nextInt(-200, 200); }
            case MIN -> { return random.nextInt(-2000, 2000); }
            default -> { return 100;}
        }
    }

    private void updateEntity(CandleStickEntity entity, boolean bullRun, byte time, TimeUnit timeUnit) {

        double prevClose = entity.getClose();

        setTimeStamp(entity, time);


        double high = bullRun ? entity.getHigh() + random.nextDouble(-1, 2.8) :
                entity.getHigh() + random.nextDouble(-3, 1);

        double low = high == 0 ? 0 : high - random.nextDouble(4,10);
        entity.setLow(low);

        double open = (low + high) / 2 + random.nextDouble(2);
        double close = high - random.nextDouble(3);


        BigDecimal bigHigh = new BigDecimal(high).setScale(2, RoundingMode.DOWN);
        high = bigHigh.doubleValue();

        BigDecimal bigLow = new BigDecimal(low).setScale(2, RoundingMode.DOWN);
        low = bigLow.doubleValue();


        BigDecimal bigOpen = new BigDecimal(open).setScale(2, RoundingMode.DOWN);
        open = bigOpen.doubleValue();

        BigDecimal bigClose = new BigDecimal(close).setScale(2, RoundingMode.DOWN);
        close = bigClose.doubleValue();

        double change = close - prevClose;

        BigDecimal bigChange = new BigDecimal(change).setScale(2, RoundingMode.DOWN);
        change = bigChange.doubleValue();

        long volume = entity.getVolume() + random.nextLong(-10000, 10000);
        volume = Math.max(volume, random.nextInt(10000));

        entity.setLow(low);
        entity.setOpen(open);
        entity.setClose(close);
        entity.setHigh(high);
        entity.setVolume(volume);
        entity.setChange(change);

    }


    private void setTimeStamp(CandleStickEntity entity, byte time) {
        LocalDateTime current = entity.getKey().getTimestamp();
        LocalDateTime newTime = getNewTime(current, time);
        if(validTime(newTime)) entity.getKey().setTimestamp(newTime);
        else throw new InvalidStateException();
    }

    private LocalDateTime getNewTime(LocalDateTime current, byte time) {
        LocalDateTime newTime = current.plusMinutes(time);
        boolean closed = newTime.getHour() >= 15 && newTime.getMinute() > 30;
        if(closed){
            newTime = newTime.plusDays(1);
            newTime = LocalDateTime.of(newTime.getYear(), newTime.getMonth(), newTime.getDayOfMonth(), 9, 0, 0, 0);
        }
        return newTime;
    }



    private boolean validTime(LocalDateTime newTime) {
        return newTime.isBefore(NOW) || newTime.isEqual(NOW);
    }


}
