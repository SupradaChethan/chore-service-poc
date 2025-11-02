# Chore Service POC

A Spring Boot application that provides an AI-powered chat assistant for managing family chores. The assistant uses Azure OpenAI to understand natural language requests and automatically executes user and chore management operations.

## Features

- **AI Chat Assistant**: Natural language interface for managing users and chores
- **User Management**: Create, read, update, and delete family members
- **Chore Management**: Schedule and track daily chores for users
- **Conversation Memory**: Maintains context across chat sessions
- **H2 Console**: In-memory database with web console for development
- **RESTful APIs**: Direct endpoints for user and chore operations
- **File Logging**: Automatic log rotation with 30-day retention

## Tech Stack

- **Java 17**
- **Spring Boot 3.5.5**
- **Spring AI 1.0.0-M5** with Azure OpenAI integration
- **Spring Data JPA** with Hibernate
- **H2 Database** (in-memory)
- **Lombok** for clean code
- **Logback** for logging
- **Maven** for build management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Azure OpenAI account with deployed model

## Configuration

### Application Properties

Update `src/main/resources/application.properties` with your Azure OpenAI credentials:

```properties
# Azure OpenAI Configuration
spring.ai.azure.openai.api-key=YOUR_API_KEY
spring.ai.azure.openai.endpoint=YOUR_ENDPOINT
spring.ai.azure.openai.chat.options.deployment-name=YOUR_DEPLOYMENT_NAME
```

### Default Configuration

- **Server Port**: 8080
- **Database**: H2 in-memory (`jdbc:h2:mem:choredb`)
- **H2 Console**: http://localhost:8080/h2-console
- **Log Files**: `logs/chore-service.log`

## Building and Running

### Build the application

```bash
mvn clean package
```

### Run the application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/chore-service-poc-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Chat Assistant

#### Chat with AI Assistant
```http
POST /api/v1/assistant/chat
Content-Type: application/json

{
  "sessionId": "unique-session-id",
  "message": "Add a new user named Sarah with green color"
}
```

#### Health Check
```http
GET /api/v1/assistant/health
```

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users` | List all users |
| GET | `/api/v1/users/{id}` | Get user by ID |
| POST | `/api/v1/users` | Create new user |
| PUT | `/api/v1/users/{id}` | Update user |
| DELETE | `/api/v1/users/{id}` | Delete user |

**Example - Create User:**
```http
POST /api/v1/users
Content-Type: application/json

{
  "name": "Sarah",
  "color": "#10B981"
}
```

### Chore Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/chores` | List all chores |
| GET | `/api/v1/chores/{id}` | Get chore by ID |
| GET | `/api/v1/chores/user/{userId}` | Get chores for user |
| GET | `/api/v1/chores/date/{date}` | Get chores by date |
| POST | `/api/v1/chores` | Create new chore |
| PUT | `/api/v1/chores/{id}` | Update chore |
| DELETE | `/api/v1/chores/{id}` | Delete chore |

**Example - Create Chore:**
```http
POST /api/v1/chores
Content-Type: application/json

{
  "userId": 1,
  "description": "Take out the trash",
  "choreDate": "2025-11-02",
  "choreTime": "18:00"
}
```

## AI Assistant Capabilities

The chat assistant can understand natural language requests for:

### User Management
- "Add a new user named John with blue color"
- "Show me all family members"
- "Update Sarah's color to red"
- "Delete user with ID 5"

### Chore Management
- "Create a chore for John to take out trash tomorrow at 6 PM"
- "Show all chores for today"
- "List all chores assigned to Sarah"
- "Update chore 3 to be due at 3 PM instead"
- "Delete the chore with ID 7"

### Guidelines
- Dates must be in `YYYY-MM-DD` format (e.g., 2025-11-02)
- Times must be in `HH:MM` format (e.g., 18:00)
- The assistant confirms before deleting users or chores
- Conversation context is maintained per session ID

## Available AI Functions

The assistant has access to these functions:

**User Functions:**
- `createUser` - Add new family members
- `getUser` - Get user details
- `listUsers` - See all family members
- `updateUser` - Update user information (name, color)
- `deleteUser` - Remove a user (deletes all their chores)

**Chore Functions:**
- `createChore` - Add a new chore for a user
- `getChore` - Get chore details
- `listAllChores` - See all chores
- `listChoresByDate` - Get chores for a specific date
- `listChoresForUser` - Get chores for a specific user
- `updateChore` - Modify chore details
- `deleteChore` - Remove a chore

## Logging

### Log Locations

- **Console**: Real-time output to terminal
- **File**: `logs/chore-service.log`
- **Archived**: `logs/chore-service.YYYY-MM-DD.log`

### View Logs

**Windows PowerShell:**
```powershell
Get-Content logs/chore-service.log -Wait
```

**Linux/Mac:**
```bash
tail -f logs/chore-service.log
```

### Log Configuration

- **Retention**: 30 days
- **Max Size**: 1GB total
- **Rotation**: Daily
- **Root Level**: INFO
- **Application Level**: DEBUG (com.demo.cc)

## Database Console

Access the H2 database console at http://localhost:8080/h2-console

**Connection Details:**
- **JDBC URL**: `jdbc:h2:mem:choredb`
- **Username**: `sa`
- **Password**: _(empty)_

## Project Structure

```
src/main/java/com/demo/cc/
├── config/
│   ├── FunctionConfig.java       # Spring AI function bean definitions
│   ├── MemoryAdvisorConfig.java  # Chat memory configuration
│   └── ToolConfig.java           # AI tool implementations
├── controller/
│   ├── ChoreAssistantController.java  # Chat assistant endpoint
│   ├── ChoreController.java           # Chore REST API
│   └── UserController.java            # User REST API
├── domain/
│   ├── Chore.java                # Chore entity
│   └── User.java                 # User entity
├── dto/
│   ├── ChatRequest.java          # Chat request model
│   └── ChatResponse.java         # Chat response model
├── repository/
│   ├── ChoreRepository.java      # Chore data access
│   └── UserRepository.java       # User data access
└── service/
    ├── ChoreAssistantService.java  # AI chat logic
    ├── ChoreService.java           # Chore business logic
    └── UserService.java            # User business logic
```

## Development Notes

### Database Schema

The application uses JPA with `ddl-auto=create-drop`, which means:
- Schema is created on startup
- All data is lost on shutdown
- Perfect for development/POC

### SQL Logging

SQL queries are logged to console with formatting enabled:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Spring AI Function Calling

The AI assistant uses Spring AI's function calling feature to execute operations. Functions are registered in `ChoreAssistantService` and implemented in `ToolConfig`.

## Troubleshooting

### Chat assistant not creating users/chores

**Issue**: AI responds but doesn't execute operations

**Solution**: Ensure functions are registered in `ChoreAssistantService`:
```java
.defaultFunctions("createUser", "getUser", "listUsers", ...)
```

### Azure OpenAI connection errors

**Issue**: 401 Unauthorized or connection timeout

**Solutions**:
- Verify API key is correct in `application.properties`
- Check Azure OpenAI endpoint URL
- Confirm deployment name matches your Azure model

### H2 Console not accessible

**Issue**: 404 error at `/h2-console`

**Solution**: Ensure H2 console is enabled:
```properties
spring.h2.console.enabled=true
```

## License

This is a proof-of-concept project for demonstration purposes.

## Contributing

This is a POC project. For production use, consider:
- Using a persistent database (PostgreSQL, MySQL)
- Adding authentication/authorization
- Implementing proper error handling
- Adding comprehensive tests
- Securing API endpoints
- Removing hardcoded API keys (use environment variables or secrets management)
