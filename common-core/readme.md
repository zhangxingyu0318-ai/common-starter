# Common Core Module

通用核心工具库，提供Spring Boot项目的基础工具和通用组件。

## 🚀 快速开始

**步骤1：引入依赖**
```xml
<dependency>
    <groupId>com.zxy</groupId>
    <artifactId>common-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

**步骤2：在配置类上启用**
```java
@Configuration
@EnableCommonCore  // 启用common-core功能
public class YourAppConfig {
}
```

**步骤3：直接使用（无需任何额外注解！）**
```java
@RestController
public class UserController {
    
    @GetMapping("/user/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        // ✅ 自动记录日志：入参、出参、耗时、异常
        // ✅ 异常会被自动捕获并转换为Result格式
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return Result.ok(user);
    }
}
```

**全自动！无需标注任何额外注解！**

## 📋 核心功能

### 1. 全局异常处理 🚨
自动捕获并转换异常为统一的Result格式

### 2. 方法执行日志 📝
自动记录所有@RestController和@Controller中public方法的：
- ✅ 入参信息
- ✅ 出参信息
- ✅ 执行耗时
- ✅ 异常详情

## 🔧 工作原理

### 注解启用流程

```
在子项目配置类上标注 @EnableCommonCore
        ↓
@Import(CommonCoreAutoConfiguration.class)
        ↓
导入自动配置类
        ↓
检查条件：
  ✅ @ConditionalOnClass(RestController.class)
  ✅ @ConditionalOnMissingBean
  ✅ @ConditionalOnProperty(zxy.exception-handling.enabled=true)
        ↓
注册 GlobalExceptionHandler 为 Bean
注册 LoggingAspect 为 Bean
        ↓
自动拦截所有Controller的public方法
        ↓
全局异常处理 + 日志记录生效 🎉
```

### 自动日志拦截范围

```
@RestController 标注的类
        ├── public 方法 ✅ 自动记录日志
        └── 其他方法 ❌ 不记录

@Controller 标注的类
        ├── public 方法 ✅ 自动记录日志
        └── 其他方法 ❌ 不记录
```

## 📚 核心API

### Result 统一响应对象
```java
// 成功响应
Result.ok(data);
Result.ok();

// 错误响应
Result.error();
Result.error(errorData);
```

### BusinessException 业务异常
```java
// 抛出业务异常
throw new BusinessException("用户不存在");

// 带错误码的业务异常
throw new BusinessException("USER_001", "用户状态异常");
```

### @EnableCommonCore 启用注解
```java
@Configuration
@EnableCommonCore  // 显式启用common-core功能
public class YourAppConfig {
}
```

## 📁 核心文件说明

### 1. CommonCoreAutoConfiguration
```java
@Configuration
@ConditionalOnClass(RestController.class)
public class CommonCoreAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    @ConditionalOnProperty(name = "zxy.exception-handling.enabled", ...)
    public GlobalExceptionHandler globalExceptionHandler() { ... }
    
    @Bean
    @ConditionalOnMissingBean(LoggingAspect.class)
    public LoggingAspect loggingAspect() { ... }
}
```

### 2. GlobalExceptionHandler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) { ... }
    // ... 其他异常处理
}
```

### 3. LoggingAspect
```java
@Aspect
@Component
public class LoggingAspect {
    @Around("@within(RestController) && execution(public * *(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        // 自动记录所有RestController的public方法
    }
}
```

## ⚡ 日志输出示例

```
// 正常执行
>>> 【UserController.getUser】方法执行开始 | 入参: 1
<<< 【UserController.getUser】方法执行成功 | 出参: Result(data=User(...), ...) | 耗时: 45ms

// 异常执行
>>> 【UserController.getUser】方法执行开始 | 入参: null
!!! 【UserController.getUser】方法执行异常 | 异常: 用户ID不能为空 | 耗时: 2ms
```

## 📋 启用方式

### 唯一方式：通过注解启用 ✨
```java
@Configuration
@EnableCommonCore  // 显式启用common-core功能
public class YourAppConfig {
}
```
✅ **全自动启用** - 无需任何其他配置

### 可选：通过配置禁用异常处理 🔌
```yaml
# 子项目application.yml
zxy:
  exception-handling:
    enabled: false  # 禁用全局异常处理（默认true）
```

## ❓ 常见问题

**Q: 必须使用 @EnableCommonCore 注解吗？**
A: 是的。这样可以让子项目明确知道启用了哪些功能。

**Q: 所有Controller方法都会记录日志吗？**
A: 是的，所有@RestController和@Controller中的public方法都会自动记录。

**Q: 日志会影响性能吗？**
A: 不会。日志记录是异步的，对业务逻辑执行时间影响极小。

**Q: 如何限制日志输出的内容大小？**
A: LoggingAspect 内部已限制字符串输出为200字符，避免日志过大。

**Q: 可以自定义异常处理吗？**
A: 可以。在子项目中创建自己的 `@RestControllerAdvice` 即可，会与全局异常处理合并。

## 🛠 技术栈

- Java 8+
- Spring Boot（Web可选）
- Spring AOP
- Lombok
- Apache Commons工具库

## ✨ 核心特性总结

- ✅ **显式启用** - 通过@EnableCommonCore注解启用
- ✅ **全自动** - 无需任何额外标注
- ✅ **全局异常处理** - 统一异常为Result格式
- ✅ **自动日志记录** - 所有Controller方法自动记录
- ✅ **完整信息** - 记录入参、出参、耗时、异常
- ✅ **可配置** - 支持禁用异常处理
- ✅ **性能友好** - 日志输出大小受限
- ✅ **无入侵** - 对子项目代码零污染
