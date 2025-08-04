# Todo AI App - API Reference

Base URL: `http://localhost:8080/api`

## Authentication

### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com", 
  "password": "securepassword123"
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securepassword123"
}
```
**Response:** JWT token for Authorization header

---

## Todos

**All todo endpoints require:** `Authorization: Bearer <jwt-token>`

### Get All Todos
```http
GET /api/todos
```

### Create Todo
```http
POST /api/todos
Content-Type: application/json

{
  "title": "Task title",
  "description": "Task description", 
  "priority": "HIGH",
  "dueDate": "2025-08-10T10:00:00"
}
```
**Priorities:** `LOW`, `MEDIUM`, `HIGH`, `URGENT`

### Update Todo
```http
PUT /api/todos/{id}
Content-Type: application/json

{
  "title": "Updated title",
  "description": "Updated description",
  "priority": "URGENT", 
  "dueDate": "2025-08-15T15:00:00"
}
```

### Mark Complete
```http
PATCH /api/todos/{id}/complete
```

### Toggle Completion
```http
PATCH /api/todos/{id}/toggle
```
**Note:** Switches between completed/uncompleted

### Delete Todo
```http
DELETE /api/todos/{id}
```

---

## AI Features

**All AI endpoints require:** `Authorization: Bearer <jwt-token>`

### Prioritize Todos
```http
POST /api/ai/prioritize
```
**Returns:** Todos reordered by AI importance

### Generate Suggestions
```http
POST /api/ai/suggest
```
**Returns:** 3 AI-generated task suggestions

### Get Suggestions
```http
GET /api/ai/suggestions
```
**Returns:** All saved AI suggestions for user

---

## Health Check

```http
GET /api/health
GET /actuator/health
```
**No authentication required**

---

## Response Format

### Success Response
```json
{
  "id": 1,
  "title": "Task title",
  "description": "Task description",
  "priority": "HIGH",
  "isCompleted": false,
  "dueDate": "2025-08-10T10:00:00",
  "createdAt": "2025-08-04T09:00:00",
  "updatedAt": "2025-08-04T09:00:00"
}
```

### Error Response
```json
{
  "message": "Error description"
}
```

---

## Quick Test Commands

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"password123"}'

# Create Todo
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"Test Task","priority":"HIGH"}'

# Toggle Todo
curl -X PATCH http://localhost:8080/api/todos/1/toggle \
  -H "Authorization: Bearer YOUR_TOKEN"
```
