package org.bainsight.liquidity.Listener;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class UdpListener {

    private final MessageBuffer messageBuffer;


    @Value("${primary.multicast.address}")
    public String PRIMARY_MULTICAST_ADDRESS;

    @Value("${backup.multicast.address}")
    public String BACKUP_MULTICAST_ADDRESS;

    @Value("${primary.multicast.port}")
    public int PRIMARY_MULTICAST_PORT;

    @Value("${backup.multicast.port}")
    public int BACKUP_MULTICAST_PORT;

    public UdpListener(final MessageBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
    }


    @PostConstruct
    public void startListeningToPrimary(){
        new Thread(new PrimaryGroupListener(
                messageBuffer,
                PRIMARY_MULTICAST_ADDRESS,
                PRIMARY_MULTICAST_PORT))
                .start();
    }

    @PostConstruct
    public void startListeningToBackup(){
        new Thread(new BackupGroupListener(
                messageBuffer,
                BACKUP_MULTICAST_ADDRESS,
                BACKUP_MULTICAST_PORT))
                .start();
    }


}
