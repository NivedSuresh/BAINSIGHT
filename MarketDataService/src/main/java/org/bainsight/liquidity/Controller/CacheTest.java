package org.bainsight.liquidity.Controller;


import lombok.RequiredArgsConstructor;
import org.bainsight.liquidity.LiquidityServiceApplication;
import org.bainsight.liquidity.Model.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class CacheTest {
    private final RedisTemplate<String, Object> template;

    @Value("${exchange.id}")
    String[] exchangeIds;


    @PostMapping
    public void insertToCache(@RequestBody(required = false) Person person){
        template.opsForValue().set(person.getName(), person);
    }

    @GetMapping("{name}")
    public Person findByName(@PathVariable String name){
        return (Person) template.opsForValue().get(name);
    }

    @GetMapping("/config/server")
    public void testConfigServer(){
        System.out.println(Arrays.toString(exchangeIds));
    }



}
