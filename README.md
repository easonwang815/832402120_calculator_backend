# Calculator Backend

## Project Overview

This is the backend of my EE308 calculator project. It uses Java 17, Spring Boot and an H2 file database.

The frontend sends an expression to the backend. The backend calculates the answer, saves the history and returns JSON. I use a parser and two stacks to handle the order of operations. The code does not use `eval` or `ScriptEngine`.

[Frontend repository](https://github.com/easonwang815/832402120_calculator_frontend)

## Features

- Addition, subtraction, multiplication and division
- Brackets, decimals, negative numbers and operator precedence
- Powers, square roots and trigonometric functions in degrees
- Error messages for invalid expressions and division by zero
- Saved history with search, pages, single deletion and clear all
- Integer conversion between base 2, 8, 10 and 16

## How It Works

The main calculation flow is:

~~~text
Frontend -> Controller -> CalculatorService -> ExpressionEvaluator
                                            -> HistoryRepository -> H2
Frontend <- JSON response with the answer
~~~

The parser splits the expression into numbers, operators, functions and brackets. The evaluator puts them in the correct order and calculates the answer. A successful calculation is saved before the answer is returned. Invalid expressions and division by zero return an error and are not saved.

## API Endpoints

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/calculate` | Calculate an expression and save its result |
| GET | `/api/history` | Read and search history pages |
| DELETE | `/api/history/{id}` | Delete one history record |
| DELETE | `/api/history` | Clear all history |
| POST | `/api/convert` | Convert an integer to another base |

For example, the frontend sends this body to `POST /api/calculate`:

~~~json
{"expression": "(1+2)*3"}
~~~

The backend returns:

~~~json
{"success": true, "expression": "(1+2)*3", "result": "9"}
~~~

History requests use `keyword`, `page` and `size`. For example, `/api/history?keyword=1&page=0&size=5` returns the first page of matching records. Page numbers start at zero, and the newest records are shown first.

## History Database

The H2 database has a table named `calculation_history`:

| Field | What it stores |
|---|---|
| `id` | The record ID |
| `expression` | The original expression |
| `result` | The formatted answer as text |
| `created_at` | The time the backend saved the record |

The database is saved as a file instead of only staying in memory. This is why history is still available after restarting the backend. The frontend always reads history through the API.

## Project Structure

The Java files are in `src/main/java/com/calculator/`:

- `CalculatorApplication.java`: starts the backend
- `controller/`: receives HTTP requests
- `service/`: calculates answers and manages history
- `calculator/`: splits and evaluates expressions
- `model/`: request, response and history classes
- `repository/`: reads and writes history in the database
- `config/`: handles errors and frontend access

Settings are in `src/main/resources/application.yml`. Design notes are in [`docs/`](docs/), the time record is in [`docs/02_psp.md`](docs/02_psp.md), and code style is in [`codestyle.md`](codestyle.md).

## Requirements

Install Java 17 and Maven 3.9. The database is created automatically, so no separate database installation is needed.

## How to Run

Open a terminal in the parent folder of the project:

~~~bash
cd 832402120_calculator_backend
mvn spring-boot:run
~~~

Keep this terminal running. The backend uses port 8080. Open [http://localhost:8080/api/history](http://localhost:8080/api/history) to see the history response.

To open the calculator page, use a second terminal in the same parent folder:

~~~bash
cd 832402120_calculator_frontend
python3 -m http.server 8000
~~~

Open [http://localhost:8000/](http://localhost:8000/) in your browser. Type `1+2` and press Enter.

The local history is saved in `data/calculator.mv.db` inside the backend folder when started with the command above. Restarting the backend does not clear this file. Press Ctrl+C in each terminal to stop the project.

## Online Demo

[Open the calculator](http://47.98.182.108/)

The project runs on an Alibaba Cloud server in Hangzhou. Nginx serves the frontend and forwards `/api` requests to the backend. History is stored in an H2 file database. The server trial ends on November 6, 2026.

This demo uses HTTP and shares history between visitors. Please do not enter private information.
