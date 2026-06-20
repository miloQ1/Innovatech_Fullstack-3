# authService — Innovatech

Servicio de autenticación y gestión de usuarios desarrollado con **Spring Boot 4**, **Spring Security**, **JWT (JJWT 0.13)** y **MySQL**. Expone una API REST que cubre registro, inicio de sesión, refresco de tokens, auditoría y administración de usuarios.

---

## Tabla de contenidos

1. [Stack tecnológico](#stack-tecnológico)
2. [Estructura del proyecto](#estructura-del-proyecto)
3. [Configuración y ejecución](#configuración-y-ejecución)
4. [Modelos de datos](#modelos-de-datos)
5. [DTOs de referencia](#dtos-de-referencia)
6. [API Reference](#api-reference)
   - [Auth — `/api/auth`](#auth----apiauth)
   - [Users — `/api/users`](#users----apiusers)
   - [Refresh Tokens — `/api/refresh-tokens`](#refresh-tokens----apirefresh-tokens)
   - [Audit Logs — `/api/auth-audit-logs`](#audit-logs----apiauth-audit-logs)
7. [Swagger / OpenAPI](#swagger--openapi)

---

## Stack tecnológico

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.5 |
| Spring Security | (incluido en Boot) |
| Spring Data JPA | (incluido en Boot) |
| JJWT | 0.13.0 |
| springdoc-openapi | 3.0.2 |
| MySQL Connector/J | (runtime) |
| BCryptPasswordEncoder | — |

---

## Estructura del proyecto

```
src/main/java/cl/innovatech/authService/
├── config/
│   └── SecurityBeansConfig.java       # Bean BCryptPasswordEncoder
├── controller/
│   ├── AuthController.java            # Registro, login, refresh, logout, me
│   ├── UserController.java            # CRUD de usuarios
│   ├── RefreshTokenController.java    # Gestión de refresh tokens
│   └── AuthAuditLogController.java    # Logs de auditoría
├── DTOs/
│   ├── request/                       # DTOs de entrada (validados con Bean Validation)
│   └── response/                      # DTOs de salida
├── model/
│   ├── User.java
│   ├── RefreshToken.java
│   └── AuthAuditLog.java
├── repository/                        # Repositorios JPA
└── service/                           # Lógica de negocio
```

---

## Configuración y ejecución

### Variables de entorno recomendadas

```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/innovatech_auth
spring.datasource.username=<usuario>
spring.datasource.password=<contraseña>

spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=<clave-secreta-base64>
jwt.expiration-ms=3600000
```

### Ejecutar localmente

```bash
./mvnw spring-boot:run
```

El servicio quedará disponible en `http://localhost:8080`.

---

## Modelos de datos

### User

| Campo | Tipo | Notas |
|---|---|---|
| `id` | `String` | Prefijo `USR-` + UUID, generado automáticamente |
| `userName` | `String` | Único, máx. 50 caracteres |
| `firstName` | `String` | Máx. 80 caracteres |
| `lastName` | `String` | Máx. 80 caracteres |
| `email` | `String` | Único, máx. 120 caracteres |
| `passwordHash` | `String` | Hash BCrypt |
| `status` | `String` | Por defecto `ACTIVE` |
| `enabled` | `Boolean` | Por defecto `true` |
| `lastLoginAt` | `LocalDateTime` | Actualizado en cada login |
| `createdAt` | `LocalDateTime` | Auto-asignado en `@PrePersist` |
| `updatedAt` | `LocalDateTime` | Auto-actualizado en `@PreUpdate` |

### RefreshToken

| Campo | Tipo | Notas |
|---|---|---|
| `id` | `String` | Prefijo `RFT-` + UUID |
| `userId` | `String` | FK lógica al usuario |
| `token` | `String` | Único, máx. 255 caracteres |
| `expiresAt` | `LocalDateTime` | Fecha de expiración |
| `revoked` | `Boolean` | Por defecto `false` |
| `revokedAt` | `LocalDateTime` | Fecha de revocación |
| `createdAt` | `LocalDateTime` | Auto-asignado |

### AuthAuditLog

| Campo | Tipo | Notas |
|---|---|---|
| `id` | `String` | Prefijo `AUD-` + UUID |
| `userId` | `String` | Puede ser `null` (eventos anónimos) |
| `eventType` | `String` | Ej. `LOGIN`, `LOGOUT`, `REGISTER`, máx. 50 |
| `description` | `String` | Máx. 255 caracteres |
| `ipAddress` | `String` | Máx. 100 caracteres |
| `createdAt` | `LocalDateTime` | Auto-asignado |

---

## DTOs de referencia

### `AuthResponseDTO` (respuesta de autenticación)

```json
{
  "accessToken":  "eyJhbGci...",
  "refreshToken": "eyJhbGci...",
  "tokenType":    "Bearer",
  "expiresIn":    3600,
  "user": {
    "id":          "USR-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "userName":    "jdoe",
    "firstName":   "John",
    "lastName":    "Doe",
    "email":       "jdoe@example.com",
    "status":      "ACTIVE",
    "enabled":     true,
    "lastLoginAt": "2025-04-23T17:00:00",
    "createdAt":   "2025-01-01T10:00:00",
    "updatedAt":   "2025-04-23T17:00:00"
  }
}
```

### `UserResponseDTO`

```json
{
  "id":          "USR-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "userName":    "jdoe",
  "firstName":   "John",
  "lastName":    "Doe",
  "email":       "jdoe@example.com",
  "status":      "ACTIVE",
  "enabled":     true,
  "lastLoginAt": "2025-04-23T17:00:00",
  "createdAt":   "2025-01-01T10:00:00",
  "updatedAt":   "2025-04-23T17:00:00"
}
```

### `RefreshTokenResponseDTO`

```json
{
  "id":        "RFT-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "userId":    "USR-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "token":     "eyJhbGci...",
  "expiresAt": "2025-04-30T17:00:00",
  "revoked":   false,
  "revokedAt": null,
  "createdAt": "2025-04-23T17:00:00",
  "expired":   false
}
```

### `AuthAuditLogResponseDTO`

```json
{
  "id":          "AUD-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "userId":      "USR-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "eventType":   "LOGIN",
  "description": "Inicio de sesión exitoso",
  "ipAddress":   "192.168.1.1",
  "createdAt":   "2025-04-23T17:00:00"
}
```

---

## API Reference

> **Base URL:** `http://localhost:8080`
>
> Los endpoints marcados con 🔒 requieren el header `Authorization: Bearer <accessToken>`.

---

### Auth — `/api/auth`

#### `POST /api/auth/register`

Registra un nuevo usuario y devuelve tokens de acceso y refresco.

**Request body**

```json
{
  "userName":  "jdoe",
  "firstName": "John",
  "lastName":  "Doe",
  "email":     "jdoe@example.com",
  "password":  "secreto123"
}
```

| Campo | Tipo | Requerido | Restricciones |
|---|---|---|---|
| `userName` | `string` | ✅ | Máx. 50 caracteres |
| `firstName` | `string` | ✅ | Máx. 80 caracteres |
| `lastName` | `string` | ✅ | Máx. 80 caracteres |
| `email` | `string` | ✅ | Formato email válido, máx. 120 |
| `password` | `string` | ✅ | Entre 6 y 100 caracteres |

**Respuesta exitosa:** `201 Created` → [`AuthResponseDTO`](#authresponsedto-respuesta-de-autenticación)

---

#### `POST /api/auth/login`

Autentica a un usuario existente con su identificador (username o email) y contraseña.

**Request body**

```json
{
  "identifier": "jdoe",
  "password":   "secreto123"
}
```

| Campo | Tipo | Requerido | Restricciones |
|---|---|---|---|
| `identifier` | `string` | ✅ | Username o email, máx. 120 caracteres |
| `password` | `string` | ✅ | Máx. 100 caracteres |

> La IP del cliente se captura automáticamente del request para su registro en auditoría.

**Respuesta exitosa:** `200 OK` → [`AuthResponseDTO`](#authresponsedto-respuesta-de-autenticación)

---

#### `POST /api/auth/refresh-token`

Emite un nuevo access token a partir de un refresh token válido.

**Request body**

```json
{
  "refreshToken": "eyJhbGci..."
}
```

| Campo | Tipo | Requerido | Restricciones |
|---|---|---|---|
| `refreshToken` | `string` | ✅ | Máx. 500 caracteres |

**Respuesta exitosa:** `200 OK` → [`AuthResponseDTO`](#authresponsedto-respuesta-de-autenticación)

---

#### `POST /api/auth/logout`

Invalida (revoca) el refresh token del usuario, cerrando la sesión.

**Request body**

```json
{
  "refreshToken": "eyJhbGci..."
}
```

| Campo | Tipo | Requerido | Restricciones |
|---|---|---|---|
| `refreshToken` | `string` | ✅ | Máx. 500 caracteres |

**Respuesta exitosa:** `204 No Content`

---

#### `GET /api/auth/me` 🔒

Devuelve el perfil del usuario autenticado a partir del access token.

**Headers**

```
Authorization: Bearer <accessToken>
```

**Respuesta exitosa:** `200 OK` → [`UserResponseDTO`](#userresponsedto)

---

### Users — `/api/users`

#### `POST /api/users/register`

Crea un nuevo usuario directamente (sin emitir tokens).

**Request body**

```json
{
  "userName":  "jdoe",
  "firstName": "John",
  "lastName":  "Doe",
  "email":     "jdoe@example.com",
  "password":  "secreto123"
}
```

| Campo | Tipo | Requerido | Restricciones |
|---|---|---|---|
| `userName` | `string` | ✅ | Máx. 50 caracteres |
| `firstName` | `string` | ✅ | Máx. 80 caracteres |
| `lastName` | `string` | ✅ | Máx. 80 caracteres |
| `email` | `string` | ✅ | Formato email válido, máx. 120 |
| `password` | `string` | ✅ | Entre 6 y 100 caracteres |

**Respuesta exitosa:** `201 Created` → [`UserResponseDTO`](#userresponsedto)

---

#### `GET /api/users`

Retorna la lista completa de usuarios.

**Respuesta exitosa:** `200 OK` → `UserResponseDTO[]`

---

#### `GET /api/users/{id}`

Obtiene un usuario por su ID.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `string` | ID del usuario (ej. `USR-...`) |

**Respuesta exitosa:** `200 OK` → [`UserResponseDTO`](#userresponsedto)

---

#### `GET /api/users/username/{userName}`

Obtiene un usuario por su nombre de usuario.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `userName` | `string` | Username del usuario |

**Respuesta exitosa:** `200 OK` → [`UserResponseDTO`](#userresponsedto)

---

#### `PUT /api/users/{id}`

Actualiza los datos de un usuario. Todos los campos son opcionales.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `string` | ID del usuario |

**Request body**

```json
{
  "userName":  "nuevo_usuario",
  "firstName": "Jane",
  "lastName":  "Doe",
  "email":     "jane@example.com",
  "password":  "nueva_clave123",
  "status":    "ACTIVE",
  "enabled":   true
}
```

| Campo | Tipo | Requerido | Restricciones |
|---|---|---|---|
| `userName` | `string` | ❌ | Máx. 50 caracteres |
| `firstName` | `string` | ❌ | Máx. 80 caracteres |
| `lastName` | `string` | ❌ | Máx. 80 caracteres |
| `email` | `string` | ❌ | Formato email válido, máx. 120 |
| `password` | `string` | ❌ | Entre 6 y 100 caracteres |
| `status` | `string` | ❌ | Ej. `ACTIVE`, `INACTIVE` |
| `enabled` | `boolean` | ❌ | `true` / `false` |

**Respuesta exitosa:** `200 OK` → [`UserResponseDTO`](#userresponsedto)

---

#### `DELETE /api/users/{id}`

Elimina un usuario por su ID.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `string` | ID del usuario |

**Respuesta exitosa:** `204 No Content`

---

### Refresh Tokens — `/api/refresh-tokens`

#### `POST /api/refresh-tokens`

Crea un refresh token para un usuario dado.

**Request body**

```json
{
  "userId": "USR-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
}
```

| Campo | Tipo | Requerido | Restricciones |
|---|---|---|---|
| `userId` | `string` | ✅ | Máx. 50 caracteres |

**Respuesta exitosa:** `201 Created` → [`RefreshTokenResponseDTO`](#refreshtokenresponsedto)

---

#### `GET /api/refresh-tokens`

Lista todos los refresh tokens del sistema.

**Respuesta exitosa:** `200 OK` → `RefreshTokenResponseDTO[]`

---

#### `GET /api/refresh-tokens/{id}`

Obtiene un refresh token por su ID.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `string` | ID del refresh token (ej. `RFT-...`) |

**Respuesta exitosa:** `200 OK` → [`RefreshTokenResponseDTO`](#refreshtokenresponsedto)

---

#### `GET /api/refresh-tokens/user/{userId}`

Lista todos los refresh tokens asociados a un usuario.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `userId` | `string` | ID del usuario |

**Respuesta exitosa:** `200 OK` → `RefreshTokenResponseDTO[]`

---

#### `PATCH /api/refresh-tokens/{id}/revoke`

Revoca un refresh token específico.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `string` | ID del refresh token |

**Respuesta exitosa:** `200 OK` → [`RefreshTokenResponseDTO`](#refreshtokenresponsedto) (con `revoked: true`)

---

#### `PATCH /api/refresh-tokens/user/{userId}/revoke-all`

Revoca todos los refresh tokens activos de un usuario.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `userId` | `string` | ID del usuario |

**Respuesta exitosa:** `204 No Content`

---

#### `DELETE /api/refresh-tokens/{id}`

Elimina un refresh token por su ID.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `string` | ID del refresh token |

**Respuesta exitosa:** `204 No Content`

---

#### `DELETE /api/refresh-tokens/expired`

Elimina todos los refresh tokens expirados del sistema.

**Respuesta exitosa:** `204 No Content`

---

### Audit Logs — `/api/auth-audit-logs`

#### `POST /api/auth-audit-logs`

Registra manualmente un evento de auditoría.

**Request body**

```json
{
  "userId":      "USR-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "eventType":   "LOGIN",
  "description": "Inicio de sesión exitoso",
  "ipAddress":   "192.168.1.1"
}
```

| Campo | Tipo | Requerido | Restricciones |
|---|---|---|---|
| `userId` | `string` | ❌ | Máx. 50 caracteres |
| `eventType` | `string` | ✅ | Máx. 50 caracteres |
| `description` | `string` | ❌ | Máx. 255 caracteres |
| `ipAddress` | `string` | ❌ | Máx. 100 caracteres |

**Respuesta exitosa:** `201 Created` → [`AuthAuditLogResponseDTO`](#authauditlogresponsedto)

---

#### `GET /api/auth-audit-logs`

Lista todos los registros de auditoría.

**Respuesta exitosa:** `200 OK` → `AuthAuditLogResponseDTO[]`

---

#### `GET /api/auth-audit-logs/{id}`

Obtiene un registro de auditoría por su ID.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `string` | ID del log (ej. `AUD-...`) |

**Respuesta exitosa:** `200 OK` → [`AuthAuditLogResponseDTO`](#authauditlogresponsedto)

---

#### `GET /api/auth-audit-logs/user/{userId}`

Lista todos los logs de auditoría de un usuario.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `userId` | `string` | ID del usuario |

**Respuesta exitosa:** `200 OK` → `AuthAuditLogResponseDTO[]`

---

#### `GET /api/auth-audit-logs/event/{eventType}`

Filtra logs de auditoría por tipo de evento.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `eventType` | `string` | Ej. `LOGIN`, `LOGOUT`, `REGISTER` |

**Respuesta exitosa:** `200 OK` → `AuthAuditLogResponseDTO[]`

---

#### `GET /api/auth-audit-logs/range`

Filtra logs de auditoría en un rango de fechas.

**Query params**

| Parámetro | Tipo | Formato | Descripción |
|---|---|---|---|
| `start` | `string` | ISO 8601 (`yyyy-MM-ddTHH:mm:ss`) | Fecha/hora de inicio |
| `end` | `string` | ISO 8601 (`yyyy-MM-ddTHH:mm:ss`) | Fecha/hora de fin |

**Ejemplo:**

```
GET /api/auth-audit-logs/range?start=2025-04-01T00:00:00&end=2025-04-23T23:59:59
```

**Respuesta exitosa:** `200 OK` → `AuthAuditLogResponseDTO[]`

---

#### `DELETE /api/auth-audit-logs/{id}`

Elimina un registro de auditoría por su ID.

**Path params**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `string` | ID del log de auditoría |

**Respuesta exitosa:** `204 No Content`

---

## Swagger / OpenAPI

El proyecto incluye **springdoc-openapi 3.0.2**. Una vez levantado el servidor, accede a la documentación interactiva en:

```
http://localhost:8080/swagger-ui.html
```

El JSON de la especificación OpenAPI estará disponible en:

```
http://localhost:8080/v3/api-docs
```
