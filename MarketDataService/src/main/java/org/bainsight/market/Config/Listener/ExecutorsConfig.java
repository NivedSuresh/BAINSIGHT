package org.bainsight.market.Config.Listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorsConfig {
    @Bean
    public ExecutorService listenerThreads(){
        return Executors.newSingleThreadExecutor();
    }


    @Bean
    public ExecutorService recoveryExecutor(){
        return Executors.newFixedThreadPool(3);
    }


    @Bean
    public ExecutorService orderBookExecutor(){
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService messageExecutor(){
        return Executors.newSingleThreadExecutor();
    }


    @Bean
    public ExecutorService greenExecutor(){
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
