package org.bainsight.liquidity.Listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.liquidity.Config.ConfigurationVariables;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

@Slf4j
@RequiredArgsConstructor
class PrimaryGroupListener implements Runnable, GroupListener {


    private final MessageBuffer messageBuffer;
    private final String PRIMARY_MULTICAST_ADDRESS;
    private final int PRIMARY_MULTICAST_PORT;


    @Override
    public void run() {


        try (MulticastSocket socket = new MulticastSocket(PRIMARY_MULTICAST_PORT))
        {
            InetAddress group = InetAddress.getByName(PRIMARY_MULTICAST_ADDRESS);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(group);
            socket.joinGroup(new InetSocketAddress(group, 5000), networkInterface);


            byte[] buffer = new byte[1024];
            this.listenToGroup(socket, group, buffer, messageBuffer, log);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
