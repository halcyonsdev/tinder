package com.halcyon.tinder.rediscache

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CacheManager(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(CacheManager::class.java)

    fun save(key: String, value: Any, timeToLive: Duration) {
        redisTemplate.opsForValue().set(key, value, timeToLive)
        log.info("Cached value with key '{}' for '{}'", key, timeToLive.seconds)
    }

    fun save(key: String, timeToLive: Duration) {
        redisTemplate.opsForValue().set(key, "", timeToLive)
        log.info("Cached non with key '{}' for '{}'", key, timeToLive)
    }

    fun isPresent(key: String): Boolean {
        return redisTemplate.opsForValue().get(key) != null
    }

    fun <T> fetch(key: String, targetClass: Class<T>): T? {
        val value = redisTemplate.opsForValue().get(key) ?: return null
        return try {
            objectMapper.convertValue(value, targetClass)
        } catch (e: Exception) {
            log.error("Failed to convert cached value for key '{}'", key, e)
            null
        }
    }

    fun delete(key: String) {
        redisTemplate.delete(key)
    }
}