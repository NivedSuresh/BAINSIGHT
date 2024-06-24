package org.bainsight.updates.Message.Aeron;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import lombok.RequiredArgsConstructor;
import org.agrona.DirectBuffer;
import org.bainsight.updates.Domain.WebSocketService;
import org.exchange.library.Dto.MarketRelated.CandleStick;

@RequiredArgsConstructor
public class CandleStickHandler implements FragmentHandler {

    private final ObjectMapper mapper;
    private final WebSocketService webSocketService;
    @Override
    public void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
        byte[] bytes = new byte[length];
        buffer.getBytes(offset, bytes);
        try
        {
            CandleStick stick = mapper.readValue(bytes, CandleStick.class);
            this.webSocketService.pushRequested(stick);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            /* TODO: IMPLEMENT JOURNALING */
        }
//        catch (IOException e) {
//            System.out.println(e.getMessage());
//            /* TODO: IMPLEMENT JOURNALING */
//        }
    }
}
