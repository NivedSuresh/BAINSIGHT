package org.bainsight.data.Config.Redis;


import org.bainsight.data.Model.Entity.CandleStick;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.util.Collections;

public class CandleStickKeySpaceConfig extends KeyspaceConfiguration {
    @Override
    protected @NotNull Iterable<KeyspaceSettings> initialConfiguration() {
        return Collections.singleton(new KeyspaceSettings(CandleStick.class, "CandleStick"));
    }

}
