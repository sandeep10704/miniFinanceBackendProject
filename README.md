# 💰 Finance Management Backend API

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-blue?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-JWT-black?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Swagger](https://img.shields.io/badge/Docs-Swagger%20UI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

A robust, production-ready **RESTful backend API** for personal finance management — built with Java 21 and Spring Boot, featuring JWT-based authentication, role-based access control, rate limiting, and comprehensive Swagger documentation.

</div>

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Design](#-database-design)
- [Setup & Installation](#-setup--installation)
- [API Endpoints](#-api-endpoints)
- [Role Permissions](#-role-permissions)
- [Example Requests & Responses](#-example-requests--responses)
- [Rate Limiting](#-rate-limiting)
- [Validation & Error Handling](#-validation--error-handling)
- [Swagger Documentation](#-swagger-documentation)
- [Future Improvements](#-future-improvements)
- [Author](#-author)

---

## 🌟 Project Overview

The **Finance Management Backend API** is a secure, scalable Spring Boot application that enables users to track their financial records (income & expenses) with granular control over access. It supports:

- 🔐 Stateless JWT-based authentication
- 👥 Three-tier role-based access control (ADMIN, ANALYST, VIEWER)
- 📊 Public dashboard analytics without authentication
- 🛡️ IP-based rate limiting on public APIs via Bucket4j
- 📄 Full CRUD + filtering + pagination on financial records
- 🗂️ Soft delete to preserve data integrity
- 🌐 OAuth-ready user schema for future social login integration

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 JWT Authentication | Stateless token-based login & authorization |
| 👑 Role-Based Access Control | ADMIN, ANALYST, and VIEWER roles with granular permissions |
| 📁 Financial Records CRUD | Create, read, update, and soft-delete records |
| 🔍 Filtering & Pagination | Filter by type, category, date range with page support |
| 📊 Dashboard APIs | Summary, category totals, monthly trends, and recent records |
| 🛡️ Rate Limiting | IP-based rate limiting on public dashboard endpoints (Bucket4j) |
| ✅ Input Validation | Bean validation annotations on all request DTOs |
| 🚨 Global Exception Handling | Centralized error responses with proper HTTP status codes |
| 🗑️ Soft Delete | Records are flagged as deleted, never physically removed |
| 📖 Swagger UI | Interactive API documentation with JWT bearer support |
| 🔗 OAuth-Ready Schema | Database ready for social login (Google, GitHub, etc.) |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 4.0.5 |
| **ORM** | Spring Data JPA (Hibernate) |
| **Database** | PostgreSQL (hosted on Supabase) |
| **Authentication** | JWT (JJWT 0.11.5) |
| **Rate Limiting** | Bucket4j 8.10.1 |
| **API Docs** | SpringDoc OpenAPI 3 (Swagger UI) |
| **Build Tool** | Maven |
| **Utilities** | Lombok |

---

## 📁 Project Structure

```
src/main/java/com/sandeep/simplebackend/finance/
│
├── 📂 config/
│   ├── FilterConfig.java          # Filter registration (rate limiter)
│   └── SwaggerConfig.java         # OpenAPI / Swagger configuration
│
├── 📂 controller/
│   ├── AuthController.java        # Login endpoint
│   ├── UserController.java        # User management (ADMIN only)
│   ├── FinancialRecordController.java  # CRUD for financial records
│   └── DashboardController.java   # Public analytics endpoints
│
├── 📂 service/
│   ├── AuthService.java
│   ├── UserService.java           # Interface
│   ├── UserServiceImpl.java
│   ├── FinancialRecordService.java # Interface
│   ├── FinancialRecordServiceImpl.java
│   ├── DashboardService.java      # Interface
│   └── DashboardServiceImpl.java
│
├── 📂 repository/
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   └── FinancialRecordRepository.java
│
├── 📂 entity/
│   ├── User.java
│   ├── Role.java
│   └── FinancialRecord.java
│
├── 📂 dto/
│   ├── LoginRequest.java
│   ├── CreateUserRequest.java
│   ├── UserDTO.java
│   ├── CreateRecordRequest.java
│   ├── FinancialRecordDTO.java
│   └── DashboardSummaryDTO.java
│
├── 📂 security/
│   ├── JwtUtil.java               # JWT generation & validation
│   ├── RateLimitFilter.java       # Servlet filter for rate limiting
│   └── RateLimitService.java      # Bucket4j token bucket management
│
├── 📂 exception/
│   └── GlobalExceptionHandler.java # Centralized @ControllerAdvice
│
└── FinanceApplication.java        # Spring Boot entry point
```

---

## 🗄️ Database Design

The database is hosted on **Supabase** (PostgreSQL). Below is the complete schema used by the application.

### Entity-Relationship Overview

```
roles (1) ──< users (1) ──< financial_records
```

### SQL Schema

```sql
-- ===============================
-- 1. ROLES TABLE
-- ===============================
CREATE TABLE roles (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (name) VALUES ('VIEWER'), ('ANALYST'), ('ADMIN');


-- ===============================
-- 2. USERS TABLE (JWT + OAuth READY)
-- ===============================
CREATE TABLE users (
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(100) UNIQUE NOT NULL,
    email         VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    role_id       INT REFERENCES roles(id),
    status        VARCHAR(20) DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    provider      VARCHAR(50),
    provider_id   VARCHAR(255),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ===============================
-- 3. FINANCIAL RECORDS TABLE
-- ===============================
CREATE TABLE financial_records (
    id          SERIAL PRIMARY KEY,
    amount      DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    type        VARCHAR(20)   NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    category    VARCHAR(100)  NOT NULL,
    description TEXT,
    date        DATE          NOT NULL,
    user_id     INT REFERENCES users(id) ON DELETE CASCADE,
    is_deleted  BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ===============================
-- 4. INDEXES
-- ===============================
CREATE INDEX idx_user_id   ON financial_records(user_id);
CREATE INDEX idx_type      ON financial_records(type);
CREATE INDEX idx_date      ON financial_records(date);
CREATE INDEX idx_category  ON financial_records(category);


-- ===============================
-- 5. UNIQUE OAUTH INDEX
-- ===============================
CREATE UNIQUE INDEX idx_provider_unique
ON users(provider, provider_id)
WHERE provider IS NOT NULL;


-- ===============================
-- 6. AUTO UPDATE TIMESTAMP FUNCTION
-- ===============================
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- ===============================
-- 7. TRIGGERS
-- ===============================
CREATE TRIGGER set_user_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER set_record_updated_at
BEFORE UPDATE ON financial_records
FOR EACH ROW EXECUTE FUNCTION update_timestamp();


-- ===============================
-- 8. SEED DATA
-- ===============================
INSERT INTO users (username, email, password_hash, role_id, status) VALUES
('admin',   'admin@gmail.com',   '123', 3, 'ACTIVE'),
('analyst', 'analyst@gmail.com', '123', 2, 'ACTIVE'),
('viewer',  'viewer@gmail.com',  '123', 1, 'ACTIVE');
```

---

## ⚙️ Setup & Installation

### Prerequisites

- ☕ Java 21+
- 📦 Maven 3.8+
- 🐘 PostgreSQL database (local or [Supabase](https://supabase.com))

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/finance-management-api.git
cd finance-management-api
```

### 2. Configure Database

Open `src/main/resources/application.properties` and update the following:

```properties
# PostgreSQL / Supabase
spring.datasource.url=jdbc:postgresql://<your-host>:<port>/<database>
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# JWT Secret (use a strong 256-bit key in production)
jwt.secret=your_very_secure_secret_key_here
jwt.expiration=86400000
```

> 💡 **Supabase users:** Use the connection string from your Supabase project under  
> `Settings → Database → Connection String → JDBC`.

### 3. Create the Database Schema

Run the SQL script from the [Database Design](#-database-design) section in your PostgreSQL client (psql, pgAdmin, or Supabase SQL editor).

### 4. Build & Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The server starts at: **`http://localhost:8080`**

---

## 📡 API Endpoints

### 🔐 Authentication

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/api/auth/login` | Public | Authenticate and receive a JWT token |

### 👤 User Management

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/api/users` | ADMIN | Create a new user |
| `GET` | `/api/users` | ADMIN | Get all users |
| `GET` | `/api/users/{id}` | ADMIN | Get user by ID |
| `PUT` | `/api/users/{id}` | ADMIN | Update user details |
| `DELETE` | `/api/users/{id}` | ADMIN | Delete a user |

### 📁 Financial Records

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/api/records` | ADMIN, ANALYST | Create a new financial record |
| `GET` | `/api/records` | ADMIN, ANALYST, VIEWER | Get all records (paginated, filterable) |
| `GET` | `/api/records/{id}` | ADMIN, ANALYST, VIEWER | Get a single record by ID |
| `PUT` | `/api/records/{id}` | ADMIN, ANALYST | Update a record |
| `DELETE` | `/api/records/{id}` | ADMIN | Soft-delete a record |

#### Query Parameters for `GET /api/records`

```
?type=INCOME             # Filter by type (INCOME or EXPENSE)
?category=Food           # Filter by category
?startDate=2025-01-01    # Filter from date
?endDate=2025-12-31      # Filter to date
?page=0                  # Page number (0-indexed)
?size=10                 # Records per page
```

### 📊 Dashboard (Public, Rate-Limited)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/dashboard/summary` | Public | Total income, total expense, net balance |
| `GET` | `/api/dashboard/category-totals` | Public | Totals grouped by category |
| `GET` | `/api/dashboard/monthly-trends` | Public | Monthly income vs expense breakdown |
| `GET` | `/api/dashboard/recent-records` | Public | Last N financial records |

---

## 🔑 Role Permissions

| Permission | VIEWER | ANALYST | ADMIN |
|---|:---:|:---:|:---:|
| Login | ✅ | ✅ | ✅ |
| View financial records | ✅ | ✅ | ✅ |
| View dashboard | ✅ | ✅ | ✅ |
| Create financial records | ❌ | ✅ | ✅ |
| Update financial records | ❌ | ✅ | ✅ |
| Delete financial records | ❌ | ❌ | ✅ |
| Manage users (CRUD) | ❌ | ❌ | ✅ |

---

## 📬 Example Requests & Responses

### Login

**Request:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6..."
}
```

---

### Create a Financial Record

**Request:**
```http
POST /api/records
Authorization: Bearer <your_jwt_token>
Content-Type: application/json

{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "description": "Monthly salary",
  "date": "2025-04-01"
}
```

**Response:**
```json
{
  "id": 1,
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "description": "Monthly salary",
  "date": "2025-04-01",
  "userId": 1,
  "createdAt": "2025-04-01T10:30:00"
}
```

---

### Dashboard Summary

**Request:**
```http
GET /api/dashboard/summary
```

**Response:**
```json
{
  "totalIncome": 15000.00,
  "totalExpense": 8500.00,
  "netBalance": 6500.00,
  "recordCount": 42
}
```

---

### Paginated Records with Filter

**Request:**
```http
GET /api/records?type=EXPENSE&category=Food&page=0&size=5
Authorization: Bearer <your_jwt_token>
```

**Response:**
```json
{
  "content": [
    {
      "id": 7,
      "amount": 250.00,
      "type": "EXPENSE",
      "category": "Food",
      "description": "Grocery shopping",
      "date": "2025-03-28"
    }
  ],
  "totalElements": 18,
  "totalPages": 4,
  "currentPage": 0
}
```

---

### Validation Error Response

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "amount": "must be greater than 0",
    "type": "must not be blank",
    "date": "must not be null"
  }
}
```

---

## 🛡️ Rate Limiting

Dashboard endpoints are publicly accessible but protected against abuse using **Bucket4j** (token bucket algorithm).

| Setting | Value |
|---|---|
| Strategy | Token Bucket (per IP address) |
| Capacity | 20 requests |
| Refill Rate | 20 requests per minute |
| Scope | `/api/dashboard/**` |

When the rate limit is exceeded, the API returns:

```http
HTTP 429 Too Many Requests

{
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again later."
}
```

> 📌 Each IP address gets its own independent bucket, so one client's overuse does not affect others.

---

## ✅ Validation & Error Handling

### Input Validation

All request bodies are validated using Jakarta Bean Validation annotations:

```java
// Example: CreateRecordRequest.java
@NotNull
@Positive
private BigDecimal amount;

@NotBlank
@Pattern(regexp = "INCOME|EXPENSE")
private String type;

@NotBlank
@Size(max = 100)
private String category;

@NotNull
@PastOrPresent
private LocalDate date;
```

### Global Exception Handling

All exceptions are handled centrally by `GlobalExceptionHandler.java` using `@ControllerAdvice`:

| Exception | HTTP Status | Description |
|---|---|---|
| `MethodArgumentNotValidException` | `400 Bad Request` | Bean validation failure |
| `EntityNotFoundException` | `404 Not Found` | Resource not found |
| `AccessDeniedException` | `403 Forbidden` | Insufficient role |
| `AuthenticationException` | `401 Unauthorized` | Invalid or missing token |
| `Exception` | `500 Internal Server Error` | Unexpected server errors |

---

## 📖 Swagger Documentation

Interactive API documentation is available via Swagger UI after starting the application:

```
http://localhost:8080/swagger-ui/index.html
```

### Using JWT in Swagger UI

1. Start the application and open Swagger UI
2. Click the **Authorize 🔒** button (top right)
3. Login via `POST /api/auth/login` to get your token
4. Enter: `Bearer <your_token>` in the authorization dialog
5. Click **Authorize** — all subsequent requests will include the JWT

> The Swagger configuration (`SwaggerConfig.java`) includes a pre-configured `SecurityScheme` for Bearer token support.

---

## 🚀 Future Improvements

- [ ] 🔑 **OAuth2 / Social Login** — Google & GitHub login (schema already prepared)
- [ ] 🔄 **Refresh Token** — Implement JWT refresh token mechanism
- [ ] 📧 **Email Notifications** — Alerts for budget thresholds
- [ ] 📈 **Forecasting** — ML-based spending predictions
- [ ] 📱 **Multi-currency Support** — Handle foreign currency records
- [ ] 🧪 **Unit & Integration Tests** — JUnit 5 + Mockito test coverage
- [ ] 🐳 **Dockerization** — Containerize with Docker + Docker Compose
- [ ] ☁️ **CI/CD Pipeline** — GitHub Actions for automated build/deploy
- [ ] 🔵 **Audit Logging** — Track all data changes with user attribution
- [ ] 💾 **Redis Caching** — Cache dashboard results for performance

---

## 👨‍💻 Author

<div align="center">

**Sandeep**  
Backend Developer | Java & Spring Boot Enthusiast

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/your-username)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/your-profile)

</div>

---

<div align="center">

⭐ **If you find this project useful, please give it a star!** ⭐

Made with ❤️ using Java & Spring Boot

</div>