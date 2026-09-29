# NovaBank

A production-oriented **modular monolith** core-banking web application. One Spring Boot process, strict module boundaries (Spring Modulith).

**Current status:** Phase 1 increment 1 — **review before the first GitHub push**.

| Phase | Scope | Status |
| --- | --- | --- |
| 0 | Architecture, C4, events, schemas, ADRs | Accepted |
| 1 | Repo, build, shared kernel, identity/security skeleton, architecture test | **Increment 1 in review** |
| 2–7 | See architecture doc | Not started |

- Architecture: [docs/PHASE-0-ARCHITECTURE.md](docs/PHASE-0-ARCHITECTURE.md)
- Learning path: [docs/LEARNING-GUIDE.md](docs/LEARNING-GUIDE.md)
- This slice: [docs/PHASE-1-INCREMENT-1.md](docs/PHASE-1-INCREMENT-1.md)

## Prerequisites

- **JDK 21** (`java -version` must show 21)
- Maven Wrapper (`mvnw.cmd`) — no global Maven required

## Test

```bat
mvnw.cmd verify
```

## Run (health only until Keycloak is added)

```bat
mvnw.cmd -pl novabank-service spring-boot:run
```

`GET http://localhost:8080/actuator/health`
