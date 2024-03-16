package org.bainsight.liquidity.Listener;


import org.slf4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

interface GroupListener {
    default void listenToGroup(MulticastSocket socket, InetAddress group, byte[] buffer, MessageBuffer messageBuffer, Logger log, boolean isPrimary) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        System.out.println(isPrimary);
        while (!Thread.currentThread().isInterrupted()) {
            try{
                socket.receive(packet);
                messageBuffer.offer(packet.getData(), isPrimary);
            }catch(IOException e){
                log.error("Failed to receive packets");
            }
        }
        socket.leaveGroup(group);
    }
}
