package com.zun.ojbackendcommon.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author zunf
 * @date 2024/6/26 13:59
 */
@Component
public class RedisUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 普通获取键对应值
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 普通设置键值
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 普通设置键值并设置过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, String value, long time) {
        try {
            if (time > 0) {
                stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return 是否成功
     */
    public boolean del(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 指定缓存的失效时间
     *
     * @param key  键值 
     * @param time 时间(秒) 
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
