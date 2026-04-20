# Auth Service

A JWT-based authentication service built with Spring Boot, Kotlin, and PostgreSQL.

## Prerequisites

- Java 24
- Docker and Docker Compose

## Getting Started

### 1. Start the Database

The application uses PostgreSQL. A SQL script in `db-init/init.sql` is automatically executed when the container is first created to set up the required tables.

You can start the database using:

```bash
docker compose up -d
```

This will start a PostgreSQL instance on port `5433` with the following credentials:
- **User**: `postgres`
- **Password**: `password`
- **Database**: `authdb`

### 2. Run the Application

You can run the application using Gradle:

```bash
./gradlew bootRun
```

The application will be available at `http://localhost:8080`.

## API Endpoints

### Registration

- **URL**: `/api/auth/register`
- **Method**: `POST`
- **Body**:
  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "StrongPassword123!"
  }
  ```

### Login

- **URL**: `/api/auth/login`
- **Method**: `POST`
- **Body**:
  ```json
  {
    "email": "john.doe@example.com",
    "password": "StrongPassword123!"
  }
  ```
- **Response**: Returns a JWT token.

### Protected Profile

- **URL**: `/api/auth/me`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <JWT_TOKEN>`

## Running Tests

Tests use an in-memory H2 database and do not require Docker.

```bash
./gradlew test
```
