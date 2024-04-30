package org.bainsight.order.MatchingSim.Redis;


import org.bainsight.order.MatchingSim.Entity.CandleStick;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.util.List;

public class CandleStickKeySpaceConfig extends KeyspaceConfiguration {
    @Override
    protected Iterable<KeyspaceSettings> initialConfiguration() {

        KeyspaceSettings candleStick = new KeyspaceSettings(CandleStick.class, "CandleStick");

        return List.of(candleStick);
    }


}
