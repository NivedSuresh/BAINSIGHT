package org.bainsight.liquidity.Listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.liquidity.Config.ConfigurationVariables;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

@RequiredArgsConstructor
@Slf4j
public class BackupGroupListener implements Runnable, GroupListener{
    private final MessageBuffer messageBuffer;

    private final String BACKUP_MULTICAST_ADDRESS;

    private final int BACKUP_MULTICAST_PORT;
    @Override
    public void run() {

        try (MulticastSocket socket = new MulticastSocket(BACKUP_MULTICAST_PORT)){
            InetAddress group = InetAddress.getByName(BACKUP_MULTICAST_ADDRESS);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(group);
            socket.joinGroup(new InetSocketAddress(group, 5000), networkInterface);

            byte[] buffer = new byte[1024];
            this.listenToGroup(socket, group, buffer, messageBuffer, log);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

}
