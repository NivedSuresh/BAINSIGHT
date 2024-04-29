package org.bainsight.data.Listener;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

@Component
@Profile("prod")
public class UdpListener {

    private final MessageReceiveBuffer messageReceiveBuffer;
    private final ExecutorService backup, primary;

    @Value("${primary.multicast.address}")
    public String PRIMARY_MULTICAST_ADDRESS;

    @Value("${backup.multicast.address}")
    public String BACKUP_MULTICAST_ADDRESS;

    @Value("${primary.multicast.port}")
    public int PRIMARY_MULTICAST_PORT;

    @Value("${backup.multicast.port}")
    public int BACKUP_MULTICAST_PORT;

    public UdpListener(final MessageReceiveBuffer messageReceiveBuffer,
                       final ExecutorService backup,
                       final ExecutorService primary) {
        this.messageReceiveBuffer = messageReceiveBuffer;
        this.backup = backup;
        this.primary = primary;
    }


    @PostConstruct
    public void startListeningToPrimary(){
        this.backup.execute(new PrimaryGroupListener(
                messageReceiveBuffer,
                PRIMARY_MULTICAST_ADDRESS,
                PRIMARY_MULTICAST_PORT));
    }

    @PostConstruct
    public void startListeningToBackup(){
        this.primary.execute(new BackupGroupListener(
                messageReceiveBuffer,
                BACKUP_MULTICAST_ADDRESS,
                BACKUP_MULTICAST_PORT));
    }


}
