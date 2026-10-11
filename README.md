# Banking Application — Spring Modulith

A production-grade banking application built with Java 17, Spring Boot 3.x, and Spring Modulith.
This project demonstrates **Modular Monolith architecture** as an evolution path between
traditional monoliths and microservices.

## Architecture

```
                 React Frontend
                       |
                       v
              Spring Boot Application
                       |
       +---------------+----------------+
       |               |                |
       v               v                v
   Customer        Account          (Transaction — Phase 3)
    Module          Module
       |               |
       +---------------+
               |
               v
          PostgreSQL
```

## Module Boundaries

Spring Modulith enforces that:
- `account` module can call `CustomerService` (public API)
- `account` module CANNOT access `CustomerRepository` (internal)
- All cross-module communication goes through public APIs or domain events

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.x |
| Architecture | Spring Modulith |
| Database | PostgreSQL 16 |
| Migration | Flyway |
| Build | Maven |
| Infrastructure | Docker |

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker Desktop

### Run Locally

```bash
# 1. Start the database
docker compose up -d

# 2. Run the application
mvn spring-boot:run

# 3. Verify it's running
curl http://localhost:8080/actuator/health
```

### Run Architecture Tests

```bash
# Verifies all module boundaries are intact
mvn test -Dtest=ModularityTests
```

## API Endpoints

### Customer
```
POST /api/customers          Register a new customer
GET  /api/customers/{id}     Get customer by ID
PUT  /api/customers/{id}/activate  Activate customer
```

### Account
```
POST /api/accounts           Create a new account
GET  /api/accounts?customerId={id}  List customer accounts
GET  /api/accounts/{id}      Get account by ID
PUT  /api/accounts/{id}/activate  Activate account
```

### System
```
GET /actuator/health         Application health
GET /actuator/modulith       Module structure
```

## Project Structure

```
src/main/java/com/bank/
├── BankingApplication.java      # Entry point
├── common/                      # Shared types (exceptions, DTOs)
│   ├── api/                     # ApiResponse, ErrorResponse, GlobalExceptionHandler
│   └── exception/               # BankingException hierarchy
├── customer/                    # Customer module (PUBLIC boundary)
│   ├── CustomerController.java
│   ├── CustomerService.java      # Module's public API
│   ├── CustomerRegisteredEvent.java  # Public domain event
│   ├── RegisterCustomerCommand.java
│   ├── CustomerResponse.java
│   └── internal/                # PRIVATE — only customer module accesses this
│       ├── Customer.java
│       ├── CustomerRepository.java
│       └── CustomerStatus.java
└── account/                     # Account module (PUBLIC boundary)
    ├── AccountController.java
    ├── AccountService.java       # Module's public API
    ├── CreateAccountCommand.java
    ├── AccountResponse.java
    └── internal/                # PRIVATE — only account module accesses this
        ├── Account.java
        ├── AccountRepository.java
        ├── AccountType.java
        ├── AccountStatus.java
        ├── AccountNumberGenerator.java
        └── InsufficientBalanceException.java
```

## Phase Roadmap

- [x] Phase 1: Architecture concepts
- [x] Phase 2: Customer + Account modules, module boundaries, events
- [ ] Phase 3: Transaction module + Ledger
- [ ] Phase 4: Spring Security + JWT
- [ ] Phase 5: Kafka integration
- [ ] Phase 6: Fraud detection
- [ ] Phase 7: Notifications
- [ ] Phase 8: Docker + Kubernetes
- [ ] Phase 9: AWS deployment
