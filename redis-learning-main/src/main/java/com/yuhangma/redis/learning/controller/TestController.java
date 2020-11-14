package com.yuhangma.redis.learning.controller;

import com.yuhangma.redis.learning.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moore
 * @since 2020/09/14
 */
@Slf4j
@RestController
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    public void run() {
        test();
        log.error("run:" + Thread.currentThread().getName());
    }

    @Async
    public void test() {
        log.error("test:" + Thread.currentThread().getName());
    }
}
