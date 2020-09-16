package com.yuhangma.redis.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moore
 * @since 2020/08/06
 */
@EnableAsync(proxyTargetClass = true)
@SpringBootApplication
@RestController
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class RedisLearningMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisLearningMainApplication.class, args);
    }

}
