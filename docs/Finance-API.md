# Finance API (Postman Collection)

## Overview
This document describes the **Finance API** Postman collection and is intended for end users who want to call the API directly (via Postman or curl).

**Base URL (collection variable):** `{{baseUrl}}` (default: `http://localhost:8080`)

The collection is organized into these folders:
- Auth
- Users
- Records
- Dashboard

## Authentication setup
The collection uses a bearer token variable:
- `{{authToken}}` — used as `Authorization: Bearer {{authToken}}` on secured endpoints.

### Suggested workflow
1. Call **Auth → Login** to obtain a token.
2. Copy the returned token into the collection/environment variable `authToken`.
3. Call secured endpoints under **Users** and **Records**.

> Note: This collection does not include an automated script to capture/set `authToken` from the login response; you’ll need to set it manually unless you add a test script.

## Environments & variables used
### Collection variables
| Variable | Default | Used for |
|---|---|---|
| `baseUrl` | `http://localhost:8080` | API host (prefix for all endpoints) |
| `authToken` | *(empty)* | Bearer token for secured endpoints |

### Additional variables referenced
These variables appear in request URLs and must be provided (typically as environment variables):

| Variable | Where used | Meaning |
|---|---|---|
| `userId` | Users → Get User By Id, Update User, Delete User | Target user identifier |
| `recordId` | Records → Get Record By Id, Update Record, Delete Record | Target record identifier |

---

## Request reference

### Auth

#### Login
| Property | Value |
|---|---|
| Method | `POST` |
| URL | `{{baseUrl}}/api/auth/login` |
| Auth | None (not set) |

**Headers**
| Key | Value |
|---|---|
| `Content-Type` | `application/json` |

**Body (raw JSON)**
```json
{
  "email": "user@example.com",
  "password": "password"
}
```

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request POST "{{baseUrl}}/api/auth/login" \
  --header "Content-Type: application/json" \
  --data '{
    "email": "user@example.com",
    "password": "password"
  }'
```

---

### Users

All requests in this folder include `Authorization: Bearer {{authToken}}`.

#### Create User
| Property | Value |
|---|---|
| Method | `POST` |
| URL | `{{baseUrl}}/api/users` |
| Auth | Bearer token via header |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |
| `Content-Type` | `application/json` |

**Body (raw JSON)**
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "123",
  "roleId": 1
}
```

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request POST "{{baseUrl}}/api/users" \
  --header "Authorization: Bearer {{authToken}}" \
  --header "Content-Type: application/json" \
  --data '{
    "username": "john",
    "email": "john@example.com",
    "password": "123",
    "roleId": 1
  }'
```

#### Get All Users
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `{{baseUrl}}/api/users` |
| Auth | Bearer token via header |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request GET "{{baseUrl}}/api/users" \
  --header "Authorization: Bearer {{authToken}}"
```

#### Get User By Id
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `{{baseUrl}}/api/users/{{userId}}` |
| Auth | Bearer token via header |

**Path variables**
| Name | Value |
|---|---|
| `userId` | `{{userId}}` |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request GET "{{baseUrl}}/api/users/{{userId}}" \
  --header "Authorization: Bearer {{authToken}}"
```

#### Update User
| Property | Value |
|---|---|
| Method | `PUT` |
| URL | `{{baseUrl}}/api/users/{{userId}}` |
| Auth | Bearer token via header |

**Path variables**
| Name | Value |
|---|---|
| `userId` | `{{userId}}` |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |
| `Content-Type` | `application/json` |

**Body (raw JSON)**
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "123",
  "roleId": 1
}
```

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request PUT "{{baseUrl}}/api/users/{{userId}}" \
  --header "Authorization: Bearer {{authToken}}" \
  --header "Content-Type: application/json" \
  --data '{
    "username": "john",
    "email": "john@example.com",
    "password": "123",
    "roleId": 1
  }'
```

#### Delete User
| Property | Value |
|---|---|
| Method | `DELETE` |
| URL | `{{baseUrl}}/api/users/{{userId}}` |
| Auth | Bearer token via header |

**Path variables**
| Name | Value |
|---|---|
| `userId` | `{{userId}}` |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request DELETE "{{baseUrl}}/api/users/{{userId}}" \
  --header "Authorization: Bearer {{authToken}}"
```

---

### Records

All requests in this folder include `Authorization: Bearer {{authToken}}`.

#### Create Record
| Property | Value |
|---|---|
| Method | `POST` |
| URL | `{{baseUrl}}/api/records` |
| Auth | Bearer token via header |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |
| `Content-Type` | `application/json` |

**Body (raw JSON)**
```json
{
  "amount": 100.0,
  "type": "income",
  "category": "salary",
  "description": "April salary",
  "date": "2026-04-01"
}
```

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request POST "{{baseUrl}}/api/records" \
  --header "Authorization: Bearer {{authToken}}" \
  --header "Content-Type: application/json" \
  --data '{
    "amount": 100.0,
    "type": "income",
    "category": "salary",
    "description": "April salary",
    "date": "2026-04-01"
  }'
```

#### Get All Records (paged)
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `{{baseUrl}}/api/records?page=0&size=5` |
| Auth | Bearer token via header |

**Query params**
| Key | Value |
|---|---|
| `page` | `0` |
| `size` | `5` |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request GET "{{baseUrl}}/api/records?page=0&size=5" \
  --header "Authorization: Bearer {{authToken}}"
```

#### Get Record By Id
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `{{baseUrl}}/api/records/{{recordId}}` |
| Auth | Bearer token via header |

**Path variables**
| Name | Value |
|---|---|
| `recordId` | `{{recordId}}` |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request GET "{{baseUrl}}/api/records/{{recordId}}" \
  --header "Authorization: Bearer {{authToken}}"
```

#### Update Record
| Property | Value |
|---|---|
| Method | `PUT` |
| URL | `{{baseUrl}}/api/records/{{recordId}}` |
| Auth | Bearer token via header |

**Path variables**
| Name | Value |
|---|---|
| `recordId` | `{{recordId}}` |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |
| `Content-Type` | `application/json` |

**Body (raw JSON)**
```json
{
  "amount": 100.0,
  "type": "income",
  "category": "salary",
  "description": "April salary",
  "date": "2026-04-01"
}
```

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request PUT "{{baseUrl}}/api/records/{{recordId}}" \
  --header "Authorization: Bearer {{authToken}}" \
  --header "Content-Type: application/json" \
  --data '{
    "amount": 100.0,
    "type": "income",
    "category": "salary",
    "description": "April salary",
    "date": "2026-04-01"
  }'
```

#### Delete Record
| Property | Value |
|---|---|
| Method | `DELETE` |
| URL | `{{baseUrl}}/api/records/{{recordId}}` |
| Auth | Bearer token via header |

**Path variables**
| Name | Value |
|---|---|
| `recordId` | `{{recordId}}` |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request DELETE "{{baseUrl}}/api/records/{{recordId}}" \
  --header "Authorization: Bearer {{authToken}}"
```

#### Filter Records
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `{{baseUrl}}/api/records/filter?type=&category=` |
| Auth | Bearer token via header |

**Query params**
| Key | Value |
|---|---|
| `type` | *(empty by default)* |
| `category` | *(empty by default)* |

**Headers**
| Key | Value |
|---|---|
| `Authorization` | `Bearer {{authToken}}` |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request GET "{{baseUrl}}/api/records/filter?type=&category=" \
  --header "Authorization: Bearer {{authToken}}"
```

---

### Dashboard

> Note: Dashboard requests in the collection do **not** currently include an `Authorization` header. If your API secures these endpoints, add `Authorization: Bearer {{authToken}}`.

#### Get Summary
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `{{baseUrl}}/api/dashboard/summary` |
| Auth | None (not set) |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request GET "{{baseUrl}}/api/dashboard/summary"
```

#### Get Category Totals
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `{{baseUrl}}/api/dashboard/category` |
| Auth | None (not set) |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request GET "{{baseUrl}}/api/dashboard/category"
```

#### Get Recent Records
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `{{baseUrl}}/api/dashboard/recent` |
| Auth | None (not set) |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request GET "{{baseUrl}}/api/dashboard/recent"
```

#### Get Monthly Trend
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `{{baseUrl}}/api/dashboard/monthly` |
| Auth | None (not set) |

**Tests (Postman)**
- Asserts status code is `200`

**curl**
```bash
curl --request GET "{{baseUrl}}/api/dashboard/monthly"
```

---

## Notes on examples / responses
This collection JSON does not include saved **Examples** (example responses) for requests. To add them in Postman:
1. Send a request.
2. In the response pane, click **Save Response** → **Save as example**.
3. Re-export / commit the updated collection.
