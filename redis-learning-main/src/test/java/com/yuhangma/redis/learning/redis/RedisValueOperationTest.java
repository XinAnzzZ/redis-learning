package com.yuhangma.redis.learning.redis;

import com.yuhangma.redis.learning.RedisLearningAppTest;
import com.yuhangma.redis.learning.model.PersonDTO;
import org.junit.Test;
import org.springframework.data.redis.RedisSystemException;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Moore
 * @since 2020/10/26
 */
@SuppressWarnings("all")
public class RedisValueOperationTest extends RedisLearningAppTest {

    /**
     * set 和 get 测试
     *
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @see <a href="http://doc.redisfans.com/string/set.html">Redis 命令参考: SET</a>
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @see <a href="http://doc.redisfans.com/string/get.html">Redis 命令参考: GET</a>
     */
    @Test
    public void setAndGetTest() {
        String value1 = valueOps.get(k1);
        assertNull(value1);

        valueOps.set(k1, v1);
        String value2 = valueOps.get(k1);
        assertEquals(v1, value2);
    }

    /**
     * Redis 命令：SETNX
     * <p>
     * key 不存在时才设置
     *
     * @see <a href="http://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     * @see <a href="http://doc.redisfans.com/string/setnx.html">Redis 命令参考: SETNX</a>
     */
    @Test
    public void setIfAbsentTest() {
        Boolean success1 = valueOps.setIfAbsent(k1, v1);
        assertTrue(success1);
        String value = valueOps.get(k1);
        assertEquals(v1, value);

        Boolean success2 = valueOps.setIfAbsent(k1, v2);
        assertFalse(success2);
        String value2 = valueOps.get(k1);
        assertNotEquals(v2, value2);
    }

    /**
     * Redis 命令：STRLEN
     * <p>
     * 查询字符串长度
     * 1、如果 key 不存在返回 0
     * 2、如果 key 的类型不是 string，那么 redis 会抛出一个错误，spring-data-redis 会将其封装为一个 {@link RedisSystemException} 异常
     *
     * @see <a href="http://redis.io/commands/strlen">Redis Documentation: STRLEN</a>
     * @see <a href="http://doc.redisfans.com/string/strlen.html">Redis 命令参考: STRLEN</a>
     */
    @Test
    public void strLengthTest() {
        // key 不存在时返回 0
        long size1 = valueOps.size(k1);
        assertEquals(0L, size1);

        // key 的类型不是 string，抛出异常
        listOps.leftPush(k1, PersonDTO.newRandomPerson());
        expectedException.expect(RedisSystemException.class);
        valueOps.size(k1);

        valueOps.set(k1, v1);
        long size2 = valueOps.size(k1);
        assertEquals(v1.length(), size2);
    }

    /**
     * 字符串追加内容
     *
     * @see <a href="http://redis.io/commands/append">Redis Documentation: APPEND</a>
     * @see <a href="http://doc.redisfans.com/string/append.html">Redis 命令参考: APPEND</a>
     */
    @Test
    public void appendTest() {
        // 当 key 不存在时，append 等同于 set
        valueOps.append(k1, v2);
        String value1 = valueOps.get(k1);
        assertEquals(v2, value1);

        valueOps.set(k1, v1);
        valueOps.append(k1, v2);
        String value2 = valueOps.get(k1);
        assertEquals(v1 + v2, value2);
    }

    /**
     * 取出旧值设置新值
     *
     * @see <a href="http://redis.io/commands/getset">Redis Documentation: GETSET</a>
     * @see <a href="http://doc.redisfans.com/string/getset.html">Redis 命令参考: GETSET</a>
     */
    @Test
    public void getSetTest() {
        // 如果 key 不存在，返回 null，并且设置
        String oldVal1 = valueOps.getAndSet(k1, v1);
        assertNull(oldVal1);

        String oldVal2 = valueOps.getAndSet(k1, v2);
        assertEquals(v1, oldVal2);

        String newVal = valueOps.get(k1);
        assertEquals(v2, newVal);
    }

    /**
     * Redis 命令：MSET/MGET
     * <p>
     * set/get 多个
     *
     * @see <a href="http://redis.io/commands/mset">Redis Documentation: MSET</a>
     * @see <a href="http://doc.redisfans.com/string/mset.html">Redis 命令参考: MSET</a>
     * @see <a href="http://redis.io/commands/mget">Redis Documentation: MGET</a>
     * @see <a href="http://doc.redisfans.com/string/mget.html">Redis 命令参考: MGET</a>
     */
    @Test
    public void multiSetAndMultiGetTest() {
        Map<String, String> kvs = Map.of(k1, v1,
                k2, v2,
                k3, v3,
                k4, v4,
                k5, v5);
        valueOps.multiSet(kvs);

        List<String> values = valueOps.multiGet(kvs.keySet());
        assertTrue(values.containsAll(kvs.values()));
    }

    /**
     * Redis 命令：MSETNX
     * <p>
     * 设置多个，当且仅当所有的 key 都不存在时设置成功
     *
     * @see <a href="http://redis.io/commands/msetnx">Redis Documentation: MSETNX</a>
     * @see <a href="http://doc.redisfans.com/string/msetnx.html">Redis 命令参考: MSETNX</a>
     */
    @Test
    public void multiSetIfAbsentTest() {
        Map<String, String> kvs = Map.of(k1, v1,
                k2, v2,
                k3, v3,
                k4, v4,
                k5, v5);
        Boolean success1 = valueOps.multiSetIfAbsent(kvs);
        // 当所有 key 不存在时设置成功
        assertTrue(success1);
        List<String> values = valueOps.multiGet(kvs.keySet());
        assertTrue(values.containsAll(kvs.values()));

        // 所有 key 都存在时设置失败
        Boolean success2 = valueOps.multiSetIfAbsent(kvs);
        assertFalse(success2);

        // 删掉 4 个 key，只留一个 key 时也是失败
        redisTemplate.delete(List.of(k1, k2, k3, k4));
        Boolean success3 = valueOps.multiSetIfAbsent(kvs);
        assertFalse(success3);
    }
}
