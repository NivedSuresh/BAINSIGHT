package org.bainsight.liquidity.Listener;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class UdpListener {

    private final ExecutorService udpExecutor;
    private final MessageBuffer messageBuffer;
    private final ExecutorService greenExecutor;


    @Value("${primary.multicast.address}")
    public String primaryMulticastAddress;
    @Value("${backup.multicast.address}")
    public String backupMulticastAddress;

    public UdpListener(final @Qualifier("listenerThreads") ExecutorService udpExecutor,
                       final MessageBuffer messageBuffer,
                       final ExecutorService greenExecutor) {
        this.udpExecutor = udpExecutor;
        this.messageBuffer = messageBuffer;
        this.greenExecutor = greenExecutor;
    }


    @PostConstruct
    public void startListeningToPrimary(){
        new Thread(new PrimaryGroupListener(messageBuffer, primaryMulticastAddress)).start();
    }

    @PostConstruct
    public void startListeningToBackup(){
        new Thread(new BackupGroupListener(messageBuffer, backupMulticastAddress)).start();
    }


}
