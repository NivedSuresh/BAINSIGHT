package org.bainsight.updates.Aeron;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import lombok.RequiredArgsConstructor;
import org.agrona.DirectBuffer;
import org.bainsight.updates.Service.WebSocketService;

import java.io.IOException;

@RequiredArgsConstructor
public class CandleStickHandler implements FragmentHandler {

    private final ObjectMapper mapper;
    private final WebSocketService webSocketService;
    @Override
    public void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
        byte[] bytes = new byte[length];
        buffer.getBytes(offset, bytes);
        try {
            CandleStick stick = mapper.readValue(bytes, CandleStick.class);
            this.webSocketService.pushRequested(stick);
        } catch (IOException e) {
            /* TODO: IMPLEMENT JOURNALING */
        }
    }
}
