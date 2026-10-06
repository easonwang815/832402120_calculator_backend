# Requirements

This project is a frontend-backend calculator for EE308. The submission deadline is October 7, 2026 at 23:59.

## Main Features

| Requirement | What the project does |
|---|---|
| Basic arithmetic | Adds, subtracts, multiplies and divides |
| Compound expressions | Handles brackets, precedence, decimals and unary signs |
| Input errors | Reports invalid expressions and division by zero |
| Calculation history | Saves each successful expression, answer and time in the backend database |
| Delete history | Deletes a selected record through the backend API |

For example, `1+2*3` gives `7`, while `(1+2)*3` gives `9`.

## Frontend and Backend

- The frontend sends expressions and shows the returned answers.
- All calculations take place in the backend.
- The parser does not run user input as code.
- The frontend and backend communicate using HTTP and JSON.
- History is stored in an H2 file database, not browser storage.
- There are two separate GitHub repositories, each with a README and code style document.
- The project has a public address for review.

## Extra Features in This Project

- Powers, square roots, sin, cos and tan
- Integer conversion between base 2, 8, 10 and 16
- History search and pagination
- Keyboard shortcuts and clearing all history

## Blog Submission

The blog needs the course information, repository and code style links, a PSP time table, and a description of the design and implementation. It also needs at least ten screenshots or the required GIF/video demonstration, with explanations, key code examples and a personal reflection.

The blog must be published and approved before submission.
