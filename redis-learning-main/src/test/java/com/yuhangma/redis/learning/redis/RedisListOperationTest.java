package com.yuhangma.redis.learning.redis;

import com.yuhangma.redis.learning.RedisLearningAppTest;
import com.yuhangma.redis.learning.model.PersonDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 * @author Moore
 * @since 2020/08/29
 */
@SuppressWarnings("all")
public class RedisListOperationTest extends RedisLearningAppTest {

    /**
     * 测试 list 添加元素
     * idea 会警告 push 的结果可能返回 null，但是实际上只有在 pipeline / transaction 中才会返回 null，所以可以放心大胆的使用自动拆箱。
     */
    @Test
    public void testListPush() {
        final String key = "test:list:push";
        final String v1 = "v1";
        final String v2 = "v2";
        // 删除 key
        stringRedisTemplate.delete(key);

        // 往 v1 前面插入一个值 v2，由于此时 key 不存在，返回 0
        long result1 = stringRedisTemplate.opsForList().leftPush(key, v1, v2);
        Assert.assertEquals(result1, 0L);
        // 正常插入，返回 list 的 size
        long result2 = stringRedisTemplate.opsForList().leftPush(key, v1);
        long size = stringRedisTemplate.opsForList().size(key);
        Assert.assertEquals(result2, size);
        // 往 v2 前面插入一个值 v1，由于此时 v2 不存在，返回 -1
        long result3 = stringRedisTemplate.opsForList().leftPush(key, v2, v1);
        Assert.assertEquals(result3, -1L);
    }

    /**
     * 测试 list 查询 size
     */
    @Test
    public void testListSize() {
        final String key = "test:list:size";
        redisTemplate.delete(key);
        // 当 key 不存在时返回 0，在 pipeline 或者 transaction 中使用此命令时，返回 null
        Assert.assertEquals(0L, listOps().size(key).longValue());

        listOps().leftPush(key, PersonDTO.newRandomPerson());
        Assert.assertEquals(1L, (long) Optional.ofNullable(listOps().size(key)).orElse(0L));
    }

    @Test
    public void testListGet() {
        final String key = "test:list:get";
        redisTemplate.delete(key);

        valueOps().set(key, PersonDTO.newRandomPerson());

        PersonDTO x = valueOps().get(key);
        System.out.println(x);
    }
}
