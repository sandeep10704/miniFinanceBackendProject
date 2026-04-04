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
| `GET` | `/api/records` | ADMIN, ANALYST, VIEWER | Get all records (paginated) |
| `GET` | `/api/records/{id}` | ADMIN, ANALYST, VIEWER | Get a single record by ID |
| `PUT` | `/api/records/{id}` | ADMIN, ANALYST | Update a record |
| `DELETE` | `/api/records/{id}` | ADMIN | Soft-delete a record |
| `GET` | `/api/records/filter` | ADMIN, ANALYST, VIEWER | Filter records by type & category |

#### Pagination Parameters for `GET /api/records`

| Param | Type | Default | Description |
|---|---|---|---|
| `page` | `integer` | `0` | Page number (0-indexed) |
| `size` | `integer` | `20` | Records per page |
| `sort` | `string` | — | Sort field and direction (e.g. `date,desc`) |

### 📁 Financial Records — Filter

| Param | Type | Description |
|---|---|---|
| `type` | `string` | Filter by `INCOME` or `EXPENSE` |
| `category` | `string` | Filter by category name |

### 📊 Dashboard (Public, Rate-Limited)

> 🌐 These endpoints are **publicly accessible** — no JWT token required.
> 🛡️ Protected by **IP-based rate limiting** via Bucket4j.

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/dashboard/summary` | Public | Total income, total expense, net balance |
| `GET` | `/api/dashboard/category` | Public | Totals grouped by category |
| `GET` | `/api/dashboard/monthly` | Public | Monthly income vs expense breakdown |
| `GET` | `/api/dashboard/recent` | Public | Recent financial records |

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

### 🔐 Login

> **Swagger:** `auth-controller` → `POST /api/auth/login`

**Request:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@gmail.com",
  "password": "123"
}
```

**Response `200 OK`:**
```json
"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3MDk..."
```

> ⚠️ The login field is **`email`** (not `username`). This matches the `LoginRequest` schema as shown in Swagger.

---

### 👤 Get User by ID

> **Swagger:** `user-controller` → `GET /api/users/{id}`

**Parameters:**

| Name | Location | Type | Required | Description |
|---|---|---|---|---|
| `Authorization` | header | `string` | ✅ Yes | `Bearer <jwt_token>` |
| `id` | path | `integer($int64)` | ✅ Yes | User's numeric ID |

**Request:**
```http
GET /api/users/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK` — `UserDTO`:**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@gmail.com",
  "role": "ADMIN",
  "status": "ACTIVE"
}
```

---

### 📝 Create a Financial Record

> **Swagger:** `financial-record-controller` → `POST /api/records`

**Parameters:**

| Name | Location | Type | Required | Description |
|---|---|---|---|---|
| `Authorization` | header | `string` | ✅ Yes | `Bearer <jwt_token>` |

**Request Body — `CreateRecordRequest`:**
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

**Response `200 OK` — `FinancialRecordDTO`:**
```json
{
  "id": 1,
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "description": "Monthly salary",
  "date": "2025-04-01"
}
```

---

### 📊 Dashboard Summary

> **Swagger:** `dashboard-controller` → `GET /api/dashboard/summary`  
> 🌐 No authentication required · 🛡️ Rate limited by IP

**Request:**
```http
GET /api/dashboard/summary
```

**Response `200 OK`:**
```json
{
  "additionalProp1": "string",
  "additionalProp2": "string",
  "additionalProp3": "string"
}
```

> The dashboard returns a dynamic `Map<String, Object>` — keys depend on implementation (e.g., `totalIncome`, `totalExpense`, `netBalance`).

---

### 📋 Paginated Records

> **Swagger:** `financial-record-controller` → `GET /api/records`  
> Returns a `PageFinancialRecordDTO` (Spring Page wrapper)

**Request:**
```http
GET /api/records?page=0&size=5&sort=date,desc
Authorization: Bearer <your_jwt_token>
```

**Response `200 OK` — `PageFinancialRecordDTO`:**
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
  "number": 0,
  "size": 5,
  "first": true,
  "last": false
}
```

---

### 🔍 Filter Records

> **Swagger:** `financial-record-controller` → `GET /api/records/filter`

**Request:**
```http
GET /api/records/filter?type=EXPENSE&category=Food
Authorization: Bearer <your_jwt_token>
```

**Response `200 OK` — `List<FinancialRecordDTO>`:**
```json
[
  {
    "id": 7,
    "amount": 250.00,
    "type": "EXPENSE",
    "category": "Food",
    "description": "Grocery shopping",
    "date": "2025-03-28"
  }
]
```

---

### ❌ Validation Error Response

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

## 📖 Swagger API Documentation

This project uses **SpringDoc OpenAPI 3** to auto-generate interactive API documentation. Once the application is running, visit:

```
http://localhost:8080/swagger-ui/index.html
```

### What Swagger UI Provides

| Feature | Description |
|---|---|
| 📋 All Endpoints | Every API route is listed with method, path, and description |
| 📨 Request Schemas | Shows required fields, types, and validation constraints |
| 📩 Response Schemas | Documents all possible HTTP response codes and body shapes |
| 🔐 JWT Authorization | Built-in Authorize button to attach Bearer tokens to requests |
| ▶️ Try It Out | Execute real API calls directly from the browser |
| 📁 Grouped by Tag | Endpoints are grouped by controller (Auth, Users, Records, Dashboard) |

---

### 🔐 How to Authenticate in Swagger UI (Step-by-Step)

**Step 1 — Login via the API**

In Swagger UI, expand `POST /api/auth/login` and click **Try it out**.
Fill in the request body and hit **Execute**:

```json
{
  "username": "admin",
  "password": "123"
}
```

Copy the JWT token from the response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIs..."
}
```

**Step 2 — Authorize the Swagger Session**

Click the 🔒 **Authorize** button at the top-right of the Swagger UI page.
In the dialog box, enter the token in this exact format:

```
Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIs...
```

Click **Authorize**, then **Close**.

**Step 3 — Use Protected Endpoints**

All subsequent requests from Swagger UI will now automatically include the `Authorization: Bearer <token>` header. You can now test protected routes like:
- `GET /api/records`
- `POST /api/records`
- `DELETE /api/records/{id}`

---

### 🛠️ How Swagger is Configured (`SwaggerConfig.java`)

The `SwaggerConfig.java` class registers a `SecurityScheme` globally across all API operations so the JWT Authorize button appears in Swagger UI:

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Finance Management API")
                .version("1.0")
                .description("Backend REST API for managing financial records"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"))
            .components(new Components()
                .addSecuritySchemes("Bearer Auth",
                    new SecurityScheme()
                        .name("Bearer Auth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

> 📌 This means **every endpoint** in Swagger UI will show the 🔒 lock icon and respect the JWT token you authorize with. You do **not** need to manually set the header for each request.

---

### 📂 Swagger UI Endpoint Groups

> These groups match exactly what you see in Swagger UI at `http://localhost:8080/swagger-ui/index.html`

```
📁 user-controller
   ├── GET    /api/users/{id}           → Get user by ID
   ├── PUT    /api/users/{id}           → Update user
   ├── DELETE /api/users/{id}           → Delete user
   ├── GET    /api/users                → List all users
   └── POST   /api/users               → Create new user

📁 financial-record-controller
   ├── GET    /api/records/{id}         → Get record by ID
   ├── PUT    /api/records/{id}         → Update record
   ├── DELETE /api/records/{id}         → Soft delete record
   ├── GET    /api/records              → List all records (paginated)
   ├── POST   /api/records              → Create new record
   └── GET    /api/records/filter       → Filter by type & category

📁 auth-controller
   └── POST   /api/auth/login           → Authenticate, get JWT token

📁 dashboard-controller  (🌐 Public, 🛡️ Rate Limited)
   ├── GET    /api/dashboard/summary    → Financial summary
   ├── GET    /api/dashboard/recent     → Recent records
   ├── GET    /api/dashboard/monthly    → Monthly trends
   └── GET    /api/dashboard/category   → Category-wise totals
```

---

### 🗂️ Swagger Schemas Reference

Swagger UI exposes the following **DTO schemas** under the `Schemas` section:

#### `LoginRequest`
```json
{
  "email": "string",
  "password": "string"
}
```

#### `CreateUserRequest`
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "roleId": 0
}
```

#### `UserDTO`
```json
{
  "id": 0,
  "username": "string",
  "email": "string",
  "role": "string",
  "status": "string"
}
```

#### `CreateRecordRequest`
```json
{
  "amount": 0.0,
  "type": "string",
  "category": "string",
  "description": "string",
  "date": "string"
}
```

#### `FinancialRecordDTO`
```json
{
  "id": 0,
  "amount": 0.0,
  "type": "string",
  "category": "string",
  "description": "string",
  "date": "string"
}
```

#### `PageFinancialRecordDTO` _(Paginated response)_
```json
{
  "content": [ /* FinancialRecordDTO[] */ ],
  "totalPages": 0,
  "totalElements": 0,
  "number": 0,
  "size": 0,
  "first": true,
  "last": false,
  "empty": false
}
```

#### `PageableObject` _(Embedded in page responses)_
```json
{
  "pageNumber": 0,
  "pageSize": 20,
  "sort": { /* SortObject */ },
  "offset": 0,
  "paged": true,
  "unpaged": false
}
```

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

**Sai Venkata Sandeep**  
Backend Developer | Java & Spring Boot Enthusiast

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/sandeep10704/miniFinanceBackendProject)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/sai-venkata-sandeep-250855288)

</div>

---

<div align="center">

⭐ **If you find this project useful, please give it a star!** ⭐

Made with ❤️ using Java & Spring Boot

</div>