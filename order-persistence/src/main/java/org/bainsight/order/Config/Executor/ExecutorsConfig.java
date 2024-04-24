package org.bainsight.order.Config.Executor;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorsConfig {

    @Bean
    public ExecutorService greenExecutor(){
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
