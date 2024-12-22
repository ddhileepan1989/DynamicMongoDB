package org.mentorbridge.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class RedisConfig {

    /*
     * Configures the default cache configuration for Redis.
     * This method defines the general settings for all caches managed by the RedisCacheManager.
     * - Default TTL (Time-to-Live) is set to 30 days for all caches.
     * - Caching of null values is disabled to avoid storing `null` entries in Redis.
     * - Values are serialized using the GenericJackson2JsonRedisSerializer, which stores cache values as JSON.
     *
     * The configuration ensures that cache entries are kept for 30 days and no null values are stored,
     * providing more efficient memory usage and better data consistency.
     *
     * @return RedisCacheConfiguration - the default cache configuration for Redis.*/


    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(30))  // Set the default TTL (Time-to-Live) for all caches to 30 days.
                .disableCachingNullValues()  // Disable caching of null values (to avoid storing empty cache entries).
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));  // Use Jackson for JSON serialization of cache values.
    }

    /*
     * Customizes the RedisCacheManager with specific cache configurations.
     * This method allows customization for a particular cache, "dbNames", with a specific configuration.
     * - The TTL for the "dbNames" cache is set to 30 days, which is the same as the default TTL in this case.
     * - Custom configurations for specific caches are useful when different caches require different settings (e.g., TTL).
     *
     * In this example, the TTL for "dbNames" is set to 30 days. This is to demonstrate how we can configure
     * specific caches with their own TTLs, even if the default TTL is different.
     *
     * @return RedisCacheManagerBuilderCustomizer - a customizer to configure the RedisCacheManager with specific cache settings.*/


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder cacheManagerBuilder = RedisCacheManager.builder(redisConnectionFactory);

        // Set default cache configuration for all caches
        cacheManagerBuilder.cacheDefaults(cacheConfiguration());

        // Additional cache configurations can be added for specific cache names here if needed
        // Example: Cache for 'dbNames' cache with a custom TTL
        cacheManagerBuilder.withCacheConfiguration("dbNames", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(10)));

        // Build and return the cache manager
        return cacheManagerBuilder.build();
    }
}
