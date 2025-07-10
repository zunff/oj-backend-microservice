package com.zun.ojbackendcommon.manager;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author zunf
 * @date 2024/9/2 15:27
 */
@Component
public class RedisManager {
    private final static String APP_INFO = "oj:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(getCacheKey(key), value);
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(getCacheKey(key), value, timeout, unit);
    }


    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(getCacheKey(key));
    }


    public String get(String key, long start, long end) {
        return stringRedisTemplate.opsForValue().get(getCacheKey(key), start, end);
    }

    public void expire(String key, int time, TimeUnit timeUnit) {
        stringRedisTemplate.expire(getCacheKey(key), time, timeUnit);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public void delete(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }

    public Collection<String> keys(String pattern) {
        return stringRedisTemplate.opsForValue().getOperations().keys(pattern);
    }


    /**
     * 添加元素到有序集合
     *
     * @param key    有序集合的键
     * @param value  元素的值
     * @param score  元素的分数
     */
    public void sortSetAdd (String key, String value, double score) {
        stringRedisTemplate.opsForZSet().add(getCacheKey(key), value, score);
    }

    /**
     * 移除有序集合中的元素
     *
     * @param key    有序集合的键
     * @param values 要移除的元素值
     */
    public void sortSetRemove(String key, Object... values) {
        stringRedisTemplate.opsForZSet().remove(getCacheKey(key), values);
    }

    /**
     * 更新有序集合中指定成员的分数
     *
     * @param key    有序集合的键
     * @param value  元素的值
     * @param newScore  新的分数
     */
    public void sortSetUpdateScore(String key, String value, double newScore) {
        Double score = stringRedisTemplate.opsForZSet().score(getCacheKey(key), value);
        if (score == null) {
            return;
        }
        stringRedisTemplate.opsForZSet().incrementScore(getCacheKey(key), value, newScore - score);
    }

    /**
     * 获取有序集合中指定范围内的元素
     *
     * @param key    有序集合的键
     * @param start  开始位置（0 表示第一个元素）
     * @param end    结束位置（-1 表示最后一个元素）
     * @return 指定范围内的元素集合
     */
    public Set<String> range(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(getCacheKey(key), start, end);
    }

    /**
     * 获取有序集合中指定范围内的元素及其分数
     *
     * @param key    有序集合的键
     * @param start  开始位置（0 表示第一个元素）
     * @param end    结束位置（-1 表示最后一个元素）
     * @return 包含元素及其分数的集合
     */
    public Set<ZSetOperations.TypedTuple<String>> rangeWithScores(String key, long start, long end) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().rangeWithScores(getCacheKey(key), start, end);
        if (typedTuples == null) {
            return new HashSet<>();
        }
        return typedTuples;
    }

    /**
     * 尝试获取锁、默认60s过期
     *
     * @param key key
     * @return 是否获取成功
     */
    public boolean tryLock(String key) {
        return tryLock(key, 60);
    }

    /**
     * 尝试获取锁
     *
     * @param key key
     * @param seconds ttl 单位s
     * @return 是否获取成功
     */
    public boolean tryLock(String key, long seconds) {
        // 防止释放错锁，value存应用名+线程id
        long threadId = Thread.currentThread().getId();
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                .setIfAbsent(getLockKey(key), APP_INFO + threadId, seconds, TimeUnit.SECONDS));
    }

    /**
     * 释放锁
     *
     * @param key key
     */
    public void unLock(String key) {
        // 利用 应用名+线程id 判断是不是当前线程，防止释放错锁
        long threadId = Thread.currentThread().getId();
        // 使用lua脚本保证操作的原子性
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        stringRedisTemplate.execute(redisScript, Collections.singletonList(getLockKey(key)), APP_INFO + threadId);
    }



    /**
     * @param key
     * @return
     */
    private String getCacheKey(String key) {
        /**
         * 缓存的名称空间
         */
        return APP_INFO + key;
    }

    /**
     * @param key
     * @return
     */
    private String getLockKey(String key) {
        /**
         * 锁的名称空间
         */
        return APP_INFO + "lock:" + key;
    }
}
