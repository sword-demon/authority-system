package top.wjstar.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存缓存
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     */
    public void set(String key, String value, Long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 取缓存
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 清除缓存
     *
     * @param key
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }
}
