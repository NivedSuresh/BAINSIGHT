package org.bainsight.liquidity.Config.Disruptor;

import com.lmax.disruptor.ExceptionHandler;
import org.bainsight.liquidity.Model.Events.TickEvent;

public class TickDisruptorExceptionHandler implements ExceptionHandler<TickEvent> {
    @Override
    public void handleEventException(Throwable ex, long sequence, TickEvent event) {
        /* Todo : Journaling */
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        /* Todo : */
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        /* Todo : */
    }
}
