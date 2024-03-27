package org.exchange.user.Service;

import java.util.UUID;

public interface KafkaService {
    void updateClientMeta(UUID ucc, double balance);
}
