package com.yuhangma.redis.learning.controller;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Proxy;

/**
 * @author Moore
 * @since 2020/11/12
 */
public class ProxyTestImpl implements ProxyTest {

    static class PersonService {
        public String sayHello(String name) {
            return "Hello " + name;
        }

        public Integer lengthOfName(String name) {
            return name.length();
        }
    }

    public static void main(String[] args) {
        JDKProxyTest();
        CGLibProxyTest();
    }

    private static void CGLibProxyTest() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PersonService.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                return "Hello Tom!";
            } else {
                return proxy.invokeSuper(obj, args);
            }
        });

        PersonService proxy = (PersonService) enhancer.create();

        assert proxy.sayHello(null).equals("Hello Tom!");
        assert proxy.lengthOfName("Mary") == 4;
    }


    private static void JDKProxyTest() {
        ProxyTest proxyTest = (ProxyTest) Proxy.newProxyInstance(
                ProxyTest.class.getClassLoader(),
                new Class[]{ProxyTest.class},
                (proxy, method, args1) -> {
                    method.invoke(new ProxyTestImpl());
                    System.out.println("proxy run...");
                    return proxy;
                });
        proxyTest.run();
    }

    @Override
    public void run() {
        System.out.println("run...");
    }
}
