package org.bainsight.portfolio.Config.Executors;


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
