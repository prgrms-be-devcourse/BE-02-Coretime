package com.prgrms.coretime.common.util;

import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

  private final RedisTemplate<String, String> redisTemplate;

  public RedisService(
      RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void setValues(String key, String data) {
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    values.set(key, data);
  }

  public void setValues(String key, String data, Duration duration) {
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    values.set(key, data, duration);
  }

  public String getValues(String key) {
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    return values.get(key);
  }

  public void deleteValues(String key) {
    redisTemplate.delete(key);
  }
}
