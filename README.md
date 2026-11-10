# QUIET BATTLES™ – Product Service

The Product Service is a backend microservice responsible for managing product information and inventory operations in the QUIET BATTLES™ application.

It provides product management functionality, maintains stock availability, and supports communication with the Order Service during order creation and cancellation.

## Technology Stack

- Java 17
- Spring Boot 4.1.2
- Spring Security
- Spring Data JPA
- Spring WebMVC
- MySQL
- JSON Web Token (JWT)
- JJWT 0.12.6
- Bean Validation
- Spring Boot Actuator
- Maven

## Service Overview

| Property | Value |
|---|---|
| Service Name | Product Service |
| Port | 8093 |
| Database | qb_product_db |
| Build Tool | Maven |
| Authentication | JWT |
| Internal Service Authentication | X-INTERNAL-SERVICE-KEY |

## Key Features

### 1. Product Management

The service supports product administration through REST APIs.

- Create new products.
- Retrieve product details.
- Update existing products.
- Delete products.
- Maintain product information such as name, description, price, size, stock, and image references.

### 2. Product Inventory Management

The service manages product stock availability.

- Maintains available stock quantities.
- Supports stock reservation during order creation.
- Supports stock release when an order is cancelled.
- Uses atomic repository-level stock updates to help prevent inconsistent inventory changes.

### 3. Order Service Integration

The Product Service communicates with the Order Service for inventory operations.

**Order creation flow:**

1. Customer places an order.
2. Order Service requests stock reservation.
3. Product Service checks and reserves available stock.
4. Order processing continues after successful reservation.

**Order cancellation flow:**

1. Customer cancels an order.
2. Order Service requests stock release.
3. Product Service restores the reserved stock.

This integration helps maintain inventory consistency across the microservices.

### 4. Security

The service uses Spring Security and JWT-based authentication for protected API access.

Internal service communication uses the `X-INTERNAL-SERVICE-KEY` header to authenticate authorized requests from other backend services.

## Microservice Architecture

The Product Service operates independently and communicates with other services through the API Gateway and authorized internal service requests.

**Application flow:**

Frontend → API Gateway (8090) → Product Service (8093) → MySQL

**Inventory integration:**

Order Service (8094) ↔ Product Service (8093)

## Database Configuration

The service uses MySQL with the database:

`qb_product_db`

Create the database before starting the application:

```sql
CREATE DATABASE qb_product_db;
```

Configure the database connection and security settings in the application's configuration file.

Do not commit database passwords, JWT secrets, or internal service keys to GitHub.

## Running the Application Locally

### Prerequisites

- Java 17 or later
- Maven
- MySQL Server
- Git

### 1. Clone the repository

```bash
git clone https://github.com/Pooji2002/quiet_battles_product.git
```

### 2. Open the project

Open the cloned project in Eclipse, IntelliJ IDEA, or VS Code.

### 3. Configure MySQL

Create the `qb_product_db` database and configure the database connection.

Make sure the required security configuration is provided before running the service.

### 4. Run the service

Using Maven:

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application class from your IDE.

The service runs at:

`http://localhost:8093`

## Integration with Other Services

| Component | Port |
|---|---:|
| API Gateway | 8090 |
| Identity Service | 8091 |
| Content Service | 8092 |
| Product Service | 8093 |
| Order Service | 8094 |

The frontend accesses product APIs through the API Gateway. The Order Service communicates with the Product Service for stock reservation and release operations.

## Project Repository

GitHub: https://github.com/Pooji2002/quiet_battles_product

## Project Context

This service is part of the QUIET BATTLES™ full-stack fashion and lifestyle e-commerce prototype.

**Brand Tagline: WEAR YOUR BATTLES.**