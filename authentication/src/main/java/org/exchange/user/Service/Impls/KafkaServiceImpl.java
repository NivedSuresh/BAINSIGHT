package org.exchange.user.Service.Impls;

import org.exchange.user.Service.KafkaService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaServiceImpl implements KafkaService {
    @Override
    public void updateClientMeta(UUID ucc, double balance) {

    }
}
