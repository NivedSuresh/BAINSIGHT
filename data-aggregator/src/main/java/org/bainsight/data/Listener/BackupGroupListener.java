package org.bainsight.data.Listener;

//@RequiredArgsConstructor
//@Slf4j
//public class BackupGroupListener implements Runnable, GroupListener{
//    private final MessageReceiveBuffer messageReceiveBuffer;
//
//    private final String BACKUP_MULTICAST_ADDRESS;
//
//    private final int BACKUP_MULTICAST_PORT;
//    @Override
//    public void run() {
//
//        try (MulticastSocket socket = new MulticastSocket(BACKUP_MULTICAST_PORT)){
//            InetAddress group = InetAddress.getByName(BACKUP_MULTICAST_ADDRESS);
//            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(group);
//            socket.joinGroup(new InetSocketAddress(group, 5000), networkInterface);
//
//            byte[] buffer = new byte[1024];
//            this.listenToGroup(socket, group, buffer, messageReceiveBuffer, log);
//        }catch (IOException e){
//            log.error(e.getMessage());
//        }
//    }
//
//}
