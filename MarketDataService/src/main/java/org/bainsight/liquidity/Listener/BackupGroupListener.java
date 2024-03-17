package org.bainsight.liquidity.Listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

@RequiredArgsConstructor
@Slf4j
public class BackupGroupListener implements Runnable, GroupListener{
    private final MessageBuffer messageBuffer;
    private final String backupMulticastAddress;
    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(5000)){
            InetAddress group = InetAddress.getByName(backupMulticastAddress);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(group);
            socket.joinGroup(new InetSocketAddress(group, 5000), networkInterface);
            /*
                Payload size per second = Number of objects per second * Size of each object
                ie: 4000 * 233 (ESTIMATED SIZE OF TICK) = 932_000 bytes per second

                Memory consumption (MB) = 932,000 bytes / (1024 * 1024) ≈ 0.889 MB
            */
            byte[] buffer = new byte[1024];
            this.listenToGroup(socket, group, buffer, messageBuffer, log);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

}
