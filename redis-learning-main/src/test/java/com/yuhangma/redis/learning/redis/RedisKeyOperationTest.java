package com.yuhangma.redis.learning.redis;

import com.yuhangma.redis.learning.RedisLearningAppTest;
import org.junit.Test;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Redis 对于 key 的操作 test
 * <p></p>
 * TODO 2020/9/16 @Moore
 * 1. OBJECT
 * 6. RESTORE
 * 7. SORT
 * 8. TYPE
 * 9. SCAN
 *
 * @author Moore
 * @since 2020/09/15
 */
@SuppressWarnings("all")
public class RedisKeyOperationTest extends RedisLearningAppTest {

    /**
     * 删除一个 key
     *
     * @see <a href="http://redis.io/commands/del">Redis Documentation: EXISTS</a>
     * @see <a href="http://doc.redisfans.com/key/del.html">Redis 命令参考: DEL</a>
     */
    @Test
    public void deleteKeyTest() {
        // key 不存时删除，返回 false
        Boolean success1 = redisTemplate.delete(k1);
        assertFalse(success1);

        // 设置一个 key，然后删除，返回 true
        valueOps.set(k1, v1);
        Boolean success2 = redisTemplate.delete(k1);
        assertTrue(success2);

        // 添加 4 个，删除 5 个，删除成功 4 个，返回 4
        valueOps.set(k1, v1);
        valueOps.set(k2, v2);
        valueOps.set(k3, v3);
        valueOps.set(k4, v4);
        long deletedCount = redisTemplate.delete(List.of(k1, k2, k3, k4, k5));
        assertEquals(4, deletedCount);
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
        Boolean success1 = redisTemplate.hasKey(k1);
        assertFalse(success1);

        // key 存在时返回 true
        valueOps.set(k1, v1);
        Boolean success2 = redisTemplate.hasKey(k1);
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
        long expire1 = redisTemplate.getExpire(k1);
        assertEquals(-2, expire1);
        // 设置 k1 两秒后过期
        valueOps.set(k1, v1, 2, TimeUnit.SECONDS);

        // 过期时间大于 0 小于 2s
        long expire2 = redisTemplate.getExpire(k1, TimeUnit.SECONDS);
        assertTrue(expire2 > 0 && expire2 <= 2);

        // 休眠 2s 后，key 过期
        TimeUnit.SECONDS.sleep(2);
        long expire3 = redisTemplate.getExpire(k1);
        assertEquals(-2, expire3);

        // 设置一个持久化的 key，返回 -1
        valueOps.set(k2, v2);
        long expire4 = redisTemplate.getExpire(k2);
        assertEquals(-1, expire4);

        // 查看剩余时间，指定单位
        valueOps.set(k3, v3, 10, TimeUnit.SECONDS);
        long expire5 = redisTemplate.getExpire(k3, TimeUnit.MILLISECONDS);
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
        Boolean success1 = redisTemplate.expire(k1, 1, TimeUnit.SECONDS);
        assertFalse(success1);

        // 设置一个值，设置过期时间 1s，此时 key 存在
        valueOps.set(k1, v1);
        assertTrue(redisTemplate.hasKey(k1));
        // key 存在时，正常设置，返回 true
        boolean success2 = redisTemplate.expire(k1, 1, TimeUnit.SECONDS);
        assertTrue(success2);
        // 休眠 1s 后，key 不存在
        TimeUnit.SECONDS.sleep(1);
        assertFalse(redisTemplate.hasKey(k2));
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
        valueOps.set(k1, v1);
        // 设置一小时以后过期
        Date oneHourAgo = Date.from(OffsetDateTime.now().plusHours(1L).toInstant());
        boolean success = redisTemplate.expireAt(k1, oneHourAgo);
        assertTrue(success);

        // 过期时间在 3600s 左右
        long expireSeconds = redisTemplate.getExpire(k1, TimeUnit.SECONDS);
        assertTrue(3588 <= expireSeconds && expireSeconds <= 3600);
    }

    /**
     * 查询所有 key，支持通配符，时间复杂度 O(n)。
     * 虽然 redis 非常快，但是如果匹配到的 key 非常多的话，还是会影响性能。所以通常情况下生产环境会禁用此命令。
     * 有统计 key 的数量的需求可以用 set 来存所有的 key，然后客户端进行处理，或者使用 scan 命令来处理。
     *
     * @see RedisKeyOperationTest#scanTest()
     * @see <a href="http://redis.io/commands/keys">Redis Documentation: KEYS</a>
     * @see <a href="http://redis.io/commands/scan">Redis Documentation: SCAN</a>
     * @see <a href="http://doc.redisfans.com/key/keys.html">Redis 命令参考: KEYS</a>
     * @see <a href="http://doc.redisfans.com/key/scan.html">Redis 命令参考: SCAN</a>
     */
    @Test
    public void keysTest() {
        // 前面应该需要 flushdb，但是此操作比较危险，所以不在此演示。
        String prefix = "keys:test:";
        valueOps.set(prefix + k1, v1);
        valueOps.set(prefix + k2, v2);
        valueOps.set(prefix + k3, v3);
        valueOps.set(prefix + k4, v4);
        valueOps.set(prefix + k5, v5);

        // 查询所有匹配 keys:test:* 模式的 key
        Set<String> keys = Set.of(prefix + k1, prefix + k2, prefix + k3, prefix + k4, prefix + k5);
        Set<String> existKeys = redisTemplate.keys(prefix + "*");
        assertTrue(existKeys.containsAll(keys));

        // 清除测试新增的数据
        redisTemplate.delete(keys);
    }

    /**
     * 移除 key 的过期时间。在 redis 中，有过期时间的 key 被称为“volatile（易失的）”，没有过期时间的称为“persist（持久的）”。
     * <p>
     *     <ul>
     *         <li>1. 如果 key 不存在，或者本来就是持久的，返回 false</li>
     *         <li>2. 操作成功返回 true</li>
     *     </ul>
     * </p>
     *
     * @see <a href="http://redis.io/commands/persist">Redis Documentation: PERSIST</a>
     * @see <a href="http://doc.redisfans.com/key/persist.html">Redis 命令参考: PERSIST</a>
     */
    @Test
    public void persistTest() {
        // 不存在时返回 false
        boolean success1 = redisTemplate.persist(k1);
        assertFalse(success1);

        // key 为持久的，返回 false
        valueOps.set(k1, v1);
        boolean success2 = redisTemplate.persist(k1);
        assertFalse(success2);

        // 给 k1 设置过期时间，返回 true
        redisTemplate.expire(k1, 1L, TimeUnit.DAYS);
        boolean success3 = redisTemplate.persist(k1);
        assertTrue(success3);
    }

    /**
     * 从数据库中随机取出一个 key，若库为空，则返回 null，若库非空，则随机返回一个 key
     *
     * @see <a href="http://redis.io/commands/randomkey">Redis Documentation: RANDOMKEY</a>
     * @see <a href="http://doc.redisfans.com/key/randomkey.html">Redis 命令参考: RANDOMKEY</a>
     */
    @Test
    public void randomKeyTest() {
        // 数据库为空时，返回 null
        String key1 = redisTemplate.randomKey();
        assertNull(key1);

        valueOps.set(k1, v1);
        valueOps.set(k2, v2);
        valueOps.set(k3, v3);
        valueOps.set(k4, v4);
        valueOps.set(k5, v5);

        String key2 = redisTemplate.randomKey();
        boolean result = List.of(k1, k2, k3, k4, k5).contains(key2);
        assertTrue(result);
    }

    /**
     * 对 key 进行重命名
     * <p>
     *     <ul>
     *         <li>1. oldKey 不存在时，返回一个错误，会被封装成 {@link RedisSystemException} 异常</li>
     *         <li>2. newKey 已经存在时，newKey 会被覆盖</li>
     *         <li>3. RENAMENX：只有 newKey 不存在时才执行重命名操作，对应的方法为 {@link RedisTemplate#renameIfAbsent(Object, Object)}</li>
     *     </ul>
     * </p>
     *
     * @param oldKey 旧的 key 的名称
     * @param newKey 新的 key 的名称
     * @see <a href="http://redis.io/commands/rename">Redis Documentation: RENAME</a>
     * @see <a href="http://redis.io/commands/renamenx">Redis Documentation: RENAMENX</a>
     * @see <a href="http://doc.redisfans.com/key/rename.html">Redis 命令参考: RENAME</a>
     * @see <a href="http://doc.redisfans.com/key/renamenx.html">Redis 命令参考: RENAMENX</a>
     */
    @Test
    public void renameKeyTest() {
        // 预期异常
        expectedException.expect(RedisSystemException.class);
        expectedException.expectMessage("ERR no such key");

        // k1 不存在的时候，抛出错误，ERR no such key
        String oldKey = k1;
        String newKey = k2;
        redisTemplate.rename(k1, k2);

        // 正常 case
        valueOps.set(k1, v1);
        redisTemplate.rename(k1, k2);
        boolean success1 = redisTemplate.hasKey(k1);
        assertFalse(success1);
        boolean success2 = redisTemplate.hasKey(k2);
        assertFalse(success2);

        // RENAMENX：newKey 不存在返回 false
        valueOps.set(k3, v3);
        valueOps.set(k4, v4);
        boolean success3 = redisTemplate.renameIfAbsent(k3, k4);
        assertTrue(success3);
        boolean success4 = redisTemplate.renameIfAbsent(k4, k5);
        assertFalse(success4);
    }

    /**
     * 判断 key 的数据类型：none、string、hash、list、set、sorted_set
     *
     * @see DataType
     * @see <a href="http://redis.io/commands/type">Redis Documentation: TYPE</a>
     * @see <a href="http://doc.redisfans.com/key/type.html">Redis 命令参考: TYPE</a>
     */
    @Test
    public void typeTest() {
        // key 不存在时返回 none
        assertEquals(DataType.NONE, redisTemplate.type(k1));

        valueOps.set(k1, v1);
        DataType type = redisTemplate.type(k1);
        assertEquals(DataType.STRING, type);
    }

    /**
     * 增量迭代
     */
    @Test
    public void scanTest() {

    }
}
