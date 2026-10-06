# Code Style

These project rules are based on the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). I use four spaces for indentation in this project.

## Names

- Use UpperCamelCase for classes, such as `CalculatorService`.
- Use lowerCamelCase for methods and variables, such as `calculate` and `createdAt`.
- Use UPPER_SNAKE_CASE for constants, such as `SCALE`.
- Use lowercase package names.
- Use snake_case for database columns, such as `created_at`.

## Formatting

- Use spaces instead of tabs.
- Keep lines within 120 characters where possible.
- Put the opening brace on the same line.
- Use braces for conditions and loops.
- Remove unused imports.

## Classes and Methods

- Controllers receive requests and return responses.
- Services contain the main steps for calculation and history.
- The calculator package contains the expression parser and evaluator.
- Use `ApiResponse` for API responses.
- Use ordinary constructors, getters and setters.

## Comments and Errors

- Write short English comments for classes and important steps.
- Explain operator precedence and unary signs in the calculator code.
- Use `CalculatorException` for invalid calculations.
- Use `NotFoundException` when a history record does not exist.
- Handle HTTP errors in `GlobalExceptionHandler`.
- Do not return internal error details to the frontend.
