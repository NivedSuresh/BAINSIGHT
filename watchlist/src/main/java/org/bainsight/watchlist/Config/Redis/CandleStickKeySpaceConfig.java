package org.bainsight.watchlist.Config.Redis;



import org.bainsight.watchlist.CandleStick.Entity.CandleStick;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.util.Collections;

public class CandleStickKeySpaceConfig extends KeyspaceConfiguration {
    @Override
    protected Iterable<KeyspaceSettings> initialConfiguration() {
        return Collections.singleton(new KeyspaceSettings(CandleStick.class, "CandleStick"));
    }

}
