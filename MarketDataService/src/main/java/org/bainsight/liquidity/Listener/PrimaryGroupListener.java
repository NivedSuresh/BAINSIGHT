package org.bainsight.liquidity.Listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

@Slf4j
@RequiredArgsConstructor
class PrimaryGroupListener implements Runnable, GroupListener {

    private final MessageBuffer messageBuffer;
    private final String primaryMulticastAddress;


    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(5000)){
            InetAddress group = InetAddress.getByName(primaryMulticastAddress);
            socket.joinGroup(group);

            /*
                Payload size per second = Number of objects per second * Size of each object
                ie: 4000 * 233 (ESTIMATED SIZE OF TICK) = 932_000 bytes per second

                Memory consumption (MB) = 932,000 bytes / (1024 * 1024) ≈ 0.889 MB
            */
            byte[] buffer = new byte[1024];
            this.listenToGroup(socket, group, buffer, messageBuffer, log, true);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }
}
