package org.bainsight.data.Config.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorsConfig {
    @Bean
    public ExecutorService listenerThreads(){
        return Executors.newFixedThreadPool(2);
    }


    @Bean
    public ExecutorService recoveryExecutor(){
        return Executors.newSingleThreadExecutor();
    }


    @Bean
    public ExecutorService snapshotExecutor(){
        return Executors.newSingleThreadExecutor();
    }


    @Bean
    public ExecutorService greenExecutor(){
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
