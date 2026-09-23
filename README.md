# 832402120_calculator_backend

EE308 第一次作业「前后端分离计算器系统」的**后端服务**。

负责四则与科学表达式计算、进制转换、计算历史的数据库持久化与搜索分页，通过 REST API（HTTP + JSON）与前端通信。核心计算使用**自实现的调度场算法（Shunting-yard）**完成，不依赖 `eval` / `ScriptEngine` 等执行任意代码的方式。

---

## 1. 技术栈

| 层 | 技术 |
|---|---|
| 语言 | Java 17 |
| 构建 | Maven 3.9 |
| 框架 | Spring Boot 3.3（Spring Web + Spring Data JPA + Validation） |
| 数据库 | 开发环境 H2（文件持久化）；生产环境 MySQL |

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
- Maven 3.9+（本机若未安装，可直接用 `~/tools/apache-maven-3.9.16/bin/mvn`）
- 开发环境**无需安装任何数据库软件**（H2 以文件模式运行）

## 5. 安装与启动

```bash
cd 832402120_calculator_backend

# 方式一：直接启动
mvn spring-boot:run

# 方式二：打包后启动
mvn clean package -DskipTests
java -jar target/calculator-backend-1.0.0.jar
```

启动成功后访问：`http://localhost:8080/api/history`（返回 `{"success":true,"data":[]}` 即正常）。

## 6. 配置说明

配置文件：`src/main/resources/application.yml`

| 配置项 | 默认值 | 说明 |
|---|---|---|
| `server.port` | 8080 | 服务端口 |
| 开发数据库 | H2 文件模式 | 数据持久化在 `./data/calculator`，重启不丢失 |
| `spring.jpa.hibernate.ddl-auto` | update（开发） | 自动建表 |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:8000,http://127.0.0.1:8000` | 允许访问 API 的前端域名，多个域名用逗号分隔 |

**生产环境（部署时）**：使用 MySQL，连接参数通过环境变量注入：

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

- **开发环境**：`ddl-auto=update` 自动创建表结构，无需手动操作
- **生产环境（MySQL）**：先创建数据库 `CREATE DATABASE calculator DEFAULT CHARSET utf8mb4;`；启动时执行 [`schema-mysql.sql`](src/main/resources/schema-mysql.sql) 并由 JPA 校验表结构

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
```

测试覆盖：四则运算、次方、开方、三角函数、运算符优先级、异常处理、进制转换、历史搜索分页，以及 API 请求校验和错误状态码。

## 9. 代码规范

代码遵循 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)，详见同仓库 `codestyle.md`。
