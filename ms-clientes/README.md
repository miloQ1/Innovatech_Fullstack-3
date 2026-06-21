# 📁 proyectsService-Innovatech

> Microservicio REST para la gestión de **Clientes**, **Proyectos**, **Fases** y **Tareas** dentro de la plataforma Innovatech.

---

## 📋 Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Stack Tecnológico](#stack-tecnológico)
- [Configuración y Ejecución](#configuración-y-ejecución)
- [Modelos de Datos](#modelos-de-datos)
  - [Client](#client)
  - [Project](#project)
  - [Phase](#phase)
  - [Task](#task)
- [Enumeraciones](#enumeraciones)
- [Endpoints de la API](#endpoints-de-la-api)
  - [Clientes](#-clientes---apiclients)
  - [Proyectos](#-proyectos---apiprojects)
  - [Fases](#-fases---apiphases)
  - [Tareas](#-tareas---apitasks)
- [Ejemplos de Uso](#ejemplos-de-uso)

---

## Descripción General

`proyectsService-Innovatech` es un microservicio basado en **Spring Boot** que expone una API RESTful para gestionar el ciclo de vida completo de proyectos tecnológicos. El servicio permite:

- Registrar y administrar **clientes**.
- Asociar **proyectos** a clientes, con control de presupuesto, fechas y estado.
- Organizar proyectos en **fases** secuenciales.
- Crear y hacer seguimiento de **tareas** dentro de proyectos y fases, con soporte para subtareas e historial de estados.

---

## Stack Tecnológico

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.5 |
| Spring Data JPA | — |
| Spring Web MVC | — |
| MySQL | 8.x+ |
| Maven | — |

---

## Configuración y Ejecución

### 1. Requisitos previos

- Java 17+
- MySQL 8+
- Maven 3.8+

### 2. Configurar la base de datos

Crear la base de datos en MySQL:

```sql
CREATE DATABASE proyectos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar `application.properties`

El archivo se encuentra en `src/main/resources/application.properties`:

```properties
spring.application.name=servicio-proyectos

spring.datasource.url=jdbc:mysql://localhost:3306/proyectos_db?useSSL=false&serverTimezone=America/Santiago
spring.datasource.username=root
spring.datasource.password=

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

> ⚠️ Reemplaza `username` y `password` con tus credenciales reales de MySQL.

### 4. Ejecutar el servicio

```bash
./mvnw spring-boot:run
```

El servicio quedará disponible en: `http://localhost:8080`

---

## Modelos de Datos

### Client

Representa a un cliente de la empresa.

| Campo | Tipo | Requerido | Descripción |
|---|---|---|---|
| `clientId` | `Long` | Auto | Identificador único (generado automáticamente) |
| `name` | `String` | ✅ | Nombre del cliente (máx. 150 caracteres) |
| `industry` | `String` | ❌ | Sector o industria del cliente (máx. 100 caracteres) |
| `contactName` | `String` | ❌ | Nombre de la persona de contacto (máx. 150 caracteres) |
| `contactEmail` | `String` | ❌ | Email de contacto (máx. 150 caracteres) |
| `status` | `ClientStatus` | ✅ | Estado del cliente (`ACTIVE` / `INACTIVE`) |
| `createdAt` | `LocalDateTime` | Auto | Fecha y hora de creación (asignado automáticamente) |

---

### Project

Representa un proyecto asociado a un cliente.

| Campo | Tipo | Requerido | Descripción |
|---|---|---|---|
| `projectId` | `Long` | Auto | Identificador único |
| `client` | `Client` | ✅ | Cliente propietario del proyecto |
| `code` | `String` | ✅ | Código único del proyecto (máx. 50 caracteres) |
| `name` | `String` | ✅ | Nombre del proyecto (máx. 200 caracteres) |
| `description` | `String` | ❌ | Descripción detallada del proyecto (TEXT) |
| `startDate` | `LocalDate` | ❌ | Fecha de inicio del proyecto |
| `endDate` | `LocalDate` | ❌ | Fecha de término del proyecto |
| `budget` | `BigDecimal` | ❌ | Presupuesto asignado (15,2) |
| `status` | `ProjectStatus` | ✅ | Estado del proyecto |
| `progressPct` | `BigDecimal` | ❌ | Porcentaje de avance (0.00 – 100.00) |
| `projectManagerId` | `Long` | ❌ | ID del jefe de proyecto (referencia externa) |
| `createdAt` | `LocalDateTime` | Auto | Fecha y hora de creación |

---

### Phase

Representa una fase dentro de un proyecto.

| Campo | Tipo | Requerido | Descripción |
|---|---|---|---|
| `phaseId` | `Long` | Auto | Identificador único |
| `name` | `String` | ✅ | Nombre de la fase (máx. 150 caracteres) |
| `sequenceOrder` | `Integer` | ✅ | Orden secuencial de la fase dentro del proyecto |
| `plannedStart` | `LocalDate` | ❌ | Fecha planificada de inicio |
| `plannedEnd` | `LocalDate` | ❌ | Fecha planificada de término |
| `status` | `PhaseStatus` | ✅ | Estado de la fase |

---

### Task

Representa una tarea dentro de un proyecto (opcionalmente asociada a una fase). Soporta jerarquía (tarea padre / subtareas) e historial de cambios de estado.

| Campo | Tipo | Requerido | Descripción |
|---|---|---|---|
| `taskId` | `Long` | Auto | Identificador único |
| `title` | `String` | ✅ | Título de la tarea (máx. 200 caracteres) |
| `description` | `String` | ❌ | Descripción detallada (TEXT) |
| `priority` | `TaskPriority` | ✅ | Prioridad de la tarea |
| `status` | `TaskStatus` | ✅ | Estado actual de la tarea |
| `assignedResourceId` | `Long` | ❌ | ID del recurso asignado (referencia externa) |
| `estimatedHours` | `BigDecimal` | ❌ | Horas estimadas (7,2) |
| `actualHours` | `BigDecimal` | ❌ | Horas reales consumidas (7,2) |
| `startDate` | `LocalDate` | ❌ | Fecha de inicio de la tarea |
| `dueDate` | `LocalDate` | ❌ | Fecha límite de entrega |
| `statusHistory` | `List<TaskStatusHistory>` | Auto | Historial de cambios de estado |

---

## Enumeraciones

### `ClientStatus`
| Valor | Descripción |
|---|---|
| `ACTIVE` | Cliente activo |
| `INACTIVE` | Cliente inactivo |

---

### `ProjectStatus`
| Valor | Descripción |
|---|---|
| `PLANNING` | En planificación |
| `ACTIVE` | En ejecución |
| `ON_HOLD` | Pausado |
| `COMPLETED` | Completado |
| `CANCELLED` | Cancelado |

---

### `PhaseStatus`
| Valor | Descripción |
|---|---|
| `PENDING` | Pendiente de inicio |
| `IN_PROGRESS` | En progreso |
| `COMPLETED` | Completada |
| `CANCELLED` | Cancelada |

---

### `TaskStatus`
| Valor | Descripción |
|---|---|
| `TODO` | Por hacer |
| `IN_PROGRESS` | En progreso |
| `IN_REVIEW` | En revisión |
| `DONE` | Completada |
| `CANCELLED` | Cancelada |

---

### `TaskPriority`
| Valor | Descripción |
|---|---|
| `LOW` | Baja prioridad |
| `MEDIUM` | Prioridad media |
| `HIGH` | Alta prioridad |
| `CRITICAL` | Prioridad crítica |

---

## Endpoints de la API

> **Base URL:** `http://localhost:8080`

---

### 👤 Clientes — `/api/clients`

#### `POST /api/clients`
Crea un nuevo cliente.

- **Request Body:** `Client` (JSON)
- **Response:** `201 Created` — El cliente creado.

**Body de ejemplo:**
```json
{
  "name": "Tech Solutions SpA",
  "industry": "Software",
  "contactName": "Juan Pérez",
  "contactEmail": "juan.perez@techsolutions.cl",
  "status": "ACTIVE"
}
```

**Respuesta de ejemplo:**
```json
{
  "clientId": 1,
  "name": "Tech Solutions SpA",
  "industry": "Software",
  "contactName": "Juan Pérez",
  "contactEmail": "juan.perez@techsolutions.cl",
  "status": "ACTIVE",
  "createdAt": "2026-04-23T10:00:00"
}
```

---

#### `GET /api/clients`
Obtiene todos los clientes registrados.

- **Response:** `200 OK` — Lista de clientes.

---

#### `GET /api/clients/{id}`
Obtiene un cliente por su ID.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID del cliente |

- **Response:** `200 OK` — El cliente encontrado.

---

#### `PUT /api/clients/{id}`
Actualiza los datos de un cliente existente.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID del cliente a actualizar |

- **Request Body:** `Client` (JSON)
- **Response:** `200 OK` — El cliente actualizado.

---

#### `DELETE /api/clients/{id}`
Elimina un cliente por su ID.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID del cliente a eliminar |

- **Response:** `204 No Content`

---

### 📂 Proyectos — `/api/projects`

#### `POST /api/projects/client/{clientId}`
Crea un nuevo proyecto y lo asocia a un cliente.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `clientId` | `Long` | ID del cliente al que pertenece el proyecto |

- **Request Body:** `Project` (JSON)
- **Response:** `201 Created` — El proyecto creado.

**Body de ejemplo:**
```json
{
  "code": "PROJ-2026-001",
  "name": "Plataforma E-Commerce",
  "description": "Desarrollo de plataforma de comercio electrónico.",
  "startDate": "2026-05-01",
  "endDate": "2026-11-30",
  "budget": 50000000.00,
  "status": "PLANNING",
  "progressPct": 0.00,
  "projectManagerId": 10
}
```

---

#### `GET /api/projects`
Obtiene todos los proyectos registrados.

- **Response:** `200 OK` — Lista de proyectos.

---

#### `GET /api/projects/client/{clientId}`
Obtiene todos los proyectos de un cliente específico.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `clientId` | `Long` | ID del cliente |

- **Response:** `200 OK` — Lista de proyectos del cliente.

---

#### `GET /api/projects/{id}`
Obtiene un proyecto por su ID.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID del proyecto |

- **Response:** `200 OK` — El proyecto encontrado.

---

#### `PUT /api/projects/{id}`
Actualiza los datos de un proyecto existente.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID del proyecto a actualizar |

- **Request Body:** `Project` (JSON)
- **Response:** `200 OK` — El proyecto actualizado.

---

#### `DELETE /api/projects/{id}`
Elimina un proyecto por su ID.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID del proyecto a eliminar |

- **Response:** `204 No Content`

---

### 🔖 Fases — `/api/phases`

#### `POST /api/phases/project/{projectId}`
Crea una nueva fase y la asocia a un proyecto.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `projectId` | `Long` | ID del proyecto al que pertenece la fase |

- **Request Body:** `Phase` (JSON)
- **Response:** `201 Created` — La fase creada.

**Body de ejemplo:**
```json
{
  "name": "Diseño UX/UI",
  "sequenceOrder": 1,
  "plannedStart": "2026-05-01",
  "plannedEnd": "2026-05-31",
  "status": "PENDING"
}
```

---

#### `GET /api/phases/project/{projectId}`
Obtiene todas las fases de un proyecto.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `projectId` | `Long` | ID del proyecto |

- **Response:** `200 OK` — Lista de fases del proyecto.

---

#### `GET /api/phases/{id}`
Obtiene una fase por su ID.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID de la fase |

- **Response:** `200 OK` — La fase encontrada.

---

#### `PUT /api/phases/{id}`
Actualiza los datos de una fase existente.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID de la fase a actualizar |

- **Request Body:** `Phase` (JSON)
- **Response:** `200 OK` — La fase actualizada.

---

#### `DELETE /api/phases/{id}`
Elimina una fase por su ID.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID de la fase a eliminar |

- **Response:** `204 No Content`

---

### ✅ Tareas — `/api/tasks`

#### `POST /api/tasks/project/{projectId}`
Crea una nueva tarea y la asocia a un proyecto.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `projectId` | `Long` | ID del proyecto al que pertenece la tarea |

- **Request Body:** `Task` (JSON)
- **Response:** `201 Created` — La tarea creada.

**Body de ejemplo:**
```json
{
  "title": "Diseñar wireframes de pantalla principal",
  "description": "Crear los wireframes en Figma para la home del e-commerce.",
  "priority": "HIGH",
  "status": "TODO",
  "assignedResourceId": 5,
  "estimatedHours": 16.00,
  "startDate": "2026-05-03",
  "dueDate": "2026-05-10"
}
```

---

#### `GET /api/tasks/project/{projectId}`
Obtiene todas las tareas de un proyecto.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `projectId` | `Long` | ID del proyecto |

- **Response:** `200 OK` — Lista de tareas del proyecto.

---

#### `GET /api/tasks/phase/{phaseId}`
Obtiene todas las tareas de una fase específica.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `phaseId` | `Long` | ID de la fase |

- **Response:** `200 OK` — Lista de tareas de la fase.

---

#### `GET /api/tasks/{id}`
Obtiene una tarea por su ID.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID de la tarea |

- **Response:** `200 OK` — La tarea encontrada.

---

#### `PUT /api/tasks/{id}`
Actualiza los datos de una tarea existente.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID de la tarea a actualizar |

- **Request Body:** `Task` (JSON)
- **Response:** `200 OK` — La tarea actualizada.

---

#### `DELETE /api/tasks/{id}`
Elimina una tarea por su ID.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | ID de la tarea a eliminar |

- **Response:** `204 No Content`

---

## Ejemplos de Uso

### Flujo completo: crear cliente → proyecto → fase → tarea

```bash
# 1. Crear un cliente
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Retail Corp",
    "industry": "Retail",
    "contactName": "María González",
    "contactEmail": "mgonzalez@retailcorp.cl",
    "status": "ACTIVE"
  }'

# 2. Crear un proyecto para el cliente (ID: 1)
curl -X POST http://localhost:8080/api/projects/client/1 \
  -H "Content-Type: application/json" \
  -d '{
    "code": "RC-2026-001",
    "name": "Sistema de Inventario",
    "status": "PLANNING",
    "startDate": "2026-06-01",
    "endDate": "2026-12-31",
    "budget": 30000000.00
  }'

# 3. Crear una fase para el proyecto (ID: 1)
curl -X POST http://localhost:8080/api/phases/project/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Análisis de Requerimientos",
    "sequenceOrder": 1,
    "plannedStart": "2026-06-01",
    "plannedEnd": "2026-06-20",
    "status": "PENDING"
  }'

# 4. Crear una tarea para el proyecto (ID: 1)
curl -X POST http://localhost:8080/api/tasks/project/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Levantamiento de requerimientos funcionales",
    "priority": "HIGH",
    "status": "TODO",
    "estimatedHours": 20.00,
    "startDate": "2026-06-01",
    "dueDate": "2026-06-10"
  }'
```

---

## Estructura del Proyecto

```
proyectsService-Innovatech/
├── src/
│   ├── main/
│   │   ├── java/cl/innovatech/servicio_proyectos/
│   │   │   ├── controller/
│   │   │   │   ├── ClientController.java
│   │   │   │   ├── ProjectController.java
│   │   │   │   ├── PhaseController.java
│   │   │   │   └── TaskController.java
│   │   │   ├── model/
│   │   │   │   ├── Client.java
│   │   │   │   ├── Project.java
│   │   │   │   ├── Phase.java
│   │   │   │   ├── Task.java
│   │   │   │   ├── TaskStatusHistory.java
│   │   │   │   └── enums/
│   │   │   │       ├── ClientStatus.java
│   │   │   │       ├── ProjectStatus.java
│   │   │   │       ├── PhaseStatus.java
│   │   │   │       ├── TaskStatus.java
│   │   │   │       └── TaskPriority.java
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

---

*Documentación generada para Innovatech — proyectsService v0.0.1-SNAPSHOT*
