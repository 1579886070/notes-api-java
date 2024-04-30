package com.zxxwl.common.api.redis;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis实现类
 *
 * @author qingyu
 * @since 2022.12.01
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisServiceImpl<V> implements RedisService<V> {


    private final RedisTemplate<String, V> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    /*@Resource(name = "objectMapper")
    private ObjectMapper objectMapper;*/

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public boolean hExists(String key, String hashKey) {
        return false;
    }

    /**
     * FIXME 是否需要校验 缓存成功？
     *
     * @param key   key
     * @param value value
     * @param time  time
     * @return R
     */
    @Override
    public boolean set(String key, V value, long time) {
        try {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void set(String key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public V get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Long del(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    @Override
    public Boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    @SneakyThrows
    @Override
    public V hGet(String key, String hashKey) {
        HashOperations<String, String, V> hashOperations = redisTemplate.opsForHash();
        return (V) hashOperations.get(key, hashKey);
    }

    @SneakyThrows
    @Override
    public Optional<V> hGet2(String key, String hashKey) {
        HashOperations<String, String, V> hashOperations = redisTemplate.opsForHash();
        return Optional.ofNullable(hashOperations.get(key, hashKey));
    }

    @SneakyThrows
    @Override
    public V hGetString(String key, String hashKey) {
        HashOperations<String, String, V> hashOperations = stringRedisTemplate.opsForHash();
        return hashOperations.get(key, hashKey);
    }

    @Override
    public Boolean hSet(String key, String hashKey, V value, long time) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        return expire(key, time);
    }

    @Override
    public void hSet(String key, String hashKey, V value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public void hSetString(String key, String hashKey, V value) {
        HashOperations<String, String, V> hashOperations = stringRedisTemplate.opsForHash();
        hashOperations.put(key, hashKey, value);
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Boolean hSetAll(String key, Map<String, V> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        return expire(key, time);
    }

    @Override
    public void hSetAll(String key, Map<String, V> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void hDel(String key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public Long hIncr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    @Override
    public Long hDecr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    @Override
    public Long sAdd(String key, V... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Long sAdd(String key, long time, V... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, time);
        return count;
    }

    @Override
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public List<V> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public V lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    @Override
    public Long lPush(String key, V value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Long lPushAll(String key, Collection<V> value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    @Override
    public Long lPush(String key, V value, long time) {
        Long index = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time);
        return index;
    }
/*

    @Override
    public Long lPushAll(String key, V... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }
*/

/*    @Override
    public Long lPushAll(String key, Long time, V... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, time);
        return count;
    }*/

    @Override
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public DataType type(String key) {
        return redisTemplate.type(key);
    }

    @Override
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    @Override
    public Boolean zAdd(String key, V value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    @Override
    public Set<V> zGet(String key, long start, long stop) {
        return redisTemplate.opsForZSet().range(key, start, stop);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<V>> rangeWithScores(String key, long start, long stop) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, stop);
    }

    @Override
    public Long zCount(String key, long start) {
        return redisTemplate.opsForZSet().count(key, start, Double.MAX_VALUE);
    }

    @Override
    public Long zCount(String key, long start, long stop) {
        return redisTemplate.opsForZSet().count(key, start, stop);
    }

    @Override
    public Long zDel(String key, long stop) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, 0, stop);

    }

    @Override
    public Long zDel(String key, long start, long stop) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, start, stop);

    }

    @Override
    public Long zRem(String key, V value) {
        return redisTemplate.opsForZSet().remove(key, value);
    }

    @Override
    public Long zDelAll(String key) {
        Long size = redisTemplate.opsForZSet().size(key);
        return redisTemplate.opsForZSet().removeRange(key, 0, size);
    }
}
