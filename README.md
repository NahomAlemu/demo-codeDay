# codeDay-productivity

Productivity Spring Boot Application

This Spring Boot application offers functionalities related to User and Goal management.

Features

CRUD operations for User
Manage Goal entities associated with User
API Endpoints

User Controller
Add a user: POST /v1/users
Add users in batch: POST /v1/users/batch
Find all users or by name: GET /v1/users?firstName=John&lastName=Doe
Find user by ID: GET /v1/users/{id}
Update user by ID: PUT /v1/users/{id}
Deactivate a user: PUT /v1/users/{id}/deactivate
Goal Controller
Create a goal for a user: POST /v1/users/{userId}/goals
Get all goals for a user: GET /v1/users/{userId}/goals
Get goals by completion status for a user: GET /v1/users/{userId}/goals/complete/{isComplete}
Get goals by start date for a user: GET /v1/users/{userId}/goals/date/{startDate}
Dependencies

Database: MySQL
ORM: Spring Data JPA with Hibernate
Configuration

The application is configured via application properties:
server.servlet.context-path=/api
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

Environment variables DB_URL, DB_USERNAME, and DB_PASSWORD need to be set for database connectivity.

Setup & Run

Clone the repository.
Set up your MySQL database and update the configuration (DB_URL, DB_USERNAME, DB_PASSWORD) accordingly.
Navigate to the root folder of the project in your terminal.
Run the command ./mvnw spring-boot:run to start the application.
Access the application at default domain: codeday-productivity.azurewebsites.net
