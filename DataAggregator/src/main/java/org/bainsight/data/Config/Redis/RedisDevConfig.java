package org.bainsight.market.Config.Redis;

import org.springframework.context.annotation.Configuration;


@Configuration
public class RedisDevConfig {

//    public RedisAdvancedClusterCommands<String, String> commands;
//
//    @Bean
//    public RedisAdvancedClusterCommands<String, String> connect(){
//        RedisURI redisUri = RedisURI.Builder.redis("localhost").withPort(6379).build();
//
//        RedisClusterClient clusterClient = RedisClusterClient.create(redisUri);
//        StatefulRedisClusterConnection<String, String> connection = clusterClient.connect();
//        RedisAdvancedClusterCommands<String, String> sync = connection.sync();
//
//        this.commands = sync;
//
//        return sync;
//    }
//
//    @Scheduled(fixedRate = 1000)
//    public void doSomething(){
//        System.out.println(this.commands.get("nn"));
//    }


//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(new LettuceConnectionFactory());
//        System.out.println(template.opsForValue().get("hii"));
//        return template;
//    }


}
