# Project Architecture

The project has a JavaScript frontend and a Java Spring Boot backend. They communicate through HTTP and JSON.

## Main Parts

~~~text
Frontend -> Backend API -> H2 file database
~~~

The frontend handles input, buttons and the displayed results. The backend calculates expressions and manages the database.

A calculation follows these steps:

1. The user enters an expression.
2. The frontend sends it to `POST /api/calculate`.
3. The backend parses and evaluates it.
4. The backend saves a successful calculation.
5. The frontend shows the answer and reloads the history.

## Backend Folders

The Java files are in `src/main/java/com/calculator/`.

| Folder | Purpose |
|---|---|
| controller | Receives API requests |
| service | Runs calculations and manages history |
| calculator | Parses and evaluates expressions |
| model | Defines requests, responses and history records |
| repository | Reads and writes the database |
| config | Sets frontend access rules and handles errors |

`CalculatorApplication.java` starts Spring Boot. `application.yml` contains the backend settings.

## Expression Calculation

`ExpressionParser` splits an expression into numbers, operators, function names and brackets.

`ExpressionEvaluator` uses the shunting-yard algorithm to put the operators in the right order. It then uses a value stack to calculate the answer. It supports unary signs, powers, square roots and trigonometric functions in degrees.

Basic arithmetic uses `BigDecimal`. Invalid input and division by zero produce error messages. User input is not passed to `eval` or `ScriptEngine`.

## Frontend Files

- `index.html` contains the page elements.
- `css/style.css` contains the page styles.
- `js/api.js` sends API requests.
- `js/app.js` connects buttons, results, history and base conversion.

The frontend shows multiplication and division as `×` and `÷`. It changes them to `*` and `/` before sending an expression to the backend.

## Current Deployment

The project runs on an Alibaba Cloud server in Hangzhou.

Nginx serves the frontend and forwards `/api/` requests to the backend on `127.0.0.1:8080`. The backend saves history in an H2 file database.

Public address: [http://47.98.182.108/](http://47.98.182.108/).
