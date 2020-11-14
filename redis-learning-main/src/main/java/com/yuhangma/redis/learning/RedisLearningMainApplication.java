package com.yuhangma.redis.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moore
 * @since 2020/08/06
 */
@EnableAsync
@SpringBootApplication
@RestController
public class RedisLearningMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisLearningMainApplication.class, args);
    }

}
