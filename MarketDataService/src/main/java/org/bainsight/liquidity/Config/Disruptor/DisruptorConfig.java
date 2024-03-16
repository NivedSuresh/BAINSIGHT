package org.bainsight.liquidity.Config.Disruptor;


import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.bainsight.liquidity.Handler.TickEventHandler;
import org.bainsight.liquidity.Listener.MessageRangeBuffer;
import org.bainsight.liquidity.Model.Events.TickEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DisruptorConfig {

    @Bean
    public Disruptor<TickEvent> tickEventDisruptor(MessageRangeBuffer messageRangeBuffer){
        Disruptor<TickEvent> disruptor = new Disruptor<>(
                TickEvent.TICK_EVENT_FACTORY,
                1024,
                DaemonThreadFactory.INSTANCE,
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
                );

        disruptor.setDefaultExceptionHandler(new TickDisruptorExceptionHandler());
        disruptor.handleEventsWith(new TickEventHandler(messageRangeBuffer));

        disruptor.start();

        return disruptor;
    }

//    @Bean
//    public Disruptor<MessageEvent> messageEventDisruptor(ObjectMapper mapper){
//        Disruptor<MessageEvent> disruptor = new Disruptor<>(
//                MessageEvent.MESSAGE_EVENT_FACTORY,
//                1024,
//                Thread.ofVirtual().factory(),
//                ProducerType.SINGLE,
//                new YieldingWaitStrategy()
//        );
//
//        disruptor.setDefaultExceptionHandler(new MessageEventExceptionHandler());
//        disruptor.handleEventsWith(new LockSnatcher(mapper));
//
//        disruptor.start();
//
//        return disruptor;
//    }

    @Bean
    public RingBuffer<TickEvent> tickEventRingBuffer(final Disruptor<TickEvent> tickEventDisruptor){
        return tickEventDisruptor.getRingBuffer();
    }

//    @Bean
//    public RingBuffer<MessageEvent> messageEventRingBuffer(final Disruptor<MessageEvent> messageEventDisruptor){
//        return messageEventDisruptor.getRingBuffer();
//    }
}
