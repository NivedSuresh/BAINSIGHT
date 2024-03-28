package org.bainsight.data.Listener;


import jakarta.annotation.PostConstruct;
import org.bainsight.data.Listener.BackupGroupListener;
import org.bainsight.data.Listener.MessageReceiveBuffer;
import org.bainsight.data.Listener.PrimaryGroupListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UdpListener {

    private final MessageReceiveBuffer messageReceiveBuffer;


    @Value("${primary.multicast.address}")
    public String PRIMARY_MULTICAST_ADDRESS;

    @Value("${backup.multicast.address}")
    public String BACKUP_MULTICAST_ADDRESS;

    @Value("${primary.multicast.port}")
    public int PRIMARY_MULTICAST_PORT;

    @Value("${backup.multicast.port}")
    public int BACKUP_MULTICAST_PORT;

    public UdpListener(final MessageReceiveBuffer messageReceiveBuffer) {
        this.messageReceiveBuffer = messageReceiveBuffer;
    }


    @PostConstruct
    public void startListeningToPrimary(){
        new Thread(new PrimaryGroupListener(
                messageReceiveBuffer,
                PRIMARY_MULTICAST_ADDRESS,
                PRIMARY_MULTICAST_PORT))
                .start();
    }

    @PostConstruct
    public void startListeningToBackup(){
        new Thread(new BackupGroupListener(
                messageReceiveBuffer,
                BACKUP_MULTICAST_ADDRESS,
                BACKUP_MULTICAST_PORT))
                .start();
    }


}
