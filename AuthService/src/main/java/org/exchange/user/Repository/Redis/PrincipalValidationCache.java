package org.exchange.user.Repository.Redis;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exchange.library.Exception.NotFound.NotFoundOnCacheException;
import org.exchange.user.Model.PrincipalRevoked;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrincipalValidationCache {

    private final ReactiveRedisOperations<String, PrincipalRevoked> template;

    private static final String hashKey = "principal_validation";

    public Mono<PrincipalRevoked> findPrincipalValidationFromCache(String broker) {
        log.info("Validation is being fetched from cache!");
        return template.<String, PrincipalRevoked>opsForHash().get(hashKey, broker)
                .switchIfEmpty(Mono.error(new NotFoundOnCacheException()));
    }

    public Mono<Boolean> save(PrincipalRevoked principalRevoked) {
        log.info("Cache update request : {}", principalRevoked);
        return template.<String, PrincipalRevoked>opsForHash()
                .put(hashKey, principalRevoked.getUsername(), principalRevoked)
                .doOnNext(System.out::println);
    }


    public Flux<PrincipalRevoked> findAll() {
        return template.<String, PrincipalRevoked>opsForHash().values(hashKey);
    }
}
