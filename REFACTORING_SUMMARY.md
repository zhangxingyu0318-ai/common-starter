# 项目结构说明

## ✅ 完成的改动

### 1. **根POM重命名**
- 根目录 `pom.xml` 的 `artifactId` 改为 `common-starter`
- `packaging` 保持为 `pom`（聚合POM）
- 但同时在 `<dependencies>` 中定义了所有依赖

### 2. **依赖管理结构**

#### 根POM (`common/pom.xml`)
- **类型**: 聚合POM + 父POM（双重角色）
- **packages**: pom
- **modules**: 聚合所有子模块
  - common-core
  - ai-data
  - ai-model
  - ai-training
- **dependencyManagement**: 定义所有依赖的版本
- **dependencies**: 声明所有实际依赖（子模块会继承）

#### 子模块 (common-core, ai-data, ai-model, ai-training)
- **parent**: 指向 `common-starter` 根POM
- **dependencies**: 完全空（从父POM继承所有依赖）
- 只需声明 artifactId 和基本信息

### 3. **依赖继承流程**

```
子模块构建
    ↓
读取父POM (common-starter)
    ↓
继承 <dependencyManagement> 中的版本定义
    ↓
自动继承 <dependencies> 中声明的依赖
    ↓
拥有完整的依赖体系，无需再单独声明
```

### 4. **优势**

✅ **集中管理**: 所有依赖在一个地方定义
✅ **版本一致**: 所有模块使用相同版本的依赖
✅ **子模块简洁**: 子模块 pom.xml 极简（只有 parent、artifactId、description）
✅ **易于维护**: 新增依赖只需改根 pom.xml

### 5. **示例对比**

#### 改动前（子模块的 pom.xml）
```xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
    <!-- 10多个其他依赖... -->
</dependencies>
```

#### 改动后（子模块的 pom.xml）
```xml
<!-- 完全不需要 dependencies 部分 -->
<!-- 从父 common-starter pom 自动继承所有依赖 -->
```

## 📋 项目新结构

```
common (根目录)
├── pom.xml (父POM - common-starter)
│   ├── dependencyManagement  (版本定义)
│   └── dependencies          (实际依赖 - 被所有子模块继承)
├── common-core/
│   ├── pom.xml (只有 parent + artifactId)
│   └── src/
├── ai-data/
│   ├── pom.xml (只有 parent + artifactId)
│   └── src/
├── ai-model/
│   ├── pom.xml (只有 parent + artifactId)
│   └── src/
└── ai-training/
    ├── pom.xml (只有 parent + artifactId)
    └── src/
```

## 🧪 验证

编译命令：
```bash
mvn clean compile
```

结果：✅ 编译成功，所有子模块都能获得正确的依赖。

