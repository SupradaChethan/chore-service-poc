## Project Name
- project name: chore-service-poc
- package name: com.demo.cc
- Package Name for Domain: com.demo.cc.domain
- Package Name for Repository: com.demo.cc.repository
- Package Name for Dto: com.demo.cc.dto
- Package Name for Service: com.demo.cc.service
- Package Name for Controller: com.demo.cc.controller
- Package Name for Utils: com.demo.cc.util

## Build tool
- Use Maven.

## REST endpoint design
- Follow REST best practices for designing the endpoints
- Use Nomenclature best practices for naming the endpoints
- Document the endpoint with best practices
- Endpoint needs to be versioned like /api/v1/[endpoint_name]

## Development Stack
- Java 17
- SpringBoot 3.5.7
- Spring AI, please refer to the documentation from https://spring.io/projects/spring-ai.
- Lombok
- Logging using logback lirbary

## Database Configuration
- Use H2 Console DB

## Architecture
- It has to be a microservice architecture
- Please look at the front end screen of calender located in @docs\calender_view.png
- This is a calender app which will help multiple users in a day to keep track of their chores.
- The app should be able to support get, add, delete, update users.
- Each user can have their own set of chores, which can be added, edited and deleted and fetched.

## AI feature
- This is Suprada's chore chart maintaining AI and it should help user to maintain daily chore in the family. Ai should be able to help maintaining the calender by add, delete and update users as well as chores.
