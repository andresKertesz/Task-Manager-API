# Task Manager API

A Spring Boot REST API for managing tasks with a clean, layered architecture.

## Project Structure

```
src/main/java/com/akertesz/task_manager_api/
├── TaskManagerApiApplication.java          # Main Spring Boot application class
├── config/                                # Configuration classes
│   └── ApplicationConfig.java             # Application-wide configuration
├── controller/                            # REST API controllers
│   └── TaskController.java                # Task management endpoints
├── dto/                                   # Data Transfer Objects
│   ├── TaskDto.java                       # Task data transfer object
│   ├── CreateTaskRequest.java             # Request DTO for creating tasks
│   └── UpdateTaskRequest.java             # Request DTO for updating tasks
├── exception/                             # Exception handling
│   └── GlobalExceptionHandler.java        # Global exception handler
├── model/                                 # Entity models
│   ├── Task.java                          # Task entity
│   ├── TaskStatus.java                    # Task status enum
│   └── TaskPriority.java                  # Task priority enum
├── repository/                            # Data access layer
│   └── TaskRepository.java                # Task repository interface
└── service/                               # Business logic layer
    ├── TaskService.java                   # Task service interface
    ├── TaskServiceImpl.java               # Task service implementation
    └── TaskStatistics.java                # Task statistics data class
```

## Architecture Layers

### 1. Model Layer (`model/`)
- **Task.java**: JPA entity representing a task with fields for id, title, description, status, priority, and timestamps
- **TaskStatus.java**: Enum for task statuses (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
- **TaskPriority.java**: Enum for task priorities (LOW, MEDIUM, HIGH, URGENT)

### 2. DTO Layer (`dto/`)
- **TaskDto.java**: Data transfer object for task data
- **CreateTaskRequest.java**: Request DTO for creating new tasks with validation
- **UpdateTaskRequest.java**: Request DTO for updating existing tasks

### 3. Repository Layer (`repository/`)
- **TaskRepository.java**: JPA repository interface extending JpaRepository with custom query methods
- Includes methods for finding tasks by status, priority, due date, and more

### 4. Service Layer (`service/`)
- **TaskService.java**: Service interface defining business logic operations
- **TaskServiceImpl.java**: Implementation of the service interface
- **TaskStatistics.java**: Data class for task statistics

### 5. Controller Layer (`controller/`)
- **TaskController.java**: REST controller providing HTTP endpoints for task management

### 6. Exception Handling (`exception/`)
- **GlobalExceptionHandler.java**: Centralized exception handling for validation errors and general exceptions

### 7. Configuration (`config/`)
- **ApplicationConfig.java**: Application-wide configuration including CORS settings

## API Endpoints

### Task Management
- `POST /api/tasks` - Create a new task
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get a task by ID
- `PUT /api/tasks/{id}` - Update a task
- `DELETE /api/tasks/{id}` - Delete a task

### Task Queries
- `GET /api/tasks/status/{status}` - Get tasks by status
- `GET /api/tasks/priority/{priority}` - Get tasks by priority
- `GET /api/tasks/overdue` - Get overdue tasks
- `GET /api/tasks/search?title={title}` - Search tasks by title
- `GET /api/tasks/created-between?startDate={date}&endDate={date}` - Get tasks created between dates
- `GET /api/tasks/ordered` - Get tasks ordered by priority and due date

### Task Operations
- `PATCH /api/tasks/{id}/status?status={status}` - Change task status
- `PATCH /api/tasks/{id}/priority?priority={priority}` - Change task priority
- `GET /api/tasks/statistics` - Get task statistics

## Features

- **Layered Architecture**: Clean separation of concerns with repository, service, and controller layers
- **Data Validation**: Input validation using Jakarta validation annotations
- **Exception Handling**: Centralized exception handling with detailed error responses
- **CORS Support**: Cross-origin resource sharing enabled for frontend integration
- **JPA Integration**: Spring Data JPA for database operations
- **RESTful Design**: RESTful API design following best practices
- **Comprehensive CRUD**: Full CRUD operations for task management
- **Advanced Queries**: Custom repository methods for complex task queries
- **Statistics**: Task statistics and analytics endpoints

## Getting Started

1. Ensure you have Java 17+ and Maven installed
2. Configure your database connection in `application.properties`
3. Run the application: `mvn spring-boot:run`
4. Access the API at `http://localhost:8080/api/tasks`

## Dependencies

The project uses Spring Boot with the following key dependencies:
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Validation
- H2 Database (for development)
- Spring Boot DevTools (for development)
