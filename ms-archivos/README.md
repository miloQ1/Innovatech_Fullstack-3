# MS Colaboracion Innovatech

Microservicio de colaboracion para proyectos y tareas. Provee hilos de conversacion, comentarios, menciones, actividad y adjuntos.

## Requisitos
- Java 17
- Maven 3.9+
- MySQL 8+

## Configuracion
La configuracion de base de datos esta en application.properties.

## Ejecutar
- Windows: mvnw.cmd spring-boot:run
- Unix: ./mvnw spring-boot:run

## Swagger
- UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI: http://localhost:8080/v3/api-docs

## Endpoints
Base URL: http://localhost:8080

### Threads
- GET /api/threads
- GET /api/threads/{id}
- POST /api/threads
- PUT /api/threads/{id}
- DELETE /api/threads/{id}
- GET /api/threads/project/{projectId}

### Comments
- GET /api/comments
- GET /api/comments/{id}
- POST /api/comments
- PUT /api/comments/{id}
- DELETE /api/comments/{id}
- GET /api/comments/thread/{threadId}

### Mentions
- GET /api/mentions
- GET /api/mentions/{id}
- POST /api/mentions
- PUT /api/mentions/{id}
- DELETE /api/mentions/{id}
- GET /api/mentions/resource/{resourceId}

### Activity Logs
- GET /api/activity-logs
- GET /api/activity-logs/{id}
- POST /api/activity-logs
- PUT /api/activity-logs/{id}
- DELETE /api/activity-logs/{id}
- GET /api/activity-logs/project/{projectId}

### Attachments
- GET /api/attachments
- GET /api/attachments/{id}
- POST /api/attachments
- PUT /api/attachments/{id}
- DELETE /api/attachments/{id}
- GET /api/attachments/comment/{commentId}

## Pruebas en Postman
Sugerencia: crear un Environment con estas variables (numericas, sin comillas):
- baseUrl = http://localhost:8080
- projectId = 1
- taskId = 1
- authorResourceId = 10
- actorResourceId = 10
- mentionedResourceId = 20

Nota: los IDs principales son autoincrementales y empiezan en 1.

### 1) Crear thread
POST {{baseUrl}}/api/threads
Body (JSON):
{
  "projectId": {{projectId}},
  "taskId": {{taskId}},
  "title": "Diseno inicial",
  "status": "OPEN",
  "createdByResourceId": {{authorResourceId}}
}

Guarda el threadId de la respuesta en una variable (por ejemplo, threadId).

### 2) Crear comment
POST {{baseUrl}}/api/comments
Body (JSON):
{
  "threadId": {{threadId}},
  "authorResourceId": {{authorResourceId}},
  "content": "Primer comentario"
}

Guarda el commentId de la respuesta en una variable (por ejemplo, commentId).

### 3) Crear mention
POST {{baseUrl}}/api/mentions
Body (JSON):
{
  "commentId": {{commentId}},
  "mentionedResourceId": {{mentionedResourceId}},
  "mentionStatus": "PENDING"
}

### 4) Crear attachment
POST {{baseUrl}}/api/attachments
Body (JSON):
{
  "commentId": {{commentId}},
  "fileName": "diagrama.png",
  "fileUrl": "https://example.com/diagrama.png",
  "mimeType": "image/png",
  "sizeBytes": 12345
}

### 5) Crear activity log
POST {{baseUrl}}/api/activity-logs
Body (JSON):
{
  "projectId": {{projectId}},
  "taskId": {{taskId}},
  "actorResourceId": {{actorResourceId}},
  "actionType": "COMMENT_CREATED",
  "description": "Se creo un comentario"
}

### 6) Consultas rapidas
- GET {{baseUrl}}/api/threads/project/{{projectId}}
- GET {{baseUrl}}/api/comments/thread/{{threadId}}
- GET {{baseUrl}}/api/mentions/resource/{{mentionedResourceId}}
- GET {{baseUrl}}/api/attachments/comment/{{commentId}}
- GET {{baseUrl}}/api/activity-logs/project/{{projectId}}

Si no usas variables, puedes probar con valores directos, por ejemplo:
- GET {{baseUrl}}/api/threads/1
- GET {{baseUrl}}/api/comments/thread/1
