# case-examples

该模块是独立案例模块，仅用于示例，不依赖自动装配去影响其他业务模块的 Spring 上下文。

## 包结构

- `com.zxy.examples.easyexcel`
  - `EasyExcelNativeCase`: EasyExcel 原生导入导出示例
  - `UserExcelRow`: Excel 行模型
- `com.zxy.examples.concurrent.spring`
  - `SpringCompletableFutureCase`: Spring 线程池 + CompletableFuture 示例（手动创建线程池，不使用 `@EnableAsync`、`@Async`）
- `com.zxy.examples.concurrent.jdk21`
  - `Jdk21ConcurrencyCase`: JDK21 虚拟线程示例（运行时自动探测，低版本 JDK 自动降级）
- `com.zxy.examples.multids`
  - `StaticMultiDataSourceConfiguration`: 静态多数据源（mysql/mysql1/mysql2/pg/oracle）配置
  - `@DataSourceKey`: 通过注解在 service、mapper 层切换数据源
  - `DataSourceSwitchAspect`: AOP 切面切换数据源并支持嵌套恢复
  - `MultiDataSourceCaseRunner`: main 入口，手动运行案例

## 运行测试

```powershell
cd D:\work\workspace\ideaSpace\swallowtail-butterfly\common-starter
mvn -pl case-examples -am test
```

## 说明

- JDK21 多线程案例使用反射调用 `Executors.newVirtualThreadPerTaskExecutor()`。
- 当运行环境不是 JDK21 时，会自动使用固定线程池作为 fallback，保证案例可运行。
- 该模块的配置通过手动创建上下文触发，不会自动并入其他模块默认 Spring 上下文。

## 静态多数据源案例说明

- 该案例使用 `AnnotationConfigApplicationContext` 手动创建上下文，仅在案例进程内生效。
- 数据源 key 支持示例：`mysql`、`mysql1`、`mysql2`、`pg`、`oracle`，可继续静态扩展。
- 可在 service、mapper 接口方法上通过 `@DataSourceKey("...")` 切换数据源。
- Mapper 案例以接口方式演示，接近 MyBatis mapper 接口的注解使用形式。

