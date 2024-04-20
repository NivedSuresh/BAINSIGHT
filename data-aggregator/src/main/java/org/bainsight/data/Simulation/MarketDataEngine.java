package org.bainsight.data.Simulation;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bainsight.data.Listener.MessageReceiveBuffer;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

@Component
class MarketDataEngine {
    private final MarketData marketData;
    private final MessageReceiveBuffer receiveBuffer;
    private final Gson gson;


    private final Queue<Tick> backupBuffer;
    public MarketDataEngine(final MarketData marketData,
                            final MessageReceiveBuffer receiveBuffer) {
        this.marketData = marketData;
        this.receiveBuffer = receiveBuffer;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        this.backupBuffer = new ArrayBlockingQueue<>(1000);
    }


    public void sendUpdates(boolean primary) {
        if(primary && !marketData.getDequeA().isEmpty()) bufferA();
        else if(primary && !marketData.getDequeB().isEmpty()) bufferB();
        else if(!primary) bufferBackup();
    }

    private void bufferBackup() {
        while (!backupBuffer.isEmpty()){
            Tick tick = backupBuffer.poll();
            if(tick == null) return;
            multicastTick(tick);
        }
    }

    private void bufferA(){

        int count = 0;
        int random = (int) (Math.random() * 100) + 1;
        while (!marketData.getDequeA().isEmpty()){
            TickerEx meta = marketData.getDequeA().poll();
            Tick tick = marketData.getRandomTick(meta, PushTo.QUEUE_B, getExchange());
            count = backupAndMulticast(count, random, tick);
        }
    }


    public void bufferB() {

        int count = 0;
        int random = new Random().nextInt(50, 100);
        while (!marketData.getDequeB().isEmpty()){
            TickerEx meta = marketData.getDequeB().poll();
            Tick tick = marketData.getRandomTick(meta, PushTo.QUEUE_A, getExchange());

            count = backupAndMulticast(count, random, tick);
        }
    }

    private int backupAndMulticast(int count, int random, Tick tick){
        backupBuffer.offer(tick);
        List<Tick> orderBook = this.marketData.getExchangeSpecificOrderBook().get(tick.getExchange());


        orderBook.set(count, tick);
        if(++count % random == 0) return count;
        multicastTick(tick);
        return count;
    }



    private void multicastTick(Tick tick) {
        String json = gson.toJson(tick);
        this.receiveBuffer.offer(json.getBytes());
    }

    private final Random random = new Random();

    private String getExchange(){
        int random = this.random.nextInt(1, 3);
        return random % 2 == 0 ? "NSE" : "BSE";
    }


}


