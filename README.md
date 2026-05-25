[![codecov](https://codecov.io/gh/jonassavas/spring-task-api/branch/main/graph/badge.svg)](https://codecov.io/gh/jonassavas/spring-task-api)

# Spring Task API

> **Note:** This project is currently in development. The API is functional for core features, but more enhancements and frontend integration will soon be available!

A RESTful backend API for a task management system inspired by Trello. This project provides a scalable way to manage users, task boards, task groups, and tasks, with JWT-based authentication and PostgreSQL persistence. The project is primarily developed for learning and personal use, as I found Trello boards to be the most effective way to track assignments and deadlines during my studies. However, I had to customize the experience heavily using browser extensions to achieve the exact functionality I wanted. This project aims to replicate and improve that experience in a fully customizable, backend-driven solution.

---

## Features

- **User Authentication** – Register and login with JWT-based security.  
- **Task Boards** – Create, update, and manage boards for organizing tasks.  
- **Task Groups** – Organize tasks into customizable groups within boards.  
- **Tasks** – Create, update, move between groups, and delete tasks.  
- **Persistence** – All data is stored in a PostgreSQL database.  
- **Validation & Error Handling** – Request DTO validation, global exception handling, and clear error messages.  
- **RESTful Endpoints** – Fully structured API for easy frontend integration.  

---

## Architecture Overview

The API follows a **layered architecture** for maintainability and scalability:

1. **Controller Layer**  
   - Exposes REST endpoints.  
   - Handles input validation (`@Valid`) and HTTP responses.  

2. **Service Layer**  
   - Contains business logic and transactional operations.  
   - Manages creation, deletion, updates, and queries for tasks, groups, and boards.  

3. **Repository Layer**  
   - Uses Spring Data JPA to persist entities in PostgreSQL.  
   - Provides CRUD operations and custom queries when needed.  

4. **Domain / DTO Layer**  
   - **Entities** – Represent database tables (`UserEntity`, `TaskBoardEntity`, `TaskGroupEntity`, `TaskEntity`).  
   - **DTOs** – Handle request and response objects with validation rules.  
   - **Mappers** – Convert between entities and DTOs.  

**Data Flow Example:**  
1. Client sends a POST request to `/boards/{boardId}/groups` with a `CreateTaskGroupRequestDto`.  
2. Controller validates the request and calls the Service layer.  
3. Service creates a `TaskGroupEntity` and saves it via the Repository.  
4. Response DTO (`TaskGroupDto`) is returned to the client.  

---

## API Endpoints

### Authentication
- `POST /auth/register` – Register a new user  
- `POST /auth/login` – Authenticate and receive a JWT

### Users
- `GET /users/me` - List user information
- `PATCH /users/me` - Update user information (name, password, email)
- `DELETE /users/me` - Delete user/account

### Boards
- `GET /taskboards` – List user taskboards  
- `POST /taskboards` – Create a new taskboard  
- `PATCH /taskboards/{id}` – Update a taskboard  
- `DELETE /taskboards/{id}` – Delete a taskboard  

### Task Groups
- `GET /taskboards/{boardId}/groups` – List task groups in a board  
- `POST /taskboards/{boardId}/groups` – Create a new task group  
- `PATCH /taskboards/{boardId}/groups/{groupId}` – Update a task group  
- `DELETE /taskboards/{boardId}/groups/{groupId}` – Delete a task group  

### Tasks
- `POST /groups/{groupId}/tasks` – Create a task in a group  
- `PATCH /tasks/{taskId}` – Update a task  
- `DELETE /tasks/{taskId}` – Delete a task  

---

## Getting Started

> Coming soon – instructions for running the API locally and connecting a frontend.  
