# ms_recursos_innovatech

Microservicio para administrar recursos humanos de Innovatech Solutions.

## Requisitos
- Java 17
- Maven 3.9+
- MySQL

## Ejecucion
1. Configura la conexion a la base de datos en `application.properties` (no incluido aqui).
2. Ejecuta el proyecto con Maven o desde tu IDE.

Comando sugerido:

```bash
mvn spring-boot:run
```

## Swagger
Una vez levantado el servicio, accede a:

- `http://localhost:8080/swagger-ui/index.html`

## Endpoints principales
Base URL: `http://localhost:8080`

### Profesionales
- `GET /api/professionals`
- `GET /api/professionals/{id}`
- `POST /api/professionals`
- `PUT /api/professionals/{id}`
- `DELETE /api/professionals/{id}`
- `GET /api/professionals/status/{status}`
- `GET /api/professionals/role/{roleName}`
- `GET /api/professionals/seniority/{seniority}`

### Habilidades
- `GET /api/skills`
- `GET /api/skills/{id}`
- `POST /api/skills`
- `PUT /api/skills/{id}`
- `DELETE /api/skills/{id}`
- `GET /api/skills/status/{status}`
- `GET /api/skills/category/{category}`

### Habilidades asociadas
- `GET /api/resource-skills`
- `GET /api/resource-skills/{id}`
- `POST /api/resource-skills`
- `PUT /api/resource-skills/{id}`
- `DELETE /api/resource-skills/{id}`
- `GET /api/resource-skills/resource/{resourceId}`
- `GET /api/resource-skills/skill/{skillId}`

### Disponibilidad
- `GET /api/availability`
- `GET /api/availability/{id}`
- `POST /api/availability`
- `PUT /api/availability/{id}`
- `DELETE /api/availability/{id}`
- `GET /api/availability/resource/{resourceId}`
- `GET /api/availability/type/{availabilityType}`

### Asignaciones
- `GET /api/assignments`
- `GET /api/assignments/{id}`
- `POST /api/assignments`
- `PUT /api/assignments/{id}`
- `DELETE /api/assignments/{id}`
- `GET /api/assignments/resource/{resourceId}`
- `GET /api/assignments/project/{projectId}`
- `GET /api/assignments/status/{assignmentStatus}`

### Ausencias
- `GET /api/absences`
- `GET /api/absences/{id}`
- `POST /api/absences`
- `PUT /api/absences/{id}`
- `DELETE /api/absences/{id}`
- `GET /api/absences/resource/{resourceId}`
- `GET /api/absences/type/{absenceType}`
