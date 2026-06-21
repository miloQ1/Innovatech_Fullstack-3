# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Innovatech is a microservices platform for managing academic/tech collaborative projects. It is organized around independent Spring Boot services registered in Eureka, exposed through a Spring Cloud Gateway BFF, and consumed by an Ionic/React frontend.

Business domains currently separated as services: auth/users, clients, projects, HR resources, assignments, collaboration, files/attachments, audit/activity logs, notifications, and the analytics routes referenced by the gateway.

> Note: several service directories contain their own legacy `.git` folder from earlier separate repositories. Treat the root folder as the main project and run root-level git commands from there.

## Repository layout

| Path | Service | Port | DB schema | Stack |
|---|---|---:|---|---|
| `eureka-server/` | Service discovery (Netflix Eureka) | 8761 | — | Spring Boot |
| `bffGateway/` | BFF / API Gateway, JWT validation, CORS, circuit breakers | 8090 | — | Spring Boot, Spring Cloud Gateway, Resilience4j |
| `authService-Innovatech/` | Authentication and users | 8080 | `auth_db` | Spring Boot, Spring Security, JJWT, MySQL |
| `proyectsService-Innovatech/` | Projects, phases, tasks, boards and project members | 8081 | `proyectos_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms-clientes/` | Clients extracted from projects service | 8087 | `clientes_db` | Spring Boot, Spring Security/JWT, Spring Data JPA, MySQL |
| `ms_recursos_innovatech/` | Professionals, skills, resource skills, availability and absences | 8083 | `recursos_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms-asignaciones/` | Project/resource assignments extracted from resources service | 8091 | `asignaciones_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms_colaboracion_innovatech/` | Collaboration threads, comments and mentions | 8084 | `colaboracion_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms-archivos/` | Attachments/files extracted from collaboration service | 8088 | `archivos_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms-auditoria/` | Activity logs/audit trail extracted from collaboration service | 8089 | `auditoria_db` | Spring Boot, Spring Data JPA, MySQL |
| `notificaciones/notificaciones/` | Notifications, templates, preferences, dispatches and webhooks | 8085 | `notificaciones_db` | Spring Boot, Spring Data JPA, MySQL |
| `frontend-ionic-capacitor/` | Active frontend — Ionic React + Vite + Capacitor | 5173 dev | — | React 18, Ionic React 8, Vite, TypeScript |
| `reactFrontend-Innovatech/` | Earlier React SPA reference/mockup | 5173 dev | — | React, Vite, TypeScript |

All Java services use Java 17 and Maven through their local `mvnw`/`mvnw.cmd` wrapper.

## Extracted microservices

Four domains were split out from larger services to reduce coupling and reach the required service count without inventing artificial functionality:

1. `ms-clientes` was extracted from `proyectsService-Innovatech`. `Project` now stores `clientId` as a plain `Long` instead of a JPA `@ManyToOne Client` relation.
2. `ms-archivos` was extracted from `ms_colaboracion_innovatech`. `Attachment` now stores `commentId` as a plain `Long` instead of joining with `Comment`.
3. `ms-auditoria` was extracted from `ms_colaboracion_innovatech`. `ActivityLog` already used plain IDs, so this split is mostly lift-and-shift.
4. `ms-asignaciones` was extracted from `ms_recursos_innovatech`. `Assignment` now stores `resourceId` as a plain `Long` instead of joining with `Professional`.

The frontend routes did not need to change because it already calls the BFF using flat paths such as `/api/clients`, `/api/attachments`, `/api/activity-logs` and `/api/assignments`.

## Running the platform locally

With Docker:

```bash
docker compose up --build
```

Manual startup order:

1. MySQL. Create all schemas from `docker/mysql/init/01-init-databases.sql`.
2. `eureka-server` on `8761`.
3. Backend services: `authService-Innovatech` (`8080`), `proyectsService-Innovatech` (`8081`), `ms-clientes` (`8087`), `ms_recursos_innovatech` (`8083`), `ms-asignaciones` (`8091`), `ms_colaboracion_innovatech` (`8084`), `ms-archivos` (`8088`), `ms-auditoria` (`8089`) and `notificaciones/notificaciones` (`8085`).
4. `bffGateway` on `8090` after the services are registered in Eureka.
5. `frontend-ionic-capacitor` on `5173` for browser development.

Each Java service can be started from its own folder using:

```bash
./mvnw spring-boot:run
```

Build a jar with:

```bash
./mvnw clean package -DskipTests
```

All services share the same `jwt.secret`; keep it synchronized between `authService-Innovatech`, `bffGateway`, `proyectsService-Innovatech` and `ms-clientes`.

## BFF Gateway

The BFF is the single entry point for the frontend. Routing config lives in `bffGateway/src/main/resources/application.properties` using indexed `spring.cloud.gateway.routes[N]` properties.

Important protected routes:

| Path | Eureka service | Circuit breaker |
|---|---|---|
| `/api/auth/**` | `auth-service` | `authService` |
| `/api/users/**` | `auth-service` | `authService` |
| `/api/projects/**`, `/api/phases/**`, `/api/tasks/**` | `servicio-proyectos` | `proyectsService` |
| `/api/clients/**` | `ms-clientes` | `clientsService` |
| `/api/professionals/**`, `/api/skills/**`, `/api/availability/**`, `/api/absences/**` | `ms-recursos` | `resourcesService` |
| `/api/assignments/**` | `ms-asignaciones` | `asignacionesService` |
| `/api/threads/**`, `/api/comments/**` | `ms-colaboracion` | `collaborationService` |
| `/api/attachments/**` | `ms-archivos` | `archivosService` |
| `/api/activity-logs/**` | `ms-auditoria` | `auditoriaService` |
| `/api/notifications/**`, `/api/templates/**`, `/api/events/**`, `/api/dispatches/**`, `/api/preferences/**`, `/api/webhooks/**` | `notificaciones` | `notificationsService` |

Key classes under `bffGateway/src/main/java/cl/duoc/bffGateway/`:

- `filter/JwtValidationGatewayFilterFactory.java`: validates JWT and forwards `X-User-Id` / `X-User-Name`.
- `service/JwtService.java`: JWT parsing and signature validation.
- `config/SecurityConfig.java`: WebFlux security is open; the custom gateway filter performs route-level validation.
- `controller/FallbackController.java`: circuit-breaker fallback endpoint.

## Backend microservice conventions

Each service uses a layered structure: `controller/` → `service/` → `repository/` → `model/`, plus `dto/` when needed.

Package roots:

- `authService-Innovatech`: `cl.innovatech.authService`
- `proyectsService-Innovatech`: `cl.innovatech.servicio_proyectos`
- `ms-clientes`: `cl.innovatech.ms_clientes`
- `ms_recursos_innovatech`: `com.example.ms_recursos_innovatech`
- `ms-asignaciones`: `com.example.ms_asignaciones`
- `ms_colaboracion_innovatech`: `com.example.ms_colaboracion_innovatech`
- `ms-archivos`: `com.example.ms_archivos`
- `ms-auditoria`: `com.example.ms_auditoria`
- `notificaciones/notificaciones`: `com.example.notificaciones`
- `bffGateway`: `cl.duoc.bffGateway`

Cross-service references must stay as plain IDs (`clientId`, `resourceId`, `commentId`, `projectId`, etc.). Do not create JPA relationships across service schemas.

## Database notes

`docker/mysql/init/01-init-databases.sql` creates all service schemas. Tables are created automatically by Hibernate with `spring.jpa.hibernate.ddl-auto=update`.

`docker/mysql/init/02-migracion-dominios-extraidos.sql` documents optional manual migration commands for existing environments that already had records in the old schemas. Because Docker init scripts run before Hibernate creates tables, those inserts are left commented for manual execution after the target tables exist.

## Frontend

The active frontend is `frontend-ionic-capacitor/`.

Commands:

```bash
npm install
npm run dev
npm run build
npm run lint
```

For Capacitor/Android, set `VITE_API_BASE_URL` to the gateway URL, for example `http://10.0.2.2:8090` on the Android emulator. In browser development, leave it empty and use the Vite `/api` proxy to `http://localhost:8090`.

## Known gaps / in-flux areas

- The gateway still references an analytics service as `analitica-service` for `/api/kpis/**`, `/api/snapshots/**` and `/api/widgets/**`, but its source code is not present in the current tree.
- The migration of old data from `proyectos_db.clients` to `clientes_db.clients` and from `recursos_db.assignments` to `asignaciones_db.assignments` must be done manually in already-populated environments after Hibernate has created the new tables.
