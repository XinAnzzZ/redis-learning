package com.yuhangma.redis.learning.redis;

import com.yuhangma.redis.learning.RedisLearningAppTest;
import org.junit.Test;

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
     * 字符串追加内容
     *
     * @see <a href="http://redis.io/commands/append">Redis Documentation: APPEND</a>
     * @see <a href="http://doc.redisfans.com/string/append.html">Redis 命令参考: APPEND</a>
     */
    @Test
    public void appendTest() {
        String key = getRandomNotExistKey();
        // 当 key 不存在时，append 等同于 set
        valueOps.append(key, v2);
        String value1 = valueOps.get(key);
        assertEquals(v2, value1);

        valueOps.set(key, v1);
        valueOps.append(key, v2);
        String value2 = valueOps.get(key);
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
        String key = getRandomNotExistKey();
        // 如果 key 不存在，返回 null，并且设置
        String oldVal1 = valueOps.getAndSet(key, v1);
        assertNull(oldVal1);

        String oldVal2 = valueOps.getAndSet(key, v2);
        assertEquals(v1, oldVal2);

        String newVal = valueOps.get(key);
        assertEquals(v2, newVal);
    }

    /**
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
}
