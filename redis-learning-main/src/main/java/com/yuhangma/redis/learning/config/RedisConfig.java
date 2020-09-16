package com.yuhangma.redis.learning.config;

import com.yuhangma.redis.learning.model.PersonDTO;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Moore
 * @since 2020/08/25
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig {

    /**
     * 配置 redis template
     * 配置 value 序列化器，默认值为 {@link JdkSerializationRedisSerializer}
     *
     * @see RedisAutoConfiguration
     * @see JdkSerializationRedisSerializer
     * @see RedisTemplate#afterPropertiesSet()
     */
    @Bean
    public RedisTemplate<String, PersonDTO> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, PersonDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
