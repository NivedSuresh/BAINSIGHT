package org.bainsight.liquidity.Controller;


import lombok.RequiredArgsConstructor;
import org.bainsight.liquidity.LiquidityServiceApplication;
import org.bainsight.liquidity.Model.Person;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CacheTest {
    private final RedisTemplate<String, Object> template;


    @PostMapping
    public void insertToCache(@RequestBody Person person){
        template.opsForValue().set(person.getName(), person);
    }

    @GetMapping("{name}")
    public Person findByName(@PathVariable String name){
        return (Person) template.opsForValue().get(name);
    }



}
