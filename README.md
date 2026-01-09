# Library Management Service

A RESTful API for managing a library system, allowing users to register books, register borrowers, and manage book borrowing with concurrency control.

## Technologies Used

- **Java 17**: Core programming language.
- **Spring Boot 3.3.0**: Application framework.
- **Spring Data JPA**: Data persistence.
- **PostgreSQL**: Relational database.
- **Docker & Docker Compose**: Containerization and orchestration.
- **OpenAPI (Swagger)**: API documentation.
- **Lombok**: Boilerplate code reduction.
- **JUnit 5 & Mockito**: Testing.

## Features

- **Book Management**: Register new books and list all books.
- **Borrower Management**: Register new borrowers.
- **Borrowing System**: 
    - Borrow books (ensuring a book is not already borrowed).
    - Return books.
    - **Concurrency Control**: Handles simultaneous borrow requests for the same book using Optimistic Locking.
- **Request Tracing**: All API requests require a correlation ID for tracking.

## Prerequisites

- Docker and Docker Compose installed.
- Java 17 (if running manually).
- Maven (if running manually).

## How to Run

### Using Docker (Recommended)

1.  Clone the repository.
2.  Run the application and database:
    ```bash
    docker-compose up --build
    ```
3.  The application will be available at `http://localhost:1212`.

### Manual Setup

1.  Ensure a PostgreSQL database is running.
2.  Configure database credentials in `src/main/resources/application.properties` or via environment variables (`DB_USERNAME`, `DB_PASSWORD`, `DB_NAME`).
3.  Build the project:
    ```bash
    ./mvnw clean package
    ```
4.  Run the JAR:
    ```bash
    java -jar target/library-management-service-0.0.1-SNAPSHOT.jar
    ```

## API Documentation

Once the application is running, you can access the Swagger UI at:

[http://localhost:1212/swagger-ui/index.html](http://localhost:1212/swagger-ui/index.html)

### Required Headers

All API endpoints (except Swagger UI) require the following header for request tracing:

- **Header Name**: `X-Correlation-Id`
- **Description**: A unique identifier for the request (e.g., UUID).
- **Example**: `X-Correlation-Id: 123e4567-e89b-12d3-a456-426614174000`

If this header is missing, the API will return `400 Bad Request`.

### Key Endpoints

- **POST /api/books**: Register a new book.
- **GET /api/books**: Get all books.
- **POST /api/borrowers**: Register a new borrower.
- **POST /api/borrowing/borrow**: Borrow a book.
- **POST /api/borrowing/return**: Return a book.

## Architecture Decisions

### 1. Layered Architecture
The application follows a standard layered architecture (Controller -> Service -> Repository) to separate concerns:
- **Controller**: Handles HTTP requests and responses.
- **DTOs**: Decouples the internal domain model from the API contract.
- **Service**: Contains business logic (e.g., validations, transaction management).
- **Repository**: Handles data access.

### 2. Concurrency Control
To prevent race conditions where two users might try to borrow the same book simultaneously, **Optimistic Locking** is implemented using the `@Version` annotation on the `Book` entity. 
- If two transactions attempt to update the same book record at the same time, one will fail with an `OptimisticLockingFailureException`. 
- This approach is chosen over Pessimistic Locking to avoid holding database locks for long durations, improving system throughput.

### 3. Exception Handling
A global exception handler (`GlobalExceptionHandler`) is used to catch specific exceptions (like `BookNotFoundException`, `BookAlreadyBorrowedException`) and return standardized JSON error responses with appropriate HTTP status codes.

## Database Justification

### PostgreSQL
PostgreSQL was chosen for its reliability, robust ACID compliance, and strong support for concurrent operations. Since the library system involves transaction-heavy operations (borrowing/returning), data integrity is paramount.

### Schema Design
- **Book**: Stores book details and current status.
    - `status`: ENUM (`AVAILABLE`, `BORROWED`). Replaces boolean flags for better state management.
    - `version`: Used for optimistic locking.
- **Borrower**: Stores user information.
- **Relationships**: While not explicitly enforced with foreign keys in the minimal implementation, the logical relationship is maintained via service logic.

## Testing

The project includes:
- **Unit Tests**: For Service layer logic using Mockito.

Run tests using:
```bash
mvn test
```
