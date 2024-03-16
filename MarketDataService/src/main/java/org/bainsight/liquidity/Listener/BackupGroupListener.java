package org.bainsight.liquidity.Listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

@RequiredArgsConstructor
@Slf4j
public class BackupGroupListener implements Runnable{
    private final MessageBuffer messageBuffer;
    private final String backupMulticastAddress;
    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(5000)){
            InetAddress group = InetAddress.getByName(backupMulticastAddress);
            socket.joinGroup(group);/*
                Payload size per second = Number of objects per second * Size of each object
                ie: 4000 * 233 (ESTIMATED SIZE OF TICK) = 932_000 bytes per second

                Memory consumption (MB) = 932,000 bytes / (1024 * 1024) ≈ 0.889 MB
            */
            byte[] buffer = new byte[932_000];

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (!Thread.currentThread().isInterrupted()) {
                try{
                    socket.receive(packet);
                    messageBuffer.offer(packet.getData(), false);
                }catch(IOException e){
                    log.error("Failed to receive packets");
                }
            }
            socket.leaveGroup(group);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

}
