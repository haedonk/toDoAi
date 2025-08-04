# 📝 Todo AI App - REST API Documentation

A Spring Boot 3 REST API for a personal Todo application with AI-powered task management features using OpenAI integration.

## 🚀 Features

- **User Authentication**: JWT-based secure authentication
- **Todo Management**: Full CRUD operations for todos
- **AI Integration**: OpenAI-powered task prioritization and suggestions
- **Security**: User data isolation with BCrypt password hashing
- **Database**: PostgreSQL with JPA/Hibernate
- **Deployment**: Ready for Render deployment

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **Authentication**: JWT + Spring Security
- **AI**: OpenAI GPT-3.5-turbo
- **Build Tool**: Gradle

## 🏃‍♂️ Getting Started

### Prerequisites
- Java 17+
- PostgreSQL database
- OpenAI API key (optional - app works with mock data)

### Environment Variables
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/todoai
DATABASE_USERNAME=todoai
DATABASE_PASSWORD=your-password
JWT_SECRET=your-jwt-secret-key
OPENAI_API_KEY=your-openai-api-key
PORT=8080
```

### Running the Application
```bash
# Clone and navigate to project
cd todo-ai-app

# Run the application
./gradlew bootRun

# Or build and run the JAR
./gradlew build
java -jar build/libs/todo-ai-app-1.0.0.jar
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Content Type
All requests should include:
```
Content-Type: application/json
```

### Authentication
Most endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## 🔐 Authentication Endpoints

### Register User
**POST** `/api/auth/register`

Create a new user account.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securepassword123"
}
```

**Response:**
```json
{
  "message": "User registered successfully!"
}
```

**Validation Rules:**
- Username: 3-50 characters, unique
- Email: Valid email format, unique
- Password: Minimum 8 characters

---

### Login
**POST** `/api/auth/login`

Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "securepassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com"
}
```

---

## ✅ Todo Management Endpoints

### Get All Todos
**GET** `/api/todos`

Retrieve all todos for the authenticated user.

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive API documentation",
    "priority": "HIGH",
    "isCompleted": false,
    "dueDate": "2025-08-10T10:00:00",
    "createdAt": "2025-08-04T09:00:00",
    "updatedAt": "2025-08-04T09:00:00"
  }
]
```

---

### Create Todo
**POST** `/api/todos`

Create a new todo item.

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Request Body:**
```json
{
  "title": "Complete project documentation",
  "description": "Write comprehensive API documentation",
  "priority": "HIGH",
  "dueDate": "2025-08-10T10:00:00"
}
```

**Priority Options:** `LOW`, `MEDIUM`, `HIGH`, `URGENT`

**Response:**
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive API documentation",
  "priority": "HIGH",
  "isCompleted": false,
  "dueDate": "2025-08-10T10:00:00",
  "createdAt": "2025-08-04T09:00:00",
  "updatedAt": "2025-08-04T09:00:00"
}
```

---

### Update Todo
**PUT** `/api/todos/{id}`

Update an existing todo item.

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Request Body:**
```json
{
  "title": "Updated todo title",
  "description": "Updated description",
  "priority": "URGENT",
  "dueDate": "2025-08-15T15:00:00"
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Updated todo title",
  "description": "Updated description",
  "priority": "URGENT",
  "isCompleted": false,
  "dueDate": "2025-08-15T15:00:00",
  "createdAt": "2025-08-04T09:00:00",
  "updatedAt": "2025-08-04T10:30:00"
}
```

---

### Mark Todo as Complete
**PATCH** `/api/todos/{id}/complete`

Mark a specific todo as completed.

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive API documentation",
  "priority": "HIGH",
  "isCompleted": true,
  "dueDate": "2025-08-10T10:00:00",
  "createdAt": "2025-08-04T09:00:00",
  "updatedAt": "2025-08-04T11:00:00"
}
```

---

### Toggle Todo Completion
**PATCH** `/api/todos/{id}/toggle`

Toggle a todo's completion status between completed and uncompleted.

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive API documentation",
  "priority": "HIGH",
  "isCompleted": false,
  "dueDate": "2025-08-10T10:00:00",
  "createdAt": "2025-08-04T09:00:00",
  "updatedAt": "2025-08-04T12:00:00"
}
```

**Note:** This endpoint will switch the completion status - if the todo is completed, it becomes uncompleted, and vice versa.

---

### Delete Todo
**DELETE** `/api/todos/{id}`

Delete a specific todo item.

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "message": "Todo deleted successfully!"
}
```

---

## 🤖 AI-Powered Endpoints

### Prioritize Todos
**POST** `/api/ai/prioritize`

Use OpenAI to intelligently reorder your todos by importance and urgency.

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
[
  {
    "id": 3,
    "title": "Urgent deadline task",
    "priority": "URGENT",
    "isCompleted": false
  },
  {
    "id": 1,
    "title": "Important project work",
    "priority": "HIGH",
    "isCompleted": false
  },
  {
    "id": 2,
    "title": "Regular maintenance",
    "priority": "MEDIUM",
    "isCompleted": false
  }
]
```

**Note:** If OpenAI API key is not provided, returns todos sorted by existing priority levels.

---

### Generate AI Suggestions
**POST** `/api/ai/suggest`

Generate 3 contextual task suggestions based on your current todos.

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
[
  {
    "id": 1,
    "suggestedTask": "Review and organize your email inbox",
    "priority": "MEDIUM",
    "createdAt": "2025-08-04T12:00:00"
  },
  {
    "id": 2,
    "suggestedTask": "Plan tomorrow's priorities",
    "priority": "HIGH",
    "createdAt": "2025-08-04T12:00:00"
  },
  {
    "id": 3,
    "suggestedTask": "Take a 15-minute break for mental wellness",
    "priority": "LOW",
    "createdAt": "2025-08-04T12:00:00"
  }
]
```

---

### Get AI Suggestions
**GET** `/api/ai/suggestions`

Retrieve all AI-generated suggestions for the authenticated user.

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
[
  {
    "id": 1,
    "suggestedTask": "Review and organize your email inbox",
    "priority": "MEDIUM",
    "createdAt": "2025-08-04T12:00:00"
  }
]
```

---

## 🏥 Health Check

### Application Health
**GET** `/actuator/health` or **GET** `/api/health`

Check if the application is running properly. Both endpoints are available for convenience.

**Response:**
```json
{
  "status": "UP",
  "service": "todo-ai-app"
}
```

---

## 🚨 Error Responses

### Validation Errors
**Status:** `400 Bad Request`
```json
{
  "username": "Username must be between 3 and 50 characters",
  "email": "Email should be valid"
}
```

### Authentication Errors
**Status:** `401 Unauthorized`
```json
{
  "message": "Invalid username or password"
}
```

### Resource Not Found
**Status:** `400 Bad Request`
```json
{
  "message": "Todo not found or access denied"
}
```

### Server Errors
**Status:** `500 Internal Server Error`
```json
{
  "message": "An unexpected error occurred"
}
```

---

## 📝 Usage Examples

### Complete Workflow Example

1. **Register a new user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "securepassword123"
  }'
```

2. **Login to get JWT token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securepassword123"
  }'
```

3. **Create a todo:**
```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Complete project documentation",
    "description": "Write comprehensive API documentation",
    "priority": "HIGH",
    "dueDate": "2025-08-10T10:00:00"
  }'
```

4. **Get AI suggestions:**
```bash
curl -X POST http://localhost:8080/api/ai/suggest \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

5. **Prioritize todos with AI:**
```bash
curl -X POST http://localhost:8080/api/ai/prioritize \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🚀 Deployment

### Render Deployment
The application is configured for easy deployment on Render:

1. Connect your GitHub repository to Render
2. Set environment variables in Render dashboard
3. Deploy using the included `render.yaml` configuration

### Docker Deployment
```bash
# Build Docker image
docker build -t todo-ai-app .

# Run container
docker run -p 8080:8080 \
  -e DATABASE_URL="your-db-url" \
  -e JWT_SECRET="your-jwt-secret" \
  -e OPENAI_API_KEY="your-openai-key" \
  todo-ai-app
```

---

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Password Hashing**: BCrypt encryption for passwords
- **User Isolation**: Users can only access their own data
- **CORS Configuration**: Configured for frontend integration
- **Input Validation**: Comprehensive request validation

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

---

## 📄 License

This project is licensed under the MIT License.

---

## 📞 Support

For questions or issues, please create an issue in the GitHub repository.

---

**Happy task management with AI! 🚀**
