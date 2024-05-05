package org.bainsight.data.Config.Redis;



import org.bainsight.data.Model.Entity.CandleStick;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.util.List;

public class CandleStickKeySpaceConfig extends KeyspaceConfiguration {
    @Override
    protected Iterable<KeyspaceSettings> initialConfiguration() {

        KeyspaceSettings candleStick = new KeyspaceSettings(CandleStick.class, "CandleStick");
        return List.of(candleStick);
    }

}
