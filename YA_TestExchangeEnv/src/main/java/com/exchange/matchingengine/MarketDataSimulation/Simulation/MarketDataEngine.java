package com.exchange.matchingengine.MarketDataSimulation.Simulation;


import com.exchange.matchingengine.MarketDataSimulation.Enums.PushTo;
import com.exchange.matchingengine.MarketDataSimulation.Models.TickerEx;
import com.google.gson.Gson;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.ZoneId;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@Component
class MarketDataEngine {
    private final MarketData marketData;
    private final Gson gson;
    private final MulticastSocket socket;
    private final ZoneId zoneId = ZoneId.of("Asia/Kolkata");
    private final AtomicLong sequence = new AtomicLong(0);

    private final Queue<Tick> backupBuffer;
    public MarketDataEngine(final MarketData marketData,
                            final Gson gson, MulticastSocket socket) {
        this.marketData = marketData;
        this.gson = gson;
        this.socket = socket;
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
        if(++count % random == 0) {
            System.out.println(tick.getKey() + " - " + tick.getSequenceNumber());
            return count;
        }
        multicastTick(tick);
        return count;
    }



    private void multicastTick(Tick tick) {
        try{
            String jsonifiedTick = gson.toJson(tick);
            InetAddress group = InetAddress.getByName("230.0.0.1");
            int port = 5000;

            byte[] buffer = jsonifiedTick.getBytes();


            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
            socket.send(packet);
        }
        catch(IOException e){ e.printStackTrace();}
    }

    private final Random random = new Random();

    private String getExchange(){
        int random = this.random.nextInt(1, 3);
        return random % 2 == 0 ? "NSE" : "BSE";
    }


}


