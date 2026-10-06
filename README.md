# 832402120_calculator_backend

EE308 第一次作业「前后端分离计算器系统」的**后端服务**。

负责四则与科学表达式计算、进制转换、计算历史的数据库持久化与搜索分页，通过 REST API（HTTP + JSON）与前端通信。核心计算使用**自实现的调度场算法（Shunting-yard）**完成，不依赖 `eval` / `ScriptEngine` 等执行任意代码的方式。

## 快速访问

- **在线完整演示**：[http://47.98.182.108/](http://47.98.182.108/)。浏览器打开该地址即可使用，无需本地启动。
- **在线历史 API**：[http://47.98.182.108/api/history](http://47.98.182.108/api/history)。
- [前端仓库](https://github.com/easonwang815/832402120_calculator_frontend) · [后端仓库](https://github.com/easonwang815/832402120_calculator_backend)。私有仓库需要访问权限。
- 当前公网部署使用 **H2 文件数据库**，并非 MySQL；已验证后端重启后历史记录仍保留。
- 阿里云杭州服务器的免费试用到 **2026-11-06 23:59:59**。演示站采用 HTTP、访客共享历史，不要输入敏感信息；到期前需要备份和安排后续运行。

---

## 1. 技术栈

| 层 | 技术 |
|---|---|
| 语言 | Java 17 |
| 构建 | Maven 3.9 |
| 框架 | Spring Boot 3.3（Spring Web + Spring Data JPA + Validation） |
| 数据库 | 本地及当前公网部署使用 H2 文件持久化；可选 MySQL（`prod` profile） |

## 2. 整体框架

```
前端 (Web)
   │  HTTP / JSON
   ▼
Controller 层       接收请求、参数校验、统一响应
   │
   ▼
Service 层          业务编排：解析计算 → 保存历史 → 返回结果
   │
   ├──► calculator 包   核心计算（纯 Java，不依赖 Spring）
   │       词法分析(ExpressionParser) → 调度场转后缀(ExpressionEvaluator)
   │       处理：四则 / 次方 / 开方 / 角度制三角函数 / 括号 / 异常输入
   │
   ▼
Repository 层        JPA 数据访问（增 / 查 / 删）
   │
   ▼
数据库                calculation_history 表
                      (id, expression, result, created_at)
```

**模块目录结构**

```
src/main/java/com/calculator/
├── calculator/          # ★ 核心计算模块（可独立测试）
│   ├── ExpressionParser.java      词法分析：字符串 → Token
│   ├── ExpressionEvaluator.java   调度场算法 + BigDecimal 求值
│   ├── Token.java / TokenType.java
│   └── CalculatorException.java   业务异常
├── controller/          # CalculatorController / HistoryController
├── service/             # CalculatorService / HistoryService / NotFoundException
├── repository/          # HistoryRepository (Spring Data JPA)
├── model/               # CalculationHistory / CalculateRequest / ApiResponse
└── config/              # WebConfig(CORS) / GlobalExceptionHandler(统一异常)
```

需求、API、数据库和架构设计文档保存在 [`docs/`](docs/) 目录。

**一次计算的完整流程**

```
前端发送 {"expression":"(1+2)*3"}
  → Controller 校验请求体
  → CalculatorService 调用 ExpressionEvaluator 计算
  → 成功则保存历史到数据库
  → 返回 {"success":true,"expression":"(1+2)*3","result":"9"}
```

**架构合规说明**：核心计算只发生在后端。前端仅发送表达式、展示后端返回的结果；停掉后端服务后，前端无法独立得出任何新的计算结果。

## 3. API 一览

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/calculate` | 计算表达式（成功同时写入历史） |
| GET | `/api/history?keyword=&page=0&size=5` | 搜索并分页查询历史 |
| DELETE | `/api/history/{id}` | 删除指定历史记录 |
| DELETE | `/api/history` | 清空全部历史（加分项） |
| POST | `/api/convert` | 2 / 8 / 10 / 16 进制整数互转 |

统一响应：成功 `{"success":true,...}`；失败 `{"success":false,"message":"..."}`。

## 4. 运行环境

- JDK 17+
- Maven 3.9+，需要将 `mvn` 加入 PATH
- 本地默认**无需安装任何数据库软件**（H2 以文件模式运行）
- 前端本地运行需要 Python 3；服务器部署只运行打包后的后端时，Java 17 JRE 即可

## 5. 安装与启动

### 5.1 下载并检查环境

```bash
git clone https://github.com/easonwang815/832402120_calculator_backend.git
cd 832402120_calculator_backend
java -version
mvn -version
```

如果已经下载仓库，直接进入后端目录即可。此电脑的 Maven 也可使用 `/Users/yizhenwang/tools/apache-maven-3.9.16/bin/mvn`，其他电脑请使用自己的 Maven 安装位置。

### 5.2 启动后端（两种方式任选一种）

```bash
# 方式一：开发运行，首次启动会下载 Maven 依赖
mvn spring-boot:run
```

```bash
# 方式二：先测试并打包，再启动 JAR
mvn -DforkCount=0 clean package
java -jar target/calculator-backend-1.0.0.jar
```

`-DforkCount=0` 用于避免部分环境下中文路径导致测试子进程启动失败，不会跳过测试。两种方式不要同时运行，否则会占用同一个 8080 端口。

保持后端终端运行，浏览器打开 [http://localhost:8080/api/history](http://localhost:8080/api/history)。首次运行应返回：

```json
{"success":true,"data":{"records":[],"page":0,"size":5,"totalElements":0,"totalPages":0}}
```

这是 API 服务，访问后端根路径不会显示计算器界面。

### 5.3 启动前端并打开计算器

在与后端仓库同级的目录下载前端，在另一个终端执行：

```bash
git clone https://github.com/easonwang815/832402120_calculator_frontend.git
cd 832402120_calculator_frontend
python3 -m http.server 8000
```

浏览器打开 [http://localhost:8000/](http://localhost:8000/)，输入 `1+2` 后按 `Enter` 或点击 `=`，应得到 `3` 并新增历史。前端已有时跳过克隆命令，进入前端目录启动即可；Windows 可使用 `py -3 -m http.server 8000`。

停止本地运行时，在两个终端分别按 `Ctrl+C`。重新从同一后端目录启动，原有历史会继续保留。API 简单联调示例：

```bash
curl -H 'Content-Type: application/json' \
  --data '{"expression":"(1+2)*3"}' \
  http://localhost:8080/api/calculate
```

正确结果为 `{"success":true,"expression":"(1+2)*3","result":"9"}`。

## 6. 配置说明

配置文件：`src/main/resources/application.yml`

| 配置项 | 默认值 | 说明 |
|---|---|---|
| `server.port` | 8080 | 服务端口 |
| 默认数据库 | H2 文件模式 | 数据文件位于启动工作目录的 `./data/calculator.mv.db` |
| `spring.jpa.hibernate.ddl-auto` | update（H2） | 自动建表 |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:8000,http://127.0.0.1:8000` | 允许访问 API 的前端域名，多个域名用逗号分隔 |
| `SPRING_DATASOURCE_URL` | 配置文件中的 H2 URL | 可指定持久化数据库绝对路径 |
| `SPRING_H2_CONSOLE_ENABLED` | true（本地默认） | 公网部署必须关闭；当前阿里云已设为 false |

默认 H2 数据在磁盘中，不依赖前端缓存或内存。不要删除后端的 `data/` 目录，也不要将数据库文件或密码提交到 Git。

### 可选 MySQL 配置（不是当前公网部署方式）

只有准备使用 MySQL 时才启用 `prod` profile，并设置连接参数；默认运行和现有阿里云演示不需要这些环境变量：

```bash
# 先设置环境变量
export DB_HOST=... DB_PORT=3306 DB_NAME=calculator
export DB_USER=... DB_PASSWORD=...
export CORS_ALLOWED_ORIGINS=https://your-frontend.example.com
export PORT=8080

# 以 prod 配置启动
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 7. 数据库初始化

- **默认 H2（本地及当前公网部署）**：`ddl-auto=update` 自动创建表结构，无需手动操作
- **可选 MySQL（`prod` profile）**：先创建数据库 `CREATE DATABASE calculator DEFAULT CHARSET utf8mb4;`；启动时执行 [`schema-mysql.sql`](src/main/resources/schema-mysql.sql) 并由 JPA 校验表结构

表结构：

```
calculation_history
├── id          BIGINT AUTO_INCREMENT 主键
├── expression  VARCHAR(255)          表达式原文
├── result      VARCHAR(1024)         计算结果（字符串，避免浮点精度问题）
└── created_at  DATETIME              计算时间
```

## 8. 测试

```bash
mvn test

# 中文路径下若测试子进程启动失败，可使用：
mvn -DforkCount=0 test
```

测试覆盖：四则运算、次方、开方、三角函数、运算符优先级、异常处理、进制转换、历史搜索分页，以及 API 请求校验和错误状态码。部署时已通过 23 项自动测试，并验证公网计算、进制转换及服务重启后的历史保留。

## 9. 代码规范

代码遵循 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)，详见同仓库 `codestyle.md`。

## 10. 当前阿里云部署与运维

服务器：杭州轻量应用服务器，Ubuntu 24.04，2 vCPU / 1 GiB / 30 GiB。Nginx 提供静态前端，并把同域 `/api/` 转发到 Spring Boot 的 `127.0.0.1:8080`；无需跨域配置，也无需额外云数据库。

| 内容 | 服务器上的位置 |
|---|---|
| 前端静态文件 | `/var/www/ee308-calculator/` |
| 后端 JAR | `/opt/ee308-calculator/calculator.jar` |
| H2 数据文件 | `/var/lib/ee308-calculator/data/calculator.mv.db` |
| 后台服务配置 | `/etc/systemd/system/ee308-calculator.service` |
| Nginx 配置 | `/etc/nginx/sites-available/ee308-calculator` |

后台服务已设置开机自启、异常自动重启，Java 最大堆为 256 MiB。H2 控制台已关闭，后端只监听回环地址，数据库不在静态网站目录中。当前没有启用 MySQL 的 `prod` profile。

通过阿里云 Workbench 免密连接**现有服务器**后，可以运行以下命令：

```bash
# 查看是否正在运行、是否设置开机自启
sudo systemctl status ee308-calculator --no-pager
sudo systemctl is-enabled ee308-calculator

# 手动启动或重启服务
sudo systemctl start ee308-calculator
sudo systemctl restart ee308-calculator

# 查看近期日志和本机接口
sudo journalctl -u ee308-calculator -n 100 --no-pager
curl -fsS http://127.0.0.1/api/history
```

普通访客只需打开在线地址，不需要连接服务器。评审期间应保持实例及服务运行。备份 H2 数据前先停止 `ee308-calculator` 服务，复制数据目录后再启动，避免直接复制正在写入的数据库文件。

### 常见问题

- **`mvn: command not found`**：安装 Maven 并加入 PATH，或使用自己安装目录里的 `bin/mvn`。
- **8080 端口被占用**：关闭之前启动的后端，避免同时运行开发服务和 JAR。
- **本地前端历史加载失败**：确认后端已启动，前端来自允许的 8000 端口；若改端口，需同步修改 `CORS_ALLOWED_ORIGINS`。
- **公网返回 502**：检查后台服务和日志；Spring Boot 重启后需要等待初始化完成再访问。
- **历史记录似乎消失**：检查是否从不同工作目录启动、使用了不同数据库路径，或试用实例到期；不要直接删除数据目录。
