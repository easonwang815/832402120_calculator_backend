# 03 · API 接口设计

> 前后端通信协议：HTTP + JSON。所有接口由后端 Spring Boot 提供。

## 一、统一响应格式

### 成功响应

```json
{
  "success": true,
  "expression": "(1+2)*3",
  "result": "9"
}
```

列表类接口（如历史记录）使用 `data` 字段：

```json
{
  "success": true,
  "data": {
    "records": [
      { "id": 1, "expression": "1+2", "result": "3", "createdAt": "2026-10-01T10:20:00" }
    ],
    "page": 0,
    "size": 5,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 失败响应

```json
{
  "success": false,
  "message": "Invalid expression"
}
```

## 二、接口总览

| 方法 | 路径 | 用途 | 成功状态码 | 失败状态码 |
|---|---|---|---|---|
| POST | `/api/calculate` | 计算表达式（成功同时写入历史） | 200 | 400 / 500 |
| GET | `/api/history?keyword=&page=0&size=5` | 搜索并分页获取历史 | 200 | 400 / 500 |
| DELETE | `/api/history/{id}` | 删除指定历史记录 | 200 | 404 / 500 |
| DELETE | `/api/history` | 清空全部历史（加分项） | 200 | 500 |
| POST | `/api/convert` | 2 / 8 / 10 / 16 进制整数互转 | 200 | 400 / 500 |

## 三、接口详细定义

### 3.1 POST /api/calculate

计算表达式。**核心计算逻辑在本接口的后端完成**，前端仅传入表达式原文。

请求体：

```json
{
  "expression": "(1+2)*3"
}
```

| 场景 | 状态码 | 响应体 |
|---|---|---|
| 计算成功 | 200 | `{ "success": true, "expression": "(1+2)*3", "result": "9" }` |
| 表达式无效（语法错误、非法字符、空串） | 400 | `{ "success": false, "message": "Invalid expression" }` |
| 除零 | 400 | `{ "success": false, "message": "Division by zero" }` |
| 请求体格式错误 | 400 | `{ "success": false, "message": "Invalid request" }` |
| 服务端内部异常 | 500 | `{ "success": false, "message": "Internal server error" }` |

**行为约定**：
- 只有计算**成功**的结果才写入历史记录表；失败请求不落库。
- 表达式长度限制（如 ≤ 255 字符），超长返回 400，防止滥用。

### 3.2 GET /api/history

按 `keyword` 搜索表达式或结果，并按 `page` / `size` 分页。结果按 `created_at` 倒序。

| 场景 | 状态码 | 响应体 |
|---|---|---|
| 成功（含空历史） | 200 | `{ "success": true, "data": { "records": [], "page": 0, "size": 5, "totalElements": 0, "totalPages": 0 } }` |
| 页码/每页数量非法 | 400 | `{ "success": false, "message": "Invalid pagination parameters" }` |

**行为约定**：数据必须来自后端数据库，不得由前端缓存拼接。

### 3.3 DELETE /api/history/{id}

按 id 删除单条历史记录。`{id}` 为正整数（路径参数）。

| 场景 | 状态码 | 响应体 |
|---|---|---|
| 删除成功 | 200 | `{ "success": true }` |
| id 不存在 | 404 | `{ "success": false, "message": "Record not found" }` |
| id 格式非法 | 400 | `{ "success": false, "message": "Invalid request" }` |

**行为约定**：必须真实删除数据库中的对应记录；前端删除后重新调用 GET /api/history 刷新展示。

### 3.4 DELETE /api/history（加分项）

清空全部历史记录。

| 场景 | 状态码 | 响应体 |
|---|---|---|
| 清空成功 | 200 | `{ "success": true }` |

### 3.5 POST /api/convert（加分项）

请求体：`{ "value": "255", "fromBase": 10, "toBase": 16 }`。支持 2、8、10、16 进制整数互转。

| 场景 | 状态码 | 响应体 |
|---|---|---|
| 转换成功 | 200 | `{ "success": true, "data": { "value": "255", "fromBase": 10, "toBase": 16, "result": "FF" } }` |
| 数字与源进制不匹配 | 400 | `{ "success": false, "message": "Invalid number for source base" }` |

## 四、CORS（跨域配置）

前后端分离部署时端口/域名不同，浏览器会拦截跨域请求，因此后端需要开启 CORS：

- 允许来源：前端部署地址（开发期可用 `*` 或本机 `http://localhost:端口`）
- 允许方法：GET / POST / DELETE / OPTIONS
- 允许请求头：Content-Type

## 五、设计说明（供博客使用）

1. **为什么成功/失败都用 `success` 字段**：前端只需判断 `success` 即可决定展示结果还是错误信息，配合 HTTP 状态码双重语义。
2. **为什么 `result` 用字符串**：除法等运算结果可能是小数或无限循环小数，后端统一格式化后以字符串返回，避免前端 JSON 精度问题。
3. **为什么计算成功才落库**：作业要求"每次成功计算存库"，失败请求不应污染历史。
4. **状态码设计**：400 表示客户端请求问题（无效表达式/除零/参数错误），404 表示资源不存在，500 表示服务端异常——符合 REST 惯例。
