package com.yuhangma.redis.learning.controller;

import com.yuhangma.redis.learning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moore
 * @since 2020/09/14
 */
@RestController
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    @Transactional
    public void run() {

    }
}
