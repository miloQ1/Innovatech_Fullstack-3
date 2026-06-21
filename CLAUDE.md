# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Innovatech is a microservices platform for managing academic/tech collaborative projects (clients, projects, phases, tasks, HR resources, collaboration, notifications, analytics). It consists of several independent Spring Boot services registered in a Eureka discovery server, fronted by a Spring Cloud Gateway BFF, and consumed by an Ionic/React frontend.

> Note: several service directories (`authService-Innovatech`, `bffGateway`, `ms_colaboracion_innovatech`, `ms_recursos_innovatech`, `proyectsService-Innovatech`, `reactFrontend-Innovatech`) contain their own `.git` directory left over from when they were separate repositories. The whole tree is now tracked by a single root git repo — run git commands from the repo root, not from inside these subfolders.

## Repository layout

| Path | Service | Port | DB schema | Stack |
|---|---|---|---|---|
| `eureka-server/` | Service discovery (Netflix Eureka) | 8761 | — | Spring Boot 3.x |
| `bffGateway/` | BFF / API Gateway (routing + JWT validation + circuit breakers) | 8090 | — | Spring Boot 3.4.5, Spring Cloud Gateway (reactive/WebFlux), Resilience4j |
| `authService-Innovatech/` | Auth & user management | 8080 | `auth_db` | Spring Boot 4.0.5, Spring Security, JJWT, MySQL |
| `proyectsService-Innovatech/` | Clients, Projects, Phases, Tasks | 8081 | `proyectos_db` | Spring Boot 4.0.5, Spring Data JPA, MySQL |
| `ms_recursos_innovatech/` | HR resources: professionals, skills, assignments, availability, absences | 8083 | `recursos_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms_colaboracion_innovatech/` | Collaboration: threads, comments, mentions, activity logs, attachments | 8084 | `colaboracion_db` | Spring Boot, Spring Data JPA, MySQL |
| `notificaciones/notificaciones/` | Notifications service | (no port set) | `notificaciones_db` | Spring Boot, MySQL |
| `AnaliticaInnovatech-main/` | Analytics: KPI definitions, KPI snapshots, widgets, alert rules, dashboard layouts/layout items | 8086 | `analitica_db` | Spring Boot 3.5.14, Spring Data JPA, MySQL |
| `frontend-ionic-capacitor/` | **Active** frontend — Ionic React + Vite + Capacitor, talks to the BFF | 5173 (dev) | — | React 18, Ionic React 8, Vite 7, TS |
| `reactFrontend-Innovatech/` | Earlier mockup ("EduTech") SPA using mocked data, kept for reference | 5173 (dev) | — | React 19, Vite 8, TS |

All Java services use Java 17 and Maven (via the `mvnw`/`mvnw.cmd` wrapper in each module).

## Running the platform locally

### Option A: Docker Compose (backend only)

`docker-compose.yml` at the repo root builds and runs MySQL + every backend service (not the frontend) on a shared network, with correct startup ordering via `depends_on`/healthchecks:

```bash
docker compose up --build
```

- MySQL runs on host port `3307` (container port `3306`); `docker/mysql/init/01-init-databases.sql` auto-creates all six schemas (`auth_db`, `proyectos_db`, `recursos_db`, `colaboracion_db`, `notificaciones_db`, `analitica_db`) on first boot.
- Inside the compose network each service's `SPRING_DATASOURCE_URL` is overridden via environment to point at `mysql:3306`; `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` is overridden to `http://eureka-server:8761/eureka/`.
- `notificaciones` gets `SERVER_PORT=8085` injected here (its `application.properties` has no port set otherwise) and is exposed on host `8085`, but it is still **not** wired into the gateway routes (see Known gaps).
- After compose is up, run the frontend separately (`npm run dev` in `frontend-ionic-capacitor/`) — it talks to the gateway on `localhost:8090` same as in manual mode.

### Option B: Manual, per-service

Startup order matters because of Eureka registration and the gateway's load-balanced (`lb://`) routes:

1. **MySQL** — create the schemas listed above (each service uses `spring.jpa.hibernate.ddl-auto=update`, so tables are created automatically once the schema exists). Default credentials in every `application.properties` are `root` with an empty password, connecting to `localhost:3306`. You can reuse `docker/mysql/init/01-init-databases.sql` against a local MySQL instance instead of typing the `CREATE DATABASE` statements by hand.
2. **`eureka-server`** (port 8761) — must be up before other services register.
3. Backend microservices, any order: `authService-Innovatech` (8080), `proyectsService-Innovatech` (8081), `ms_recursos_innovatech` (8083), `ms_colaboracion_innovatech` (8084), `AnaliticaInnovatech-main` (8086).
4. **`bffGateway`** (8090) — routes by Eureka service name (`auth-service`, `servicio-proyectos`, `ms-recursos`, `ms-colaboracion`, `analitica-service`), so it must come up after the services it proxies have registered.
5. **`frontend-ionic-capacitor`** — dev server proxies `/api` to the gateway on 8090.

Each Java service: `./mvnw spring-boot:run` (or `mvnw.cmd spring-boot:run` on Windows) from that service's directory. Build a jar with `./mvnw clean package`. Run a single test class with `./mvnw test -Dtest=ClassName`. Note: as of this writing none of the services have test classes under `src/test/java`.

All services share the same `jwt.secret` (set in each `application.properties`) — this must stay in sync between `authService-Innovatech` and `bffGateway` for token validation to work.

## BFF Gateway (`bffGateway/`)

The gateway is the single entry point for the frontend. Routing config lives entirely in `src/main/resources/application.properties` (not `application.yml` despite what the README says) as indexed `spring.cloud.gateway.routes[N]` properties — each route has a `Path=` predicate, an optional `JwtValidation` filter (custom, for protected routes), and a `CircuitBreaker` filter with a `forward:/fallback` URI handled by `FallbackController`.

Key classes (`src/main/java/cl/duoc/bffGateway/`):
- `filter/JwtValidationGatewayFilterFactory.java` — validates the JWT signature (shared secret with `authService`) and injects `X-User-Id` / `X-User-Name` headers before forwarding.
- `service/JwtService.java` — JWT parsing/validation helpers.
- `config/SecurityConfig.java` — WebFlux security is set to `permitAll()`; auth is enforced entirely by the custom JWT filter, not Spring Security.
- `controller/FallbackController.java` — circuit-breaker fallback endpoint.

To add a new route to an existing or new downstream service: add a new indexed `spring.cloud.gateway.routes[N].*` block (predicate `Path=/api/xxx/**`, `uri=lb://<eureka-app-name>`), add `JwtValidation` to `filters` if it needs auth, and add a matching `resilience4j.circuitbreaker.instances.<name>` / `resilience4j.timelimiter.instances.<name>` config block. CORS is centralized here via `spring.cloud.gateway.globalcors.*` (allows `localhost:5173` and `localhost:8100`).

## Backend microservices conventions

Each microservice follows a layered package structure: `controller/` → `service/` → `repository/` (Spring Data JPA) → `model/` (JPA entities), plus `dto/`/`DTOs/` for request/response objects. Package roots are inconsistent across services (legacy naming from when each was scaffolded separately):
- `authService-Innovatech`: `cl.innovatech.authService`
- `proyectsService-Innovatech`: `cl.innovatech.servicio_proyectos`
- `ms_recursos_innovatech`: `com.example.ms_recursos_innovatech`
- `ms_colaboracion_innovatech`: `com.example.ms_colaboracion_innovatech`
- `notificaciones`: `com.example.notificaciones`
- `AnaliticaInnovatech-main`: `com.innovatech.analitica`
- `bffGateway`: `cl.duoc.bffGateway`

Entity IDs vary by service: `authService` uses prefixed UUID strings (`USR-...`, `RFT-...`, `AUD-...`); `proyectsService`, `ms_recursos_innovatech`, and `ms_colaboracion_innovatech` use auto-incrementing `Long` IDs. Cross-service references (e.g. `assignedResourceId`, `projectManagerId`, `authorResourceId`) are stored as plain foreign-key-style IDs with no DB-level FK — there is no cross-database join, services are independent.

Each service that talks to MySQL exposes Swagger/OpenAPI at `/swagger-ui/index.html` and `/v3/api-docs` once running.

## Frontend (`frontend-ionic-capacitor/`)

This is the active frontend. Commands (run from `frontend-ionic-capacitor/`):
- `npm install`
- `npm run dev` — Vite dev server on 5173, proxies `/api/*` to `VITE_API_PROXY_TARGET` (defaults to `http://localhost:8090`, the BFF gateway).
- `npm run build` — `tsc -b && vite build`
- `npm run lint` — ESLint
- `npm run cap:add:android` / `npm run cap:sync` — Capacitor Android setup (build first)

Structure under `src/`:
- `api/` — one service module per backend domain (`authService.ts`, `projectService.ts`, `resourcesService.ts`, `collaborationService.ts`, `invitationService.ts`), all built on `apiClient.ts`.
- `config/backend.ts` — central map of backend ports and API route prefixes (`BACKEND_PORTS`, `BACKEND_ROUTES`); update this when adding new gateway routes.
- `context/` + `hooks/` — `AuthContext`/`useAuth` for session state.
- `layouts/` — `AuthLayout` (public) and `AppLayout` (Ionic split-pane + menu, authenticated).
- `pages/`, `components/` — organized by domain (`auth`, `projects`, `resources`, `invitations`, `shared`).
- `routes/` — `AppRouter` using `IonReactRouter` (React Router 5.3.4, required by `@ionic/react-router`).

For Capacitor/Android, set `VITE_API_BASE_URL` (e.g. `http://10.0.2.2:8090` for the Android emulator); for browser dev, leave it empty and rely on the Vite `/api` proxy.

`reactFrontend-Innovatech/` is an older fully-mocked SPA prototype (no real backend calls) — not the one to extend unless explicitly asked.

## Known gaps / in-flux areas

- `AnaliticaInnovatech-main` (`analitica-service`) has no Docker registry/CI wiring beyond the local `docker-compose.yml` entry, and the frontend only has the data layer (`api/analyticsService.ts`, `types/analytics.ts`) for KPIs/snapshots/widgets — no dashboard UI pages consume it yet. `AlertRuleController` (`/api/alerts`), `DashboardLayoutController` (`/api/layouts`) and `LayoutItemController` (`/api/layout-items`) have gateway routes but no frontend service module yet.
- `notificaciones/notificaciones` has no `server.port` set and is not wired into the BFF gateway routes yet (only gets a port via the `docker-compose.yml` override).
- `ms_colaboracion_innovatech` also exposes `/api/attachments`, `/api/mentions`, `/api/resource-skills` and `/api/activity-logs` (note the gateway route for activity is `/api/activity/**`, not `/api/activity-logs/**`) that aren't all covered by gateway routes — check `bffGateway/src/main/resources/application.properties` before assuming a frontend call will reach the backend.
