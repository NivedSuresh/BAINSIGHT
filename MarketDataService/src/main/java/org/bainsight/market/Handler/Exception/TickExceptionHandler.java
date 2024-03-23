package org.bainsight.market.Handler.Exception;

import com.lmax.disruptor.ExceptionHandler;
import org.bainsight.market.Model.Events.TickEvent;

public class TickExceptionHandler<T extends TickEvent> implements ExceptionHandler<T> {
    @Override
    public void handleEventException(Throwable ex, long sequence, T event) {
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
