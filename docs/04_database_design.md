# Database Design

The project uses an H2 file database to store calculation history. The data stays on disk when the backend stops.

## History Table

The table is called `calculation_history`.

| Column | Type | Purpose |
|---|---|---|
| id | BIGINT | Automatically generated record ID |
| expression | VARCHAR(255) | The original expression |
| result | VARCHAR(1024) | The answer as text |
| created_at | TIMESTAMP | The time the record was saved |

All four fields are required. The ID is the primary key.

## Java Class

`CalculationHistory` maps the Java fields to the database columns using JPA annotations. The `@PrePersist` method sets `createdAt` before a new record is saved.

`HistoryRepository` reads and writes records. `HistoryService` handles searching, pages and deletion.

## Why the Result Is Text

The backend formats the answer before saving it. Keeping this text makes the history show the same answer as the calculator. Division results are rounded to ten decimal places.

## Database Location

When the backend is started from its project folder, the local file is:

~~~text
data/calculator.mv.db
~~~

On the current Alibaba Cloud server, the file is:

~~~text
/var/lib/ee308-calculator/data/calculator.mv.db
~~~

JPA creates the table automatically with `ddl-auto=update`. A separate database installation is not needed. The frontend gets history from the backend instead of storing it in the browser.
