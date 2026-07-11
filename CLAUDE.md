# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Descripción del proyecto

Innovatech es una plataforma de microservicios para gestionar proyectos colaborativos académicos/tecnológicos. Está organizada en servicios Spring Boot independientes registrados en Eureka, expuestos a través de un BFF Spring Cloud Gateway, y consumidos por un frontend Ionic/React.

Dominios de negocio separados como servicios: auth/usuarios, clientes, proyectos, recursos humanos, asignaciones, colaboración, archivos/adjuntos, registros de auditoría, notificaciones y analítica.

> Nota: varios directorios de servicio contienen su propia carpeta `.git` heredada de repositorios separados anteriores. Trata la carpeta raíz como el proyecto principal y ejecuta los comandos git desde ahí.

## Estructura del repositorio

| Ruta | Servicio | Puerto | Schema DB | Stack |
|---|---|---:|---|---|
| `eureka-server/` | Descubrimiento de servicios (Netflix Eureka) | 8761 | — | Spring Boot |
| `bffGateway/` | BFF / API Gateway, validación JWT, CORS, circuit breakers | 8090 | — | Spring Boot, Spring Cloud Gateway, Resilience4j |
| `authService-Innovatech/` | Autenticación y usuarios | 8080 | `auth_db` | Spring Boot, Spring Security, JJWT, MySQL |
| `proyectsService-Innovatech/` | Proyectos, fases, tareas, tableros y miembros de proyecto | 8081 | `proyectos_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms-clientes/` | Clientes extraído del servicio de proyectos | 8087 | `clientes_db` | Spring Boot, Spring Security/JWT, Spring Data JPA, MySQL |
| `ms_recursos_innovatech/` | Profesionales, habilidades, disponibilidad y ausencias | 8083 | `recursos_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms-asignaciones/` | Asignaciones de proyecto/recurso extraído del servicio de recursos | 8091 | `asignaciones_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms_colaboracion_innovatech/` | Hilos de colaboración, comentarios y menciones | 8084 | `colaboracion_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms-archivos/` | Adjuntos/archivos extraído del servicio de colaboración | 8088 | `archivos_db` | Spring Boot, Spring Data JPA, MySQL |
| `ms-auditoria/` | Registros de actividad/auditoría extraído del servicio de colaboración | 8089 | `auditoria_db` | Spring Boot, Spring Data JPA, MySQL |
| `notificaciones/notificaciones/` | Notificaciones, plantillas, preferencias, despachos y webhooks | 8085 | `notificaciones_db` | Spring Boot, Spring Data JPA, MySQL |
| `AnaliticaInnovatech-main/` | Analítica — definiciones de KPI, snapshots, widgets, layouts de dashboard, reglas de alerta | 8086 | `analitica_db` | Spring Boot, Spring Data JPA, MySQL |
| `frontend-ionic-capacitor/` | Frontend activo — Ionic React + Vite + Capacitor | 5173 dev | — | React 18, Ionic React 8, Vite, TypeScript |
| `reactFrontend-Innovatech/` | SPA React anterior de referencia/maqueta | 5173 dev | — | React, Vite, TypeScript |

Todos los servicios Java usan Java 17 y Maven a través del wrapper local `mvnw`/`mvnw.cmd`.

## Microservicios extraídos

Cuatro dominios fueron separados de servicios más grandes para reducir el acoplamiento y alcanzar el número de servicios requerido sin inventar funcionalidad artificial:

1. `ms-clientes` fue extraído de `proyectsService-Innovatech`. `Project` ahora almacena `clientId` como `Long` simple en lugar de una relación JPA `@ManyToOne Client`.
2. `ms-archivos` fue extraído de `ms_colaboracion_innovatech`. `Attachment` ahora almacena `commentId` como `Long` simple en lugar de hacer join con `Comment`.
3. `ms-auditoria` fue extraído de `ms_colaboracion_innovatech`. `ActivityLog` ya usaba IDs simples, por lo que esta separación es principalmente un lift-and-shift.
4. `ms-asignaciones` fue extraído de `ms_recursos_innovatech`. `Assignment` ahora almacena `resourceId` como `Long` simple en lugar de hacer join con `Professional`.

Las rutas del frontend no necesitaron cambiar porque ya llama al BFF usando rutas planas como `/api/clients`, `/api/attachments`, `/api/activity-logs` y `/api/assignments`.

## Ejecutar la plataforma localmente

Con Docker (MySQL se expone en el puerto del host **3307**, no 3306):

```bash
docker compose up --build
```

Orden de inicio manual:

1. MySQL. Crear todos los schemas desde `docker/mysql/init/01-init-databases.sql`.
2. `eureka-server` en `8761`.
3. Servicios backend: `authService-Innovatech` (`8080`), `proyectsService-Innovatech` (`8081`), `ms-clientes` (`8087`), `ms_recursos_innovatech` (`8083`), `ms-asignaciones` (`8091`), `ms_colaboracion_innovatech` (`8084`), `ms-archivos` (`8088`), `ms-auditoria` (`8089`), `notificaciones/notificaciones` (`8085`) y `AnaliticaInnovatech-main` (`8086`).
4. `bffGateway` en `8090` una vez que los servicios estén registrados en Eureka.
5. `frontend-ionic-capacitor` en `5173` para desarrollo en navegador.

Cada servicio Java puede iniciarse desde su propia carpeta con:

```bash
./mvnw spring-boot:run
```

Compilar un jar con:

```bash
./mvnw clean package -DskipTests
```

Ejecutar una clase de test específica:

```bash
./mvnw test -Dtest=MiClaseDeTest
```

Todos los servicios comparten el mismo `jwt.secret`; mantenerlo sincronizado entre `authService-Innovatech`, `bffGateway`, `proyectsService-Innovatech` y `ms-clientes`.

## BFF Gateway

El BFF es el punto de entrada único para el frontend. La configuración de rutas está en `bffGateway/src/main/resources/application.properties` usando propiedades indexadas `spring.cloud.gateway.routes[N]`.

Rutas protegidas importantes:

| Ruta | Servicio Eureka | Circuit breaker |
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
| `/api/kpis/**`, `/api/snapshots/**`, `/api/widgets/**`, `/api/alerts/**`, `/api/layouts/**`, `/api/layout-items/**` | `analitica-service` | `analiticaService` |
| `/api/notifications/**`, `/api/templates/**`, `/api/events/**`, `/api/dispatches/**`, `/api/preferences/**`, `/api/webhooks/**` | `notificaciones` | `notificationsService` |

Clases clave en `bffGateway/src/main/java/cl/duoc/bffGateway/`:

- `filter/JwtValidationGatewayFilterFactory.java`: valida JWT y reenvía `X-User-Id` / `X-User-Name`.
- `service/JwtService.java`: parseo y validación de firma JWT.
- `config/SecurityConfig.java`: la seguridad WebFlux está abierta; el filtro personalizado del gateway realiza la validación por ruta.
- `controller/FallbackController.java`: endpoint de fallback del circuit breaker.

## Convenciones de microservicios backend

Cada servicio usa una estructura en capas: `controller/` → `service/` → `repository/` → `model/`, más `dto/` cuando es necesario.

Paquetes raíz:

- `authService-Innovatech`: `cl.innovatech.authService`
- `proyectsService-Innovatech`: `cl.innovatech.servicio_proyectos`
- `ms-clientes`: `cl.innovatech.ms_clientes`
- `ms_recursos_innovatech`: `com.example.ms_recursos_innovatech`
- `ms-asignaciones`: `com.example.ms_asignaciones`
- `ms_colaboracion_innovatech`: `com.example.ms_colaboracion_innovatech`
- `ms-archivos`: `com.example.ms_archivos`
- `ms-auditoria`: `com.example.ms_auditoria`
- `notificaciones/notificaciones`: `com.example.notificaciones`
- `AnaliticaInnovatech-main`: `com.innovatech.analitica`
- `bffGateway`: `cl.duoc.bffGateway`

Las referencias entre servicios deben mantenerse como IDs simples (`clientId`, `resourceId`, `commentId`, `projectId`, etc.). No crear relaciones JPA entre schemas de distintos servicios.

## Llamadas HTTP entre servicios

Algunos servicios se llaman entre sí directamente vía REST (sin pasar por el BFF). Estas dependencias se inyectan como variables de entorno (configuradas en `docker-compose.yml` y en el `application.properties` de cada servicio):

| Llamante | Llama a | Variable de entorno |
|---|---|---|
| `auth-service` | `ms-recursos`, `notificaciones` | `MS_RECURSOS_BASE_URL`, `NOTIFICACIONES_BASE_URL` |
| `proyects-service` | `ms-recursos`, `ms-asignaciones`, `notificaciones` | `MS_RECURSOS_BASE_URL`, `MS_ASIGNACIONES_BASE_URL`, `NOTIFICACIONES_BASE_URL` |
| `ms-asignaciones` | `notificaciones` | `NOTIFICACIONES_BASE_URL` |
| `ms-colaboracion` | `ms-asignaciones`, `notificaciones` | `MS_ASIGNACIONES_BASE_URL`, `NOTIFICACIONES_BASE_URL` |
| `notificaciones` | `ms-recursos` | `RESOURCES_SERVICE_URL` |

Al ejecutar manualmente (sin Docker), configurar estas variables apuntando a `http://localhost:<puerto>`.

## Base de datos

`docker/mysql/init/01-init-databases.sql` crea todos los schemas de los servicios. Las tablas se crean automáticamente por Hibernate con `spring.jpa.hibernate.ddl-auto=update`.

`docker/mysql/init/02-migracion-dominios-extraidos.sql` documenta comandos de migración manual opcionales para entornos que ya tenían registros en los schemas anteriores. Como los scripts de init de Docker se ejecutan antes de que Hibernate cree las tablas, los inserts están comentados para ejecución manual posterior.

## Frontend

El frontend activo es `frontend-ionic-capacitor/`.

Comandos:

```bash
npm install
npm run dev
npm run build
npm run lint
```

### Arquitectura del frontend

- `src/api/` — un archivo por dominio (`authService.ts`, `projectService.ts`, etc.) construido sobre `apiClient.ts`, que maneja headers de auth, refresco de token ante 401 y fallback de URL para Android.
- `src/config/backend.ts` — lugar central para las URLs base de la API y todas las constantes de rutas (`BACKEND_ROUTES`). Editar aquí al agregar o renombrar rutas del BFF.
- `src/context/AuthContext.tsx` — provee `user`, `isAuthenticated`, `login`, `register`, `logout`. Limpia intencionalmente la sesión en cada inicio de la app (comportamiento de demo académica — no hay auto-login desde tokens almacenados).
- `src/routes/AppRouter.tsx` — todas las rutas. Las páginas protegidas se envuelven con `<ProtectedRoute>` + `<AppLayout>`.

El `apiClient` envía los headers `X-User-Id` y `X-User-Name` en cada petición (no solo en las autenticadas), tomados de `localStorage`.

### Capacitor / Android

Para Android, configurar `VITE_API_BASE_URL` con la URL del gateway. Valores por defecto:

- Emulador: `http://10.0.2.2:8090`
- Dispositivo físico: `http://192.168.1.9:8090` (fallback hardcodeado — actualizar con `VITE_ANDROID_PHYSICAL_API_BASE_URL` según sea necesario)

El cliente intenta automáticamente la URL principal y luego recorre `API_FALLBACK_BASE_URLS` si falla la conexión.
