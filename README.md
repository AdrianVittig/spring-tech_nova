# Tech Nova

A Spring Boot backend for an e-commerce platform with realistic order, payment, refund, inventory, procurement, budget,
and financial transaction flows.

## Overview

Tech Nova is a backend-focused portfolio project built with Java and Spring Boot.

The goal of the project is to go beyond basic CRUD by implementing real business rules such as:

- JWT authentication and role-based authorization
- inventory management
- order lifecycle management
- card and cash-on-delivery payments
- automated payment processing
- refunds and stock restoration
- supplier purchase orders
- weighted average acquisition cost
- company budget tracking
- financial transaction history
- pessimistic locking for critical operations
- Dockerized deployment
- Swagger / OpenAPI documentation

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- OAuth2 Resource Server
- JWT
- MySQL
- Gradle
- Lombok
- Swagger / OpenAPI
- Docker
- Docker Compose
- JUnit 5
- Mockito

## Architecture

The application follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL
```

The project also contains:

```text
DTOs
Entities
Mappers
Security configuration
Schedulers
Exception handling
Application initializers
```

## Main Features

### Authentication & Security

- User registration
- User login
- BCrypt password hashing
- Stateless JWT authentication
- `CUSTOMER` and `ADMIN` roles
- Role-based route protection
- Ownership checks for customer resources

Typical access rules:

```text
/auth/**          -> public
GET /products/**  -> public
/admin/**         -> ADMIN
/supply/**        -> ADMIN
other routes      -> authenticated
```

JWT tokens contain the authenticated user's email and role and have a limited expiration time.

### Products & Inventory

- Product management
- Inventory per product
- Stock validation
- Stock increase and decrease operations
- Prevention of purchasing unavailable quantities
- Pessimistic locking for stock-sensitive operations

### Orders

Customers can create orders containing multiple products.

During order creation the application:

1. validates the requested products
2. validates quantities
3. calculates the selling price
4. stores the unit price as an order-item snapshot
5. calculates the total order value
6. decreases available inventory
7. assigns the order to the authenticated user

Orders also support cancellation rules and stock restoration when applicable.

### Pricing

Product selling prices currently use a 20% markup over acquisition cost:

```text
selling price = acquisition cost × 1.20
```

### Payments

Supported payment methods:

- Card
- Cash on delivery

Card payments are successful immediately.

Cash-on-delivery payments are initially created with a pending status and are processed automatically after the
configured delay.

The payment flow prevents:

- duplicate payments for one order
- payment of orders in an invalid state
- invalid payment status transitions

### Payment Processing

A scheduled process checks pending payments and completes due payments.

A successful payment can trigger:

- payment status update
- order status update
- company budget increase
- incoming financial transaction
- invoice generation

### Refunds

The refund workflow supports:

- refund requests for eligible orders
- partial refunds
- refund quantity tracking
- inventory restoration
- company budget decrease
- outgoing financial transactions
- refund status management

### Procurement

Admins can create purchase orders to restock inventory.

When a purchase order is completed:

- inventory is increased
- the product acquisition price is recalculated
- company budget is decreased
- an outgoing financial transaction is created

The project uses weighted average cost when new stock is purchased at a different acquisition price.

Conceptually:

```text
new average cost =
(old stock value + new stock value)
/
(total stock quantity)
```

### Budget

The application keeps a company budget used by business operations.

Examples:

```text
Successful sale
    -> budget increases

Refund
    -> budget decreases

Supplier purchase
    -> budget decreases
```

Critical budget updates use pessimistic locking to reduce concurrent balance-update issues.

### Financial Transactions

The project stores a financial ledger containing operations such as:

- incoming sales
- outgoing refunds
- outgoing procurement expenses

This provides a history of money entering and leaving the system.

### Invoices

Invoices are generated as part of successful purchase/payment flows.

### Error Handling

The application uses centralized exception handling.

| Error                     | HTTP Status |
|---------------------------|------------:|
| Invalid input             |         400 |
| Invalid quantity          |         400 |
| Invalid credentials       |         401 |
| Forbidden operation       |         403 |
| Resource not found        |         404 |
| Conflict                  |         409 |
| Invalid status transition |         409 |
| Insufficient balance      |         409 |

## Concurrency

Several business operations modify shared resources such as inventory, budget, orders, payments, refunds, and purchase
orders.

The project uses database-level pessimistic locking where appropriate to reduce race conditions and inconsistent state.

Examples include:

- locked inventory reads before stock modification
- locked budget reads before balance changes
- locked payment retrieval before cancellation
- locked business entities during critical state transitions

## API Documentation

Swagger UI is available when the application is running:

```text
http://localhost:8084/swagger-ui/index.html
```

Use the Swagger **Authorize** option after logging in to access protected endpoints.

## Running with Docker

### Requirements

- Docker Desktop
- Docker Compose

### 1. Clone the repository

```bash
git clone <repository-url>
cd <repository-folder>
```

### 2. Create `.env`

Create a `.env` file in the project root:

```env
DB_PASSWORD=your_database_password
MYSQL_ROOT_PASSWORD=your_mysql_root_password
JWT_SECRET=your_long_random_jwt_secret
```

The `.env` file is intentionally excluded from Git.

### 3. Start the application

```bash
docker compose up --build
```

Docker Compose starts:

```text
tech_nova_mysql
tech_nova_app
```

The application is available at:

```text
http://localhost:8084
```

Swagger:

```text
http://localhost:8084/swagger-ui/index.html
```

### Stop the application

```bash
docker compose down
```

### Remove containers and database data

```bash
docker compose down -v
```

> `-v` removes the MySQL Docker volume and deletes persisted database data.

## Local Development

The application can also be started directly from IntelliJ.

Example datasource configuration:

```properties
spring.application.name=tech_nova
server.port=8084
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/tech_nova?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

Provide the required environment variables in the IDE run configuration.

## Testing

Unit tests are written with:

- JUnit 5
- Mockito

Covered service areas include:

- pricing
- budget
- products
- inventory
- users
- authentication
- JWT generation
- orders
- payments

Run tests with:

```bash
./gradlew test
```

Windows:

```bash
gradlew.bat test
```

## Docker Architecture

```text
Browser / API Client
        |
        | :8084
        v
+-------------------+
|   Tech Nova API   |
|    Spring Boot    |
+-------------------+
        |
        | mysql:3306
        v
+-------------------+
|       MySQL       |
+-------------------+
        |
        v
 Docker Volume
```

## Project Structure

```text
src
├── main
│   ├── java/com/vittig/tech_nova
│   │   ├── config
│   │   ├── controller
│   │   ├── data
│   │   │   ├── dto
│   │   │   ├── entity
│   │   │   └── repo
│   │   ├── exception
│   │   ├── security
│   │   ├── service
│   │   └── service/impl
│   └── resources
└── test
```

## Business Flow Summary

### Purchase

```text
Customer
   ↓
Order
   ↓
Inventory decrease
   ↓
Payment
   ↓
Order paid
   ↓
Budget increase
   ↓
Financial transaction
   ↓
Invoice
```

### Refund

```text
Paid order
   ↓
Refund
   ↓
Inventory restoration
   ↓
Budget decrease
   ↓
Financial transaction
```

### Procurement

```text
Admin
   ↓
Purchase order
   ↓
Inventory increase
   ↓
Weighted average cost update
   ↓
Budget decrease
   ↓
Financial transaction
```

## Future Improvements

Possible future additions:

- expanded integration tests
- concurrency tests
- Flyway or Liquibase migrations
- stricter duplicate item validation for refunds and purchase orders
- Redis caching
- rate limiting
- CI/CD pipeline
- frontend application
- email notifications
- external payment provider integration
- metrics and observability
- cloud deployment

## Status

The main backend functionality is implemented and the application can be started with Docker Compose.

This project demonstrates practical experience with Spring Boot, REST APIs, JPA/Hibernate, Spring Security, JWT
authentication, transactional business logic, concurrency control, MySQL, automated processing, testing, and Docker.
