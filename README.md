# E-commerce Microservices Application

This project is a modular E-Commerce application built using a microservices architecture. Each service is a standalone system implemented with Java (using Jakarta EE and Spring Data JPA) and managed independently. This architecture ensures scalability, maintainability, and ease of deployment.

## Table of Contents
- [Project Overview](#project-overview)
- [Modules and Features](#modules-and-features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [APIs](#apis)
- [Contributing](#contributing)
- [License](#license)

## Project Overview

The **E-commerce Microservices Application** includes multiple services to manage different aspects of an e-commerce platform, such as:
- **Inventory Management**
- **Order Processing**
- **Gateway and API Routing**
- **Centralized Configuration**

### Key Features:
- Independent service deployment and scaling.
- Secure interactions between services.
- Swagger documentation for APIs.
- Feign clients for inter-service communication.

## Modules and Features

The project consists of the following modules:

### 1. **Config Service**
- Provides centralized configuration for all other microservices.

### 2. **Gateway Service**
- Acts as the API gateway, routing all external requests to the appropriate microservices.
- Handles security and authentication flows.

### 3. **Inventory Service**
- Manages the inventory of products.
- CRUD operations for product management.
- Secure access with role-based authorization and token validation.

### 4. **Order Service**
- Manages customer orders and order-related entities (e.g., product items within orders).
- Communicates with the Inventory Service using Feign clients for product availability checks.

## Technologies Used

The project incorporates the following tools and technologies:

- **Backend Frameworks:**
    - Java 22 with **Jakarta EE** and **Spring Data JPA**

- **Security:**
    - Keycloak for authentication.
    - JWT-based security.

- **Communication:**
    - Feign clients for inter-service calls.

- **API Management:**
    - Spring Web and REST Controllers.
    - Swagger for API documentation.

- **Microservices Infrastructure:**
    - Config Service for centralized configuration.
    - Gateway Service for unified routing.

- **Build Tools:**
    - Maven for dependency management and builds.

- **Database:**
    - Example: H2 as the in-memory database for local development.

## Getting Started

To start working with the E-commerce Microservices Application:

### Prerequisites:
1. Install Java Development Kit (JDK 22 or later).
2. Install [Maven](https://maven.apache.org/).
3. Ensure **Keycloak** is running if using its authentication features.

### Clone the Repository:
```bash
git clone https://github.com/Dev7HD/e-com-Project_App
cd e-com-Project_App
```

### Build the Project:
Navigate into each service folder or the root where the `pom.xml` exists, then run:
```bash
mvn clean install
```

### Configuration:
Ensure that all `application.properties` files (in each service) are properly configured for your environment, such as database, Keycloak, or other dependencies.

## Running the Application

### Start the Config Service:
Navigate to the `config-service` directory and start the application:
```bash
mvn spring-boot:run
```

### Start the Gateway Service:
Navigate to the `gateway-service` directory and start the application:
```bash
mvn spring-boot:run
```

### Start the Inventory Service:
Navigate to the `inventory-service` directory and start the application:
```bash
mvn spring-boot:run
```

### Start the Order Service:
Navigate to the `order-service` directory and start the application:
```bash
mvn spring-boot:run
```

## APIs

### Swagger Documentation:
Swagger is enabled in multiple services. Once started, Swagger UI can be accessed:
- **Inventory Service Swagger:** `http://localhost:<port>/swagger-ui.html`
- **Order Service Swagger:** `http://localhost:<port>/swagger-ui.html`

### Sample Endpoints:
- Inventory Service:
    - `GET /api/products` - Get all products.
    - `POST /api/products` - Add a new product.
- Order Service:
    - `GET /api/orders` - Get all orders.
    - `POST /api/orders` - Place a new order.

### Authentication:
The services are secured using JWT tokens. You will need to obtain a valid token from Keycloak to access authenticated endpoints.

## Contributing

We welcome contributions! To contribute:
1. Fork the repository.
2. Create a branch (`git checkout -b feature-branch-name`).
3. Make your changes and commit them (`git commit -m 'Add new feature'`).
4. Push to the branch (`git push origin feature-branch-name`).
5. Open a pull request.

## License

This project is licensed under the [MIT License](LICENSE).

---

Happy Coding! 🎉