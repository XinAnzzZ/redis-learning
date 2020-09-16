package com.yuhangma.redis.learning.redis;

import com.yuhangma.redis.learning.RedisLearningAppTest;
import org.junit.Test;
import org.springframework.data.redis.core.ValueOperations;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Redis 对于 key 的操作 test
 * <p></p>
 *
 * @author Moore
 * @since 2020/09/15
 */
@SuppressWarnings("all")
public class RedisKeyOperationTest extends RedisLearningAppTest {

    private ValueOperations<String, String> valueOperations() {
        return stringRedisTemplate.opsForValue();
    }

    /**
     * 删除一个 key
     *
     * @see <a href="http://redis.io/commands/del">Redis Documentation: EXISTS</a>
     * @see <a href="http://doc.redisfans.com/key/del.html">Redis 命令参考: DEL</a>
     */
    @Test
    public void deleteKeyTest() {
        // key 不存时删除，返回 false
        Boolean success1 = stringRedisTemplate.delete(k1);
        assertFalse(success1);

        // 设置一个 key，然后删除，返回 true
        valueOperations().set(k1, v1);
        Boolean success2 = stringRedisTemplate.delete(k1);
        assertTrue(success2);

        // TODO
    }

    /**
     * 检查 key 是否存在
     *
     * @see <a href="http://redis.io/commands/exists">Redis Documentation: EXISTS</a>
     * @see <a href="http://doc.redisfans.com/key/exists.html">Redis 命令参考: EXISTS</a>
     */
    @Test
    public void existsKeyTest() {
        // key 不存在时返回 false
        Boolean success1 = stringRedisTemplate.hasKey(k1);
        assertFalse(success1);

        // key 存在时返回 true
        valueOperations().set(k1, v1);
        Boolean success2 = stringRedisTemplate.hasKey(k1);
        assertTrue(success2);
    }

    /**
     * 查看 key 的剩余生存时间（time to live），即过期时间
     * <p>
     *     <ul>
     *         <li>1. 返回 key 剩余的生存时间</li>
     *         <li>2. 如果 key 不存在，返回 -2</li>
     *         <li>3. 如果 key 存在，但是是持久化 key，返回 -1</li>
     *     </ul>
     * </p>
     *
     * @see <a href="http://redis.io/commands/ttl">Redis Documentation: TTL</a>
     * @see <a href="http://doc.redisfans.com/key/ttl.html">Redis 命令参考: TTL</a>
     */
    @Test
    public void ttlKeyTest() throws InterruptedException {
        // k1 不存在，返回 -2
        long expire1 = stringRedisTemplate.getExpire(k1);
        assertEquals(-2, expire1);
        // 设置 k1 两秒后过期
        valueOperations().set(k1, v1, 2, TimeUnit.SECONDS);

        // 过期时间大于 0 小于 2s
        long expire2 = stringRedisTemplate.getExpire(k1, TimeUnit.SECONDS);
        assertTrue(expire2 > 0 && expire2 <= 2);

        // 休眠 2s 后，key 过期
        TimeUnit.SECONDS.sleep(2);
        long expire3 = stringRedisTemplate.getExpire(k1);
        assertEquals(-2, expire3);

        // 设置一个持久化的 key，返回 -1
        valueOperations().set(k2, v2);
        long expire4 = stringRedisTemplate.getExpire(k2);
        assertEquals(-1, expire4);

        // 查看剩余时间，指定单位
        valueOperations().set(k3, v3, 10, TimeUnit.SECONDS);
        long expire5 = stringRedisTemplate.getExpire(k3, TimeUnit.MILLISECONDS);
        assertTrue(0 < expire5 && expire5 <= 10000);
    }

    /**
     * 为指定 key 设置过期时间
     * <p>
     *   <ul>
     *     <li>1. 如果 key 的值被改变，那么它的过期时间不会改变</li>
     *     <li>2. 如果使用 RENAME 命令对 key 进行改名，也不会修改它的过期时间</li>
     *     <li>3. 如果将带过期时间的 k1 改名为不带过期时间的 k2，则 k2 会被删除，然后 k1 改名为 k2，过期时间仍然不变</li>
     *     <li>4. 使用 PERSIST 命令可以移除过期时间，让 key 变成持久化 key</li>
     *     <li>5. 对一个带有过期时间的 key 使用该命令，会更新它的过期时间</li>
     *   </ul>
     * </p>
     *
     * @see <a href="http://redis.io/commands/expire">Redis Documentation: EXPIRE</a>
     * @see <a href="http://redis.io/commands/expireAt">Redis Documentation: EXPIREAT</a>
     * @see <a href="http://doc.redisfans.com/key/expire.html">Redis 命令参考: EXPIRE</a>
     * @see <a href="http://doc.redisfans.com/key/expireAt.html">Redis 命令参考: EXPIREAT</a>
     */
    @Test
    public void expireKeyTest() throws InterruptedException {
        // 当 key 不存在时，返回 false
        Boolean success1 = stringRedisTemplate.expire(k1, 1, TimeUnit.SECONDS);
        assertFalse(success1);

        // 设置一个值，设置过期时间 1s，此时 key 存在
        valueOperations().set(k1, v1);
        assertTrue(stringRedisTemplate.hasKey(k1));
        // key 存在时，正常设置，返回 true
        boolean success2 = stringRedisTemplate.expire(k1, 1, TimeUnit.SECONDS);
        assertTrue(success2);
        // 休眠 1s 后，key 不存在
        TimeUnit.SECONDS.sleep(1);
        assertFalse(stringRedisTemplate.hasKey(k2));
    }

    /**
     * 设置 key 在某个时间过期，这里的 expireAt 和 Redis 的 expireAt 是有区别的。
     * Redis 的 expireAt 和 expire 命令几乎一致，只是设置过期时间的单位不同，expire 是秒，而 expireAt 是毫秒。
     * 而这里调用的 expireAt 是 spring-data-redis 封装的一个方法而已，它的功能是设置让 key 在某个时间点过期。
     *
     * @see <a href="http://redis.io/commands/expire">Redis Documentation: EXPIRE</a>
     * @see <a href="http://redis.io/commands/expireAt">Redis Documentation: EXPIREAT</a>
     * @see <a href="http://doc.redisfans.com/key/expire.html">Redis 命令参考: EXPIRE</a>
     * @see <a href="http://doc.redisfans.com/key/expireAt.html">Redis 命令参考: EXPIREAT</a>
     */
    @Test
    public void expireAtTest() {
        valueOperations().set(k1, v1);
        // 设置一小时以后过期
        Date oneHourAgo = Date.from(OffsetDateTime.now().plusHours(1L).toInstant());
        boolean success = stringRedisTemplate.expireAt(k1, oneHourAgo);
        assertTrue(success);

        // 过期时间在 3600s 左右
        long expireSeconds = stringRedisTemplate.getExpire(k1, TimeUnit.SECONDS);
        assertTrue(3588 <= expireSeconds && expireSeconds <= 3600);
    }

    /**
     * 查询所有 key，支持通配符，时间复杂度 O(n)。
     * 虽然 redis 非常快，但是如果匹配到的 key 非常多的话，还是会影响性能。所以通常情况下生产环境会禁用此命令。
     * 有统计 key 的数量的需求可以用 set 来存所有的 key，然后客户端进行处理，或者使用 scan 命令来处理。
     *
     * @see {@link RedisKeyOperationTest#scanTest()}
     * @see <a href="http://redis.io/commands/keys">Redis Documentation: KEYS</a>
     * @see <a href="http://redis.io/commands/scan">Redis Documentation: SCAN</a>
     * @see <a href="http://doc.redisfans.com/key/keys.html">Redis 命令参考: KEYS</a>
     * @see <a href="http://doc.redisfans.com/key/scan.html">Redis 命令参考: SCAN</a>
     */
    @Test
    public void keysTest() {
        // 前面应该需要 flushdb，但是此操作比较危险，所以不在此演示。
        String prefix = "keys:test:";
        valueOperations().set(prefix + k1, v1);
        valueOperations().set(prefix + k2, v2);
        valueOperations().set(prefix + k3, v3);
        valueOperations().set(prefix + k4, v4);
        valueOperations().set(prefix + k5, v5);

        // 查询所有匹配 keys:test:* 模式的 key
        Set<String> keys = stringRedisTemplate.keys(prefix + "*");
        assertTrue(keys.containsAll(Set.of(prefix + k1, prefix + k2, prefix + k3, prefix + k4, prefix + k5)));
    }

    @Test
    public void scanTest() {

    }
}
