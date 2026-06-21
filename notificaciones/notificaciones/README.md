# Microservicio de Notificaciones - Innovatech

Servicio Spring Boot encargado de procesar eventos de notificación, aplicar preferencias de usuario/recurso, seleccionar plantillas y generar despachos por canal.

## Responsabilidades

- Registrar plantillas por tipo de evento y canal.
- Registrar eventos de notificación generados por otros microservicios.
- Crear despachos por destinatario y canal.
- Respetar preferencias del destinatario.
- Aplicar Factory Method mediante `NotifierFactory` para seleccionar `EmailNotifier`, `InAppNotifier` o `WebhookNotifier`.
- Registrarse en Eureka con el nombre `notificaciones`.

## Puerto

```properties
server.port=8085
spring.application.name=notificaciones
```

## Endpoints principales

### Procesar notificación

```http
POST /api/notifications/send
```

Ejemplo:

```json
{
  "sourceService": "ms-colaboracion",
  "eventType": "MENTION_CREATED",
  "entityId": 15,
  "recipientResourceIds": [1, 2],
  "channels": ["IN_APP", "EMAIL"],
  "payload": {
    "userName": "Eduardo",
    "projectName": "Portal Innovatech",
    "message": "Te mencionaron en una tarea crítica"
  }
}
```

### Listar notificaciones de un recurso

```http
GET /api/notifications/recipient/{recipientResourceId}
```

### CRUD de soporte

- `GET/POST/PUT/DELETE /api/templates`
- `GET/POST/PUT/DELETE /api/events`
- `GET/POST/PUT/DELETE /api/dispatches`
- `GET/POST/PUT/DELETE /api/preferences`
- `GET/POST/PUT/DELETE /api/webhooks`

## Flujo de ejecución

1. Otro microservicio o el BFF envía un evento a `/api/notifications/send`.
2. El servicio valida destinatarios, evento y canales.
3. Busca una plantilla activa para `eventType + channel`; si no existe, crea una plantilla base.
4. Renderiza asunto y cuerpo usando variables `{{variable}}` del payload.
5. Revisa preferencias del destinatario.
6. Usa `NotifierFactory` para seleccionar el notificador concreto.
7. Guarda el despacho con estado `SENT`, `FAILED` o `SKIPPED`.

## Ejecución local

```bash
cd notificaciones/notificaciones
./mvnw spring-boot:run
```

Swagger UI:

```http
http://localhost:8085/swagger-ui.html
```
