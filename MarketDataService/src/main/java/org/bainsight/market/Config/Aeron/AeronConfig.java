package org.bainsight.market.Config.Aeron;

import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.MinMulticastFlowControlSupplier;
import io.aeron.driver.ThreadingMode;
import org.agrona.concurrent.BackoffIdleStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class AeronConfig {

    public static final long LINGER_TIMEOUT_MS;
    public static final boolean EMBEDDED_MEDIA_DRIVER = Objects.equals(System.getProperty("aeron.sample.embeddedMediaDriver"), "true");


    static {
        LINGER_TIMEOUT_MS = 0;
    }



    @Bean
    public MediaDriver mediaDriver(@Value("${aeron.directory}") final String AERON_DIRECTORY){
        MediaDriver.Context context = new MediaDriver.Context()
                .dirDeleteOnStart(true)
                .dirDeleteOnShutdown(true)
                .senderIdleStrategy(new BackoffIdleStrategy())
                .multicastFlowControlSupplier(new MinMulticastFlowControlSupplier())
                .aeronDirectoryName(AERON_DIRECTORY)
                .threadingMode(ThreadingMode.SHARED);

        return MediaDriver.launchEmbedded(context);
    }

    @Bean
    public Aeron aeron(MediaDriver driver){

        final Aeron.Context ctx = new Aeron.Context();

        if (EMBEDDED_MEDIA_DRIVER)
        {
            ctx.aeronDirectoryName(driver.aeronDirectoryName());
        }

        return Aeron.connect(ctx);
    }


}
