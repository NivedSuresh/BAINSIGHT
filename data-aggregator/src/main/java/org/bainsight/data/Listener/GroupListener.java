package org.bainsight.data.Listener;


import org.slf4j.Logger;

import java.io.IOException;
import java.net.*;


interface GroupListener {
    default void listenToGroup(MulticastSocket socket, InetAddress group, byte[] buffer, MessageReceiveBuffer messageReceiveBuffer, Logger log) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (!Thread.currentThread().isInterrupted()) {
            try{
                socket.receive(packet);
                messageReceiveBuffer.offer(packet.getData());
            }
            catch(IOException e){
                log.error("Failed to receive packets"); }
        }
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(group);
        socket.leaveGroup(new InetSocketAddress(group, 5000), networkInterface);
    }
}
