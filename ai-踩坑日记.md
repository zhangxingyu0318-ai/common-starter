# AI 平台踩坑日记

> 只记录在本项目中已经实际发生、并完成排查验证的问题。不要把推测、待办或通用知识写入本文件。

## 2026-09-30｜终端 Maven 仍使用 JDK 8，导致 Java 21 工程构建失败

### 现象

项目根 `pom.xml` 已统一声明 Java 21（包括 `maven.compiler.release=21`），但终端中直接运行 Maven 时，实际 Java 运行时仍是 JDK 8。

执行受影响模块测试时，Surefire 报错：

```text
class file version 65.0; this version of the Java Runtime only recognizes class file versions up to 52.0
```

其中 `65.0` 是 Java 21 class 文件版本，`52.0` 是 Java 8 可识别的最高版本。

### 已确认原因

终端环境的 `JAVA_HOME` / `Path` 指向 `D:\work\jdk\jdk1.8\jre`，所以 Maven 3.5.4 实际运行在 Java 8；IDE 或 POM 配置为 Java 21 并不会自动改变当前 PowerShell 会话的 Java 运行时。

本机已安装可用 JDK：`D:\work\jdk\jdk-21.0.8`。

### 修复方式

在执行 Maven 前，显式切换当前 PowerShell 会话：

```powershell
$env:JAVA_HOME = 'D:\work\jdk\jdk-21.0.8'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
mvn -version
mvn clean verify
```

### 验证结果

显式切换后，`java -version` 显示 `21.0.8`；已成功执行根工程 `mvn clean verify`，并成功执行安全、网关、认证模块的聚合测试。

### 防再踩

1. 每次新开终端首次构建前先执行 `java -version` 和 `mvn -version`，两者必须显示 Java 21。
2. IDE 的 Project SDK、Maven Runner JRE、终端 `JAVA_HOME`、CI 构建镜像均固定为 JDK 21。
3. 不要只看 `pom.xml` 的 `<java.version>21</java.version>`；它指定编译目标，不能替代 Maven 进程实际使用的 JDK。

## 2026-09-30｜新建分支未设置上游且 GitHub HTTPS 连接超时，导致 Push 失败

### 现象

当前本地分支为 `master-260930`，执行 `git push --dry-run` 返回：

```text
fatal: The current branch master-260930 has no upstream branch.
```

使用远端只读查询时，Git 进一步返回：

```text
Failed to connect to github.com port 443: Timed out
```

### 已确认原因

1. `master-260930` 是从 `master` 新建的本地分支，尚未关联 `origin/master-260930`，因此默认 `git push` 不知道目标分支。
2. 本机当前无法通过 HTTPS 的 443 端口访问 `github.com`；远端查询超时，因此即使指定上游也无法完成推送。

### 修复方式

先恢复 GitHub 网络访问（公司网络、代理、VPN、防火墙或 DNS），确认以下命令能返回远端分支：

```powershell
git ls-remote --heads origin
```

再首次推送并建立上游关联：

```powershell
git push --set-upstream origin master-260930
```

后续可直接执行：

```powershell
git push
```

### 防再踩

1. 新建本地分支后的第一次推送一律使用 `git push -u origin <branch-name>`。
2. 推送失败时先区分本地分支关联问题与网络问题；没有 HTTPS 连通性时，不要重复尝试认证或强推。
3. 使用 `git push --dry-run` 与 `git ls-remote --heads origin` 获取不改动远端的诊断信息。



