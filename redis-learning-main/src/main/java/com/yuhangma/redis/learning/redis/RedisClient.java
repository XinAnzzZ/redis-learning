package com.yuhangma.redis.learning.redis;

import com.yuhangma.redis.learning.model.PersonDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Moore
 * @since 2020/08/27
 */
@Slf4j
@Component
public class RedisClient {

    @Autowired
    private RedisTemplate<String, PersonDTO> redisTemplate;

    /**
     * 从右边 pop count 条数据，原子操作，如果 list 中数量不足 count 个，则移除剩余的 value
     *
     * @param key   the key
     * @param count the count
     */
    public void rightMultiAtomicPop(String key, int count) {
        if (count == 0) {
            return;
        }
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (int i = 0; i < count; i++) {
                connection.rPop(key.getBytes());
            }
            return null;
        });
    }
}
