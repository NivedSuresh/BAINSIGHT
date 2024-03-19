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
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@Component
class MarketDataEngine {
    private final MarketData marketData;
    private final Gson gson;
    private final MulticastSocket socket;
    private AtomicLong sequence = new AtomicLong(0);

    private final Queue<Tick> backupBuffer;
    public MarketDataEngine(final MarketData marketData,
                            final Gson gson, MulticastSocket socket) {
        this.marketData = marketData;
        this.gson = gson;
        this.socket = socket;
        this.backupBuffer = new ArrayBlockingQueue<>(1000);
    }

    public void sendUpdates(boolean primary) throws InterruptedException {
        if(primary && !marketData.getDequeA().isEmpty()) bufferA();
        else if(primary && !marketData.getDequeB().isEmpty()) bufferB();
        else if(!primary) bufferBackup();
    }

    private void bufferBackup() throws InterruptedException {
        while (!backupBuffer.isEmpty()){
            Tick tick = backupBuffer.poll();
            if(tick == null) return;
            Thread.sleep(10);
            multicastTick(tick);
        }
        System.out.println("Backup buffer size after polling : " + backupBuffer.size());
    }

    private void bufferA() throws InterruptedException {
        int count = 0;
        int random = (int) (Math.random() * 100) + 1;
        while (!marketData.getDequeA().isEmpty()){
            TickerEx meta = marketData.getDequeA().poll();
            Tick tick = marketData.getRandomTick(meta, PushTo.QUEUE_B, getExchange());
            count = updateBufferAndBookThenSend(count, random, tick);
        }
    }

    private void bufferB() throws InterruptedException {
        int count = 0;
        int random = new Random().nextInt(50, 100);
        while (!marketData.getDequeB().isEmpty()){
            TickerEx meta = marketData.getDequeB().poll();
            Tick tick = marketData.getRandomTick(meta, PushTo.QUEUE_A, getExchange());
            count = updateBufferAndBookThenSend(count, random, tick);
        }
    }

    private int updateBufferAndBookThenSend(int count, int random, Tick tick) throws InterruptedException {
        backupBuffer.offer(tick);
        this.marketData.getOrderBookSim().set(count, tick);
        if(++count % random == 0) {
            System.out.println("Skipped : " + tick.getExchange() + " - " + tick.getSequenceNumber());
            return count;
        }
        Thread.sleep(10);
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



    private String getExchange(){
        return this.sequence.incrementAndGet() % 2 == 0 ? "NSE" : "BSE";
    }

}
