# Swallowtail Butterfly

**版本：** `0.0.1-SNAPSHOT`  
**Java：** 21  
**构建工具：** Maven  
**当前状态：** 初始微服务基础版本

## 1. 项目定位

这是一个基于 Spring Boot、Spring Cloud Alibaba 与 Nacos 的微服务基础工程。当前版本聚焦于：

- 统一的第三方依赖版本管理；
- 公共响应、异常处理等基础能力；
- JWT 本地解析、Redis 黑名单校验；
- 认证服务的登录、登出、令牌签发与刷新；
- 网关侧的非阻塞令牌校验与服务路由；
- Nacos 服务发现。

> Nacos 是外部基础设施，不由本项目内的 Java 模块实现或启动。

## 2. 当前模块

```text
common-starter/                         父 POM：聚合、构建与 Java 21 配置
├── ai-dependencies/                    第三方依赖版本 BOM
├── ai-common-core/                     公共基础库
├── ai-security/                        Token 本地校验与黑名单能力
├── ai-module-auth/                     认证服务
├── ai-getway/                          API 网关（保留现有模块命名）
├── ai-registry/                        Nacos 接入配置示例服务
└── case-examples/                      独立功能示例
```

### 2.1 `common-starter`

根模块使用 `pom` 打包，职责仅限于：

- 聚合当前模块；
- 统一项目版本与 Java 21 编译参数；
- 统一 Maven Compiler、Surefire、Spring Boot 插件版本及构建配置；
- 在 `dependencyManagement` 中导入 `ai-dependencies`，将第三方版本管理传递给以它为 parent 的服务。

业务服务应直接以 `common-starter` 为 parent，或让自己的公共 parent 继承它。

### 2.2 `ai-dependencies`

`ai-dependencies` 是独立的 Maven BOM（`pom` 打包），**只管理第三方依赖版本**，不依赖或聚合任何内部业务模块。

已统一管理的主要依赖包括：Spring Boot、Spring Cloud、Spring Cloud Alibaba、Nacos 相关 starter、DJL、LangChain4j、MyBatis、数据库驱动、Lombok、Fastjson2、EasyExcel 与 JJWT。

它不能继承 `common-starter`，因为根 POM 会导入该 BOM；这样可避免 Maven 的 parent / import 循环。

### 2.3 `ai-common-core`

公共基础库，当前提供：

- `Result` 统一响应；
- `BusinessException` 与全局异常处理；
- 常用工具、日志切面与自动配置。

Maven 坐标：

```xml
<dependency>
    <groupId>com.zxy</groupId>
    <artifactId>ai-common-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Java 包名维持为合法的 `com.zxy.core`（Java 包名不能包含连字符 `-`）；模块目录和 Maven artifactId 已统一为 `ai-common-core`。

### 2.4 `ai-security`

供业务服务和网关共用的安全能力，负责本地令牌验证，不承担用户登录管理：

- 解析 `Authorization: Bearer <token>`；
- 校验 JWT 签名、格式和过期时间；
- 通过 Redis 校验 Token 黑名单；
- 提供 Servlet Filter 与 WebFlux 非阻塞 Filter；
- 将已认证用户信息写入后续处理链路。

Maven 坐标：

```xml
<dependency>
    <groupId>com.zxy</groupId>
    <artifactId>ai-security</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2.5 `ai-module-auth`

认证中心，当前直接依赖 `ai-common-core` 与 `ai-security`，负责：

- 登录与用户凭据校验；
- Access Token / Refresh Token 签发与刷新；
- 登出时将有效 Token 写入 Redis 黑名单；
- 注册到 Nacos。

默认端口和 Nacos、Redis、JWT 等运行参数以 `ai-module-auth/src/main/resources/application.yml` 为准。

### 2.6 `ai-getway`

API 网关，当前直接依赖 `ai-security`，通过 WebFlux Filter 复用非阻塞 JWT 与黑名单校验逻辑，并负责：

- 路由到已注册服务；
- 放行登录、健康检查等公开端点；
- 拒绝无效、过期或已注销的令牌；
- 注册到 Nacos。

> 模块目录和 artifactId 当前均为 `ai-getway`，为避免破坏现有坐标暂不在本次调整中更正拼写。

### 2.7 `ai-registry` 与 `case-examples`

- `ai-registry`：Nacos 接入的配置示例服务；实际注册中心应部署独立 Nacos Server。
- `case-examples`：不参与认证主链路的独立示例。

## 3. 依赖关系

```text
common-starter
  └── import ai-dependencies (第三方版本 BOM)

ai-common-core ──┐
                 ├── ai-security
                 │      ├── ai-module-auth
                 │      └── ai-getway
                 └── ai-module-auth
```

- 内部模块依赖必须显式声明，不能通过 `ai-dependencies` 获得。
- 第三方依赖在子模块中只声明 `groupId` / `artifactId`，版本由父链路导入的 BOM 控制。

## 4. 运行前提

- JDK 21；
- Maven 3.9+；
- Nacos Server（默认示例地址：`localhost:8848`）；
- Redis（认证登录、登出黑名单功能需要）；
- 启用实际账号存储后所需的数据库。

所有服务必须使用相同的 JWT 密钥，并连接到同一个 Redis 黑名单空间，否则网关与业务服务无法正确识别认证中心签发或注销的令牌。

## 5. 构建与启动

在根目录执行：

```powershell
mvn clean verify
```

本地启动顺序建议：

1. 启动 Nacos；
2. 启动 Redis；
3. 启动 `ai-module-auth`；
4. 启动 `ai-getway`；
5. 启动后续业务服务。

```powershell
Set-Location .\ai-module-auth
mvn spring-boot:run

Set-Location ..\ai-getway
mvn spring-boot:run
```

## 6. 当前认证流程

```text
Client
  │ POST /api/auth/login
  ▼
ai-module-auth ── 签发 JWT ──► Client

Client ── Authorization: Bearer <JWT> ──► ai-getway
                                            │
                                            ├─ 非阻塞校验 JWT
                                            ├─ 查询 Redis 黑名单
                                            └─ 路由至业务服务

业务服务 ── ai-security Servlet Filter ──► 再次本地校验 JWT / 黑名单

Client ── POST /api/auth/logout ──► ai-module-auth ──► Redis 黑名单
```

## 7. 后续目标（未实现）

以下内容不是当前模块，不应视为可用功能：

- 示例业务服务与 RBAC 权限管理；
- 单体应用可切换的内嵌认证管理模块；
- OAuth2 / OIDC；
- AI 数据处理、模型管理、训练与推理模块；
- 分布式链路追踪、指标监控与告警；
- 消息队列、限流、熔断和动态网关路由管理。
