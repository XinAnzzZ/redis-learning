package com.yuhangma.redis.learning.redis;

import com.yuhangma.redis.learning.RedisLearningAppTest;
import com.yuhangma.redis.learning.model.PersonDTO;
import org.junit.Test;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * 本测试类为 Redis 对于 String 类型 key 的所有命令的演示测试类。
 * <p>
 * Redis 对于 String 类型的 key 主要有三大类操作:
 * <ul>
 *     <li>1、对于字符串的基本操作，比如对字符串进行追加、修改、截取等。</li>
 *     <li>2、对于数值字符串的特殊操作，比如对数值进行自增、自减等。</li>
 *     <li>3、对于字节的一些特殊操作，比如设置某一“bit（位）”的值，用此特性可以实现例如“bitmap（位图）等。”</li>
 * </ul>
 *
 * @author Moore
 * @since 2020/10/26
 */
@SuppressWarnings("all")
public class RedisValueOperationTest extends RedisLearningAppTest {

    //////////////////////// 字符串基础操作 ////////////////////////

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
     * Redis 命令：SETRANGE、GETRANGE
     * <p>
     * 对字符串进行范围修改/读取，包含起始和终止位置
     *
     * @see <a href="http://redis.io/commands/setrange">Redis Documentation: SETRANGE</a>
     * @see <a href="http://doc.redisfans.com/string/setrange.html">Redis 命令参考: SETRANGE</a>
     * @see <a href="http://redis.io/commands/getrange">Redis Documentation: GETRANGE</a>
     * @see <a href="http://doc.redisfans.com/string/getrange.html">Redis 命令参考: GETRANGE</a>
     */
    @Test
    public void setAndGetRangeTest() {
        String originValue = "abcdefg";
        valueOps.set(k1, originValue);

        int start = 0;
        int end = 3;
        String range1 = valueOps.get(k1, start, end);
        // redis 的 getRange 会包含起点和终点，而 java 的 String 的 subString 只包含起点，所以需要加一。
        assertEquals(originValue.substring(start, end + 1), range1);

        // 从 b 位置开始（包含 b）进行替换，bcde 将会替换为 test
        valueOps.set(k1, "test", originValue.indexOf('b'));
        String newValue1 = valueOps.get(k1);
        String expectedValue = originValue.replace("bcde", "test");
        assertEquals(expectedValue, newValue1);
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

    /**
     * Redis 命令：SETEX、PSETEX
     * <p>
     * 设置一个值并且指定生存时间，Redis 原生命令中有两个相关的命令，分别是“SETEX”，“PSETNX”，两者除了过期时间的单位不同并无其他差异。
     * 其中 SETEX 过期时间的单位为“秒”，PSETEX 过期时间单位为“毫秒”。而 spring-data-redis 将这两个命令统一成为一个方法。
     *
     * @see <a href="http://redis.io/commands/setex">Redis Documentation: SETEX</a>
     * @see <a href="http://doc.redisfans.com/string/setex.html">Redis 命令参考: SETEX</a>
     * @see <a href="http://redis.io/commands/psetex">Redis Documentation: PSETEX</a>
     * @see <a href="http://doc.redisfans.com/string/psetex.html">Redis 命令参考: PSETEX</a>
     */
    @Test
    public void setExTest() throws InterruptedException {
        // 设置一个值，存活时间为 3s，查询结果不为 null
        valueOps.set(k1, v1, 3, TimeUnit.SECONDS);
        String value1 = valueOps.get(k1);
        assertNotNull(value1);

        // 休眠 3s 后再次查询，结果为 null
        TimeUnit.SECONDS.sleep(3);
        String value2 = valueOps.get(k1);
        assertNull(value2);
    }

    //////////////////////// 数值相关 ////////////////////////

    /**
     * Redis 命令：INCR、INCRBY、INCRBYFLOAT、DECR、DECRBY.
     * <p>
     * 数值增加/减少，增加或减少的数值可以是整数，也可以是小数。Redis 原生有 5 个命令，spring-data-redis 对其实现为
     * {@link ValueOperations#increment(Object, long)} 和 {@link ValueOperations#increment(Object, double)}，
     * 分别对应整数和小数的操作，减少值只需将对应的值设置为负数即可。
     */
    @Test
    public void incrTest() {
        // key 不存在时，那么 key 的值会先被初始化为 0，然后执行操作
        long result1 = valueOps.increment(k1, 1);
        assertEquals(1, result1);

        long result2 = valueOps.increment(k1, 3);
        assertEquals(4, result2);

        // 对于小数的操作
        double result3 = valueOps.increment(k2, 1.1);
        assertEquals(1.1, result3, 0);

        double result4 = valueOps.increment(k2, 2.3);
        assertEquals(3.4, result4, 0);

        // 如果 key 的值是整数，可以对其进行小数操作
        double result5 = valueOps.increment(k1, 1.2);
        assertEquals(5.2, result5, 0);

        // 如果 key 的值是小数，不能对其进行整数操作
        expectedException.expect(RedisSystemException.class);
        valueOps.increment(k1, 1);
    }
}
