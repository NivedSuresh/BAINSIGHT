java -jar \
    -Daeron.sample.embeddedMediaDriver=true \
    -Daeron.multicast.FlowControl.supplier=io.aeron.driver.MinMulticastFlowControlSupplier \
    -Daeron.ThreadNode=io.aeron.driver.ThreadingMode.SHARED \
    -Daeron.user.service.multicast.channel="aeron:udp?endpoint=224.0.1.1:40456|interface=localhost|reliable=true" \
    -Daeron.user.service.multicast.steam.id=1001 \
    --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
    AeronPub-0.0.1-SNAPSHOT.jar


#    -Daeron.stick.multicast.channel="aeron:udp?endpoint=224.0.1.1:40456|interface=localhost" \
#    -Daeron.multicast.FlowControl.supplier=io.aeron.driver.MinMulticastFlowControlSupplier \
#    -Daeron.endpoint.ip="localhost" \
#    -Daeron.endpoint.port="7775" \
#    -Daeron.channel="aeron:udp?endpoint=localhost:20121" \