package org.bainsight.watchlist.CandleStick.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandleStickService {

    private final RedisTemplate<String, Object> redisTemplate;



}
