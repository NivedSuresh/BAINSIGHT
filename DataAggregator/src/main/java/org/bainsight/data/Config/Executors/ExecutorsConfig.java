package org.bainsight.data.Config.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorsConfig {

//    @Bean
//    public ExecutorService primary(){
//        return Executors.newSingleThreadExecutor();
//    }
//
//    @Bean
//    public ExecutorService backup(){
//        return Executors.newSingleThreadExecutor();
//    }

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
