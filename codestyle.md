# Code Style / 代码规范

## 规范来源

本文档基于 **Google Java Style Guide**（https://google.github.io/styleguide/javaguide.html），并结合本项目实际情况制定项目级约定。代码风格以 Google Java Style Guide 为基准，本文档只列出本项目实际使用到的最关键规则。

---

## 1. 命名

| 对象 | 规范 | 示例 |
|---|---|---|
| 类 / 接口 | UpperCamelCase | `CalculatorService`、`HistoryRepository` |
| 方法 / 变量 | lowerCamelCase | `calculate()`、`historyList` |
| 常量 | UPPER_SNAKE_CASE | `SCALE`、`UNARY_MINUS` |
| 包名 | 全小写 | `com.calculator.service` |
| 枚举值 | UPPER_SNAKE_CASE | `NUMBER`、`DIVIDE` |
| 数据库表 / 列 | snake_case | `calculation_history`、`created_at` |

> Java 字段用 lowerCamelCase（如 `createdAt`），与数据库列名通过 `@Column(name = "created_at")` 映射。

## 2. 格式

- 缩进使用 **4 个空格**，不使用 Tab
- 行宽不超过 **120 字符**
- 大括号采用 K&R 风格：左大括号与语句同行
- `if / else / for / while` 无论多少行都必须使用大括号
- 每个语句以分号结尾

```java
// 正确
if (stack.size() < 2) {
    throw new CalculatorException("Invalid expression");
}
```

## 3. 导入

- 不导入未使用的包
- 禁止通配符导入（`import com.calculator.*`）
- 导入顺序：`static` → `java.* / jakarta.*` → 第三方 → 本项目，各组内按字母序

## 4. 注释

- **类注释**：用 Javadoc 说明类的职责
- **公开方法**：写 Javadoc，说明参数含义与返回值
- **关键算法**（如调度场算法）：写实现思路注释，说明为什么这样设计
- 不写无意义注释（如 `// set result` 这类废话）

## 5. 分层与职责

- **Controller 层**：只接收参数、调用 Service、返回 `ApiResponse`，不写业务逻辑
- **Service 层**：业务编排
- **calculator 包**（核心计算）：纯 Java，**不依赖 Spring**，保证可独立测试
- 所有接口统一返回 `ApiResponse`

## 6. 异常处理

- 业务异常统一抛 `CalculatorException`（message 会返回给前端）
- 资源不存在抛 `NotFoundException`
- Controller 不写 try-catch，统一由 `GlobalExceptionHandler` 处理
- 不向客户端暴露内部异常堆栈（500 只返回 "Internal server error"）

## 7. 项目约定

- **不使用 Lombok**：手写 getter / setter，减少额外依赖与 IDE 插件依赖
- **数字计算用 `BigDecimal`**：避免 `double` 浮点精度问题；除法保留 10 位小数
- **时间字段**：由实体 `@PrePersist` 自动赋值，兼容 H2 / MySQL
- **字符串拼接**：少量拼接用 `+`，循环内用 `StringBuilder`（本项目无此类场景，遵循此约定）

## 8. 检查方式

- 提交前运行 `mvn test` 确保全部用例通过
- 逐项对照上文 1~7 检查新增代码
