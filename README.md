# 🧠 Task API

A simple and extensible RESTful API for managing tasks, built with Spring Boot.

---

## ✅ Technologies

- Java 23
- Spring Boot 3.5.0
- Spring Data JPA
- PostgreSQL
- Maven
- Docker (optional)

---

## ⚠️ Environment Profiles

> ℹ️ **This project currently runs using only the `dev` profile.**

The `dev` profile is used by default for local development and testing purposes. This choice is intentional to avoid cloud costs, as this is currently a **personal portfolio project**.

In the future, a `prd` (production) profile will be configured to connect with Google Cloud services such as:

- **Cloud SQL**: managed PostgreSQL instance
- **Secret Manager**: to securely retrieve DB credentials at runtime

A migration guide is available below.


## 🚀 Getting Started

### 1. Set environment variables

You can set them in your terminal or your IDE (no `.env` file required):

**Linux / macOS (bash/zsh):**
```
export DB_URL=jdbc:postgresql://localhost:5432/your_database
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

**Windows (CMD):**
```
set DB_URL=jdbc:postgresql://localhost:5432/your_database
set DB_USERNAME=your_username
set DB_PASSWORD=your_password
```

**Windows (PowerShell):**
```
$env:DB_URL = "jdbc:postgresql://localhost:5432/your_database"
$env:DB_USERNAME = "your_username"
$env:DB_PASSWORD = "your_password"
```

### 2. Run the application with the dev profile

**With Maven wrapper:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Or with installed Maven:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Or using a compiled JAR file:**
```
java -jar target/task-api.jar --spring.profiles.active=dev
```

---

## 📁 API Endpoints

| Method | Endpoint         | Description            |
|--------|------------------|------------------------|
| GET    | `/tasks`         | List all tasks         |
| POST   | `/tasks`         | Create a new task      |
| PUT    | `/tasks/{id}`    | Update a task by ID    |
| DELETE | `/tasks/{id}`    | Delete a task by ID    |

> This is a basic example. Swagger integration is recommended for full documentation.

---

## ☁️ Migrating to Production Profile (`prd`)

When moving this API to production (e.g. deployment to GCP), follow these steps:

### 1. Create a `application-prd.yml` file:

spring:
datasource:
url: ${DB_URL}
username: ${DB_USERNAME}
password: ${DB_PASSWORD}
jpa:
hibernate:
ddl-auto: validate
show-sql: false

### 2. Store credentials securely

Use **Google Cloud Secret Manager** to store:

- DB_URL
- DB_USERNAME
- DB_PASSWORD

You can then fetch and inject them into your environment at runtime using GCP Workload Identity or startup scripts.

### 3. Connect to Cloud SQL securely

Use the [Cloud SQL Java Socket Factory](https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory) with IAM authentication to avoid exposing passwords directly.

Example URL using socket factory:

jdbc:postgresql:///<DB_NAME>?cloudSqlInstance=<INSTANCE_CONNECTION_NAME>&socketFactory=com.google.cloud.sql.postgres.SocketFactory&user=<USERNAME>

---

## ✅ What I want to do next

- Add APIs Endpoints itself
- Add Swagger/OpenAPI documentation
- Implement authentication with Spring Security + JWT
- Add unit and integration tests
- Add Docker support
- Deploy to cloud (Render, GCP, etc.)

---

## 🧑‍💻 Author

Created by [Marllon Nasser](https://github.com/marllon)