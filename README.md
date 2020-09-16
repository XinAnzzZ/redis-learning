<h1 align="center">Welcome to Redis-Learning Project 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-1.0.0--SNAPSHOT-blue.svg?cacheSeconds=2592000" />
</p>

> A Redis learning project

### 🏠 [Homepage](https://yuhangma.com)

## Install

本项目是一个 Spring data redis 的使用示例项目，使用 Maven 3.6.1 版本 + JDK 11 + Spring Boot 2.0.4-RELEASE 版本进行开发，部分代码使用到了 JDK 11 版本的新特性，例如使用了类型推断 `var` 以及部分新的 API，使用 JDK8 的小伙伴可以将这部分代码改为 JDK8 的语法即可。

若使用JDK11，Spring Boot 启动时会抛出以下错误，这是升级到JDK 9以上版本之后 [Netty](https://github.com/netty/netty/issues/7769) 的一个兼容性问题，这两个错误不影响应用正常使用，只不过有些Netty的优化没法生效。详见 [ISSUE](https://github.com/netty/netty/issues/7769).

```text
java.lang.UnsupportedOperationException: Reflective setAccessible(true) disabled

java.lang.IllegalAccessException: class io.netty.util.internal.PlatformDependent0$6 cannot access class jdk.internal.misc.Unsafe (in module java.base)
```

## Author

👤 **Moore.Ma**

* Website: https://yuhangma.com/
* Github: [@XinAnzzZ](https://github.com/XinAnzzZ)

## Show your support

Give a ⭐️ if this project helped you!

***
_This README was generated with ❤️ by [readme-md-generator](https://github.com/kefranabg/readme-md-generator)_