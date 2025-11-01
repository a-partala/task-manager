
# Task Manager
REST API for managing user tasks with authentication

## Technologies
Java 21, Spring Boot (Web, Security, Data JPA), PostgreSQL, Docker, JUnit, Mockito, Maven, Lombok

## Functionality
Basic task management with user authentication and role access

## Package structure
```
net.partala.task_manager
├── auth/
│   └── jwt/
├── config/
├── tasks/
├── users/
└── web/
```
## How to run

1. Build the project:
   ./mvnw clean package

2. Build and start containers:
   docker-compose up --build

3. Wait until logs show:
   "Started TaskManagerApplication"

4. Access the API:
   http://localhost:8080
## Environment Variables
- `USER_ASSIGNED_TASKS_LIMIT` — limit for tasks assigned to one user  
  (see others in `docker-compose.yml`)

## API endpoints
/auth/register
/auth/login
/users
/tasks/{id}/start
/tasks/{id}/complete
/tasks/params=?creatorId=1&assignedUserId=1&status=1&priority=1&pageSize=1&pageNum=1
## Request Examples
	POST /auth/register
	{
		"username": "alexey",
		"email": "alexey@gmail.com",
		"password": "12345678"
	}
	
	POST /auth/login
	{
		"username": "alexey",
		"password": "12345678"
	}
	
	POST /tasks
	{
		"title": "create front-end",
		"creatorId": 1,
		"deadlineDate": "2026-10-10T00:00:00",
		"priority": "MEDIUM"
	}

	POST /tasks/{taskId}/start
	{
		"userId": 1
	}
