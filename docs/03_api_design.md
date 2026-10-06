# API Design

The frontend sends HTTP requests to the backend. Requests and responses use JSON.

## Endpoints

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/calculate` | Calculate an expression and save the result |
| GET | `/api/history` | Search and read history pages |
| DELETE | `/api/history/{id}` | Delete one record |
| DELETE | `/api/history` | Clear all records |
| POST | `/api/convert` | Convert an integer to another base |

## Calculate an Expression

Request to `POST /api/calculate`:

~~~json
{"expression": "(1+2)*3"}
~~~

Successful response:

~~~json
{"success": true, "expression": "(1+2)*3", "result": "9"}
~~~

Error response:

~~~json
{"success": false, "message": "Invalid expression"}
~~~

Only successful calculations are saved. An expression can contain up to 255 characters.

## Read History

Example request: `GET /api/history?keyword=1&page=0&size=5`.

The keyword searches the expression and result. Page numbers start at zero. Records are returned with the newest first.

~~~json
{
  "success": true,
  "data": {
    "records": [
      {"id": 1, "expression": "1+2", "result": "3", "createdAt": "2026-10-01T10:20:00"}
    ],
    "page": 0,
    "size": 5,
    "totalElements": 1,
    "totalPages": 1
  }
}
~~~

## Delete History

Use the record ID with `DELETE /api/history/{id}`. The backend deletes the database record. The frontend then reads the updated history.

Both deletion endpoints return `{"success": true, "data": true}` when successful.

## Convert a Number

Request to `POST /api/convert`:

~~~json
{"value": "255", "fromBase": 10, "toBase": 16}
~~~

Response:

~~~json
{"success": true, "data": {"value": "255", "fromBase": 10, "toBase": 16, "result": "FF"}}
~~~

The supported bases are 2, 8, 10 and 16.

## Error Status Codes

- 400: invalid expression, division by zero or invalid request data
- 404: a record or API path does not exist
- 405: the HTTP method is not supported
- 415: the request content type is not supported
- 500: an unexpected server error

For local use, the backend allows requests from `http://localhost:8000` and `http://127.0.0.1:8000`. The online frontend uses the same address as the API.
