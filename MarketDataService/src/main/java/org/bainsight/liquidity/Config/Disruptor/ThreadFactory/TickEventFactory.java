package org.bainsight.liquidity.Config.Disruptor.ThreadFactory;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public enum TickEventFactory implements ThreadFactory {
    INSTANCE;
    @Override
    public Thread newThread(final @NotNull Runnable r)
    {
        return new Thread(r);
    }
}
