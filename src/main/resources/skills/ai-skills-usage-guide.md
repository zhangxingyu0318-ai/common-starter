# AI Skills 使用指南

## 概述

当你的项目引入 `common` 作为父POM时，会自动获得AI平台的所有约束规范。本文档指导如何在你的项目中正确使用和遵守这些约束。

## 约束文件位置

引入父POM后，约束文件位于：
```
classpath:skills/ai-skills-constraint.md
```

## 如何在项目中使用约束

### 1. 代码层面约束

#### 1.1 数据预处理约束
```java
// P0约束: 必须对输入数据进行脱敏
public class DataProcessor {
    public String processInput(String rawData) {
        // 强制执行数据脱敏
        String sanitizedData = sanitizePersonalInfo(rawData);
        validateDataSize(sanitizedData); // 检查数据大小限制
        return sanitizedData;
    }
}
```

#### 1.2 API频率限制
```java
@RestController
public class AIController {

    @GetMapping("/inference")
    @RateLimit(limit = 100, window = 60) // 每分钟100次请求
    public ResponseEntity<?> inference(@RequestBody InferenceRequest request) {
        // 自动应用频率限制
        return processInference(request);
    }
}
```

#### 1.3 内容过滤
```java
public class ContentFilter {
    public String filterOutput(String aiResponse) {
        // P0约束: 过滤有害内容
        return contentFilter.filter(aiResponse)
                .removeHateSpeech()
                .removeViolence()
                .removeSensitiveContent();
    }
}
```

### 2. 配置层面约束

#### 2.1 资源限制配置
```yaml
# application.yml
ai:
  constraints:
    rate-limiting:
      user-limit: 100    # 每分钟用户请求限制
      ip-limit: 500      # 每分钟IP请求限制
      app-limit: 1000    # 每分钟应用请求限制
    resource-limits:
      cpu-threshold: 80  # CPU使用率阈值
      memory-threshold: 85 # 内存使用率阈值
      gpu-threshold: 90  # GPU使用率阈值
```

#### 2.2 审计配置
```yaml
ai:
  auditing:
    enabled: true
    log-level: INFO
    include-params: true    # 记录输入参数
    include-results: false  # 不记录完整结果（隐私保护）
    retention-days: 90      # 日志保留90天
```

### 3. 监控和告警

#### 3.1 性能监控
```java
@Service
public class AIMonitoringService {

    @Autowired
    private MeterRegistry meterRegistry;

    public void recordInferenceMetrics(String modelName, long responseTime) {
        // P1约束: 监控响应时间
        Timer.Sample sample = Timer.start(meterRegistry);
        // 执行推理...
        sample.stop(Timer.builder("ai.inference.duration")
                .tag("model", modelName)
                .register(meterRegistry));
    }
}
```

#### 3.2 约束违规告警
```java
@Component
public class ConstraintViolationHandler {

    @EventListener
    public void handleConstraintViolation(ConstraintViolationEvent event) {
        switch (event.getLevel()) {
            case P0:
                // 立即告警并可能终止服务
                alertService.sendCriticalAlert(event.getMessage());
                break;
            case P1:
                // 记录警告日志
                log.warn("P1 constraint violation: {}", event.getMessage());
                break;
            case P2:
                // 记录信息日志
                log.info("P2 constraint optimization needed: {}", event.getMessage());
                break;
        }
    }
}
```

## 约束检查清单

### 开发阶段检查
- [ ] 输入数据是否已脱敏
- [ ] 数据大小是否在限制范围内
- [ ] 数据格式是否正确验证
- [ ] 输出内容是否已过滤
- [ ] API是否有频率限制
- [ ] 资源使用是否监控
- [ ] 操作是否审计记录

### 测试阶段检查
- [ ] 约束自动化测试覆盖
- [ ] 边界条件测试（数据大小限制、频率限制等）
- [ ] 异常情况处理测试
- [ ] 性能基准测试

### 部署阶段检查
- [ ] 配置正确加载
- [ ] 监控指标正常
- [ ] 告警机制生效
- [ ] 日志正确记录

## 常见问题

### Q: 如何自定义约束？
A: 可以通过继承现有约束类并重写方法来自定义约束逻辑。

### Q: 约束违反时的处理策略？
A: P0约束直接拒绝服务，P1约束记录警告继续执行，P2约束仅记录信息。

### Q: 如何监控约束执行情况？
A: 通过日志分析和监控仪表板查看约束执行指标。

### Q: 约束文档如何更新？
A: 约束文档随common模块版本更新，子项目通过升级父POM获得最新约束。

## 技术支持

如有约束相关问题，请参考：
- 约束文档: `skills/ai-skills-constraint.md`
- 技术支持: ai-platform@company.com
- 更新频率: 随common模块版本更新
