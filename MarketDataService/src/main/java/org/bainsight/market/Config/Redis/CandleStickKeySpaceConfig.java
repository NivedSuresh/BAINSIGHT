package org.bainsight.market.Config.Redis;

import org.bainsight.market.Model.Entity.CandleStick;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.util.Collections;

public class CandleStickKeySpaceConfig extends KeyspaceConfiguration {
    @Override
    protected @NotNull Iterable<KeyspaceSettings> initialConfiguration() {
        return Collections.singleton(new KeyspaceSettings(CandleStick.class, "CandleStick"));
    }

}
