package com.yuhangma.redis.learning;

import com.yuhangma.redis.learning.model.PersonDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

/**
 * @author Moore
 * @see <a href="http://redis.io/commands/exists">Redis Documentation: EXISTS</a>
 * @see <a href="http://doc.redisfans.com">Redis 命令参考</a>
 * @since 2020/08/29
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@SuppressWarnings("all")
public class RedisLearningAppTest {

    protected String k1, k2, k3, k4, k5;

    protected static final String v1 = "v1", v2 = "v2", v3 = "v3", v4 = "v4", v5 = "v5";

    @Autowired
    protected RedisTemplate<String, PersonDTO> redisTemplate;

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @Before
    public void beforeTest() {
        log.info("-------- before test: 初始化 key 开始 -----------");
        // 初始化 5 个 key，这 5 个 key 在库中不存在
        k1 = getRandomKey();
        k2 = getRandomKey();
        k3 = getRandomKey();
        k4 = getRandomKey();
        k5 = getRandomKey();
        log.info("-------- before test: 初始化 key 完成 -----------");
    }

    @After
    public void afterTest() {
        log.info("-------- after test: 删除 key 开始 -----------");
        // 删除多个 key，List.of() 是 JDK11 新的 API
        redisTemplate.delete(List.of(k1, k2, k3, k4, k5));
        log.info("-------- after test: 删除 key 完成 -----------");
    }

    /**
     * 获取随机的 key，可以保证一定不会在数据库中存在
     *
     * @return the random key
     */
    protected String getRandomKey() {
        String randomKey;
        do {
            randomKey = UUID.randomUUID().toString().replace("-", "");
        } while (redisTemplate.hasKey(randomKey));
        return randomKey;
    }

    protected ListOperations<String, PersonDTO> listOps() {
        return redisTemplate.opsForList();
    }

    protected SetOperations<String, PersonDTO> setOps() {
        return redisTemplate.opsForSet();
    }

    protected ZSetOperations<String, PersonDTO> zsetOps() {
        return redisTemplate.opsForZSet();
    }

    protected HashOperations<String, Object, Object> hashOps() {
        return redisTemplate.opsForHash();
    }

    protected ValueOperations<String, PersonDTO> valueOps() {
        return redisTemplate.opsForValue();
    }
}
