# 通用AI平台模块结构文档

## 项目概述

通用AI平台是一个模块化的框架，提供Spring Boot基础功能和机器学习/深度学习能力。通过统一的starter包为应用提供完整的开发工具包。

## 模块架构

```
common (父POM)
├── common-core       # 通用核心库 - Spring Boot工具、数据库连接、配置等
├── ai-data           # 数据处理模块 - 数据预处理、特征工程、数据加载
├── ai-model          # 模型管理模块 - 模型版本控制、模型库管理
├── ai-training       # 训练功能模块 (整合版)
│   ├── upload        # 训练结果上传和验证
│   ├── predict       # 基于训练结果的预测服务
│   └── manage        # 训练任务管理和监控
└── common-starter    # 启动器 - 自动配置所有模块功能
```

## 模块说明

### 1. common-core (通用核心模块)
**职责**: 提供通用的Spring Boot工具和基础组件
- 统一API响应对象 (`Result<T>`)
- 通用工具类和扩展方法
- 基础配置模板

**主要包**: `com.zxy.core`
**依赖**: Lombok, Apache Commons工具库

---

### 2. ai-data (AI数据处理模块)
**职责**: AI场景下的数据预处理和特征工程
- 数据加载和清洗
- 数据预处理流程
- 特征提取和转换
- 数据验证和约束

**主要包**: `com.zxy.ai.data`
**依赖**: common-core, DJL BasicDataset

---

### 3. ai-model (AI模型管理模块)
**职责**: 模型的版本控制和管理
- 模型元数据管理
- 模型版本控制
- 模型库接口

**主要包**: `com.zxy.ai.model`
**依赖**: common-core

---

### 4. ai-training (AI训练功能整合模块)
**职责**: 统一的训练相关功能（v2.0后的架构）
- **upload** - Python训练结果上传和验证
- **predict** - 基于训练模型的预测推理
- **manage** - 训练任务管理和监控

**主要包**: 
- `com.zxy.ai.training.upload`
- `com.zxy.ai.training.predict`
- `com.zxy.ai.training.manage`

**依赖**: common-core, DJL API, DJL PyTorch Engine, MyBatis

---

### 5. common-starter (启动器模块)
**职责**: 自动配置和统一导出所有功能
- 自动装配所有通用和AI模块
- 统一的entry point
- 配置类集中管理
- 一行依赖获得完整功能

**主要类**: `AiPlatformAutoConfiguration`
**包扫描范围**:
- `com.zxy.core`
- `com.zxy.ai.data`
- `com.zxy.ai.model`
- `com.zxy.ai.training`

---

## 使用指南

### 快速开始

在您的项目中引入common-starter依赖，即可获得完整的Spring Boot + AI能力：

```xml
<dependency>
    <groupId>com.zxy</groupId>
    <artifactId>common-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 功能分类

#### 通用功能（common-core）
- Spring Boot自动配置
- 数据库ORM
- 通用工具类
- 日志和监控

#### AI能力（ai-* 系列）
- 数据处理 (ai-data)
- 模型管理 (ai-model)
- 训练上传 (ai-training.upload)
- 模型预测 (ai-training.predict)
- 训练管理 (ai-training.manage)

### 完整功能列表

| 功能 | 模块 | 说明 |
|------|------|------|
| Spring Boot工具 | common-core | 基础框架工具和配置 |
| 数据处理 | ai-data | 数据预处理、特征工程 |
| 模型管理 | ai-model | 模型版本控制和元数据 |
| 训练上传 | ai-training.upload | Python模型训练结果上传 |
| 模型预测 | ai-training.predict | 基于模型的预测服务 |
| 训练管理 | ai-training.manage | 训练任务监控和管理 |

### 配置示例

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_platform
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update

ai:
  training:
    model-path: /models
    upload-path: /uploads
```

## 版本更新历史

### v2.0 (当前)
- ✅ 整合training-manage、training-predict、training-upload为单一ai-training模块
- ✅ 移除ai-inference模块（推理功能由ai-training.predict承担）
- ✅ 重新定位common-core为通用基础框架（不限于AI）
- ✅ 重新命名ai-starter为common-starter，体现其双重性质
- ✅ 优化模块依赖关系
- ✅ 统一包结构为com.zxy.ai.training.*

### v1.0 (历史)
- 分散的training模块结构
- 包含独立的ai-inference模块
- ai-core主要面向AI场景

## 构建和测试

### 全量构建
```bash
cd common
mvn clean install
```

### 单个模块构建
```bash
mvn clean install -pl common-starter -am
```

### 运行测试
```bash
mvn clean test
```

## 依赖关系图

```
common-starter
├── common-core ✓
├── ai-data ✓
│   └── common-core ✓
├── ai-model ✓
│   └── common-core ✓
└── ai-training ✓
    └── common-core ✓
```

## 技术栈

- **框架**: Spring Boot 2.7.18
- **数据**: MyBatis 2.3.1
- **AI/ML**: DJL (Deep Java Library) 0.28.0
- **语言模型**: LangChain4j 0.31.0
- **数据库**: MySQL 8.3.0, PostgreSQL 42.7.3

## 常见问题

**Q: common-core和ai-*模块的区别是什么？**
A: common-core提供通用的Spring Boot工具和基础功能（适用于任何项目），ai-*模块提供AI相关能力（可选集成）。

**Q: 我不需要AI功能，只需要Spring Boot工具可以吗？**
A: 可以，直接依赖common-core模块即可。或依赖common-starter让所有功能都可用。

**Q: 现有使用ai-core的代码如何迁移？**
A: 将包名从com.zxy.core保持不变，但artifact从ai-core改为common-core。代码无需修改。

**Q: 为什么移除了ai-inference模块？**
A: 推理功能已整合到ai-training.predict模块中，避免功能重复和模块冗余。

**Q: 支持哪些模型框架？**
A: 当前支持PyTorch模型，可通过扩展ai-model模块支持其他框架。

## 贡献指南

新增功能应遵循以下原则：
1. **通用功能** → 放在common-core
2. **AI相关** → 根据功能类型放在ai-data、ai-model或ai-training
3. 避免模块间的循环依赖
4. 编写单元测试
5. 更新本文档

## 架构哲学

该平台采用分层设计：
- **基础层**: common-core（通用框架工具）
- **AI能力层**: ai-* 系列模块（可选的AI增强）
- **集成层**: common-starter（一站式启动器）

这种设计使框架既能作为通用Spring Boot工具库，也能作为完整的AI开发平台。

## 联系方式

- 技术支持: ai-platform@company.com
- 文档位置: `common/MODULE_STRUCTURE.md`





