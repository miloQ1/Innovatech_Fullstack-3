# Correcciones finales aplicadas

## Frontend web + Android

- La URL base del backend quedó centralizada en `frontend-ionic-capacitor/src/config/backend.ts`.
- Web usa `http://localhost:8090`.
- Android emulador usa `http://10.0.2.2:8090`.
- Se agregó `frontend-ionic-capacitor/.env.example` para configurar Android en emulador o celular físico.
- `apiClient.ts` ahora tiene timeout de 15 segundos para evitar cargas infinitas cuando el Gateway no responde.
- Se validó TypeScript con `npx tsc -b --pretty false`.

## CORS

- El CORS queda centralizado en `bffGateway/src/main/resources/application.properties`.
- Se agregó `DedupeResponseHeader` para evitar el error de headers duplicados:
  `Access-Control-Allow-Origin: http://localhost:8100, http://localhost:8100`.
- Se desactivó CORS propio en:
  - `authService-Innovatech`
  - `proyectsService-Innovatech`
  - `ms-clientes`

## Notificaciones privadas

- La página de Notificaciones ya no permite elegir otros profesionales.
- La bandeja usa `/api/notifications/me` para mostrar solo las notificaciones del usuario logeado.
- Las preferencias usan `/api/preferences/me`.
- El BFF ahora inyecta headers desde el JWT: `X-User-Id`, `X-User-Name`, `X-User-Email`, `X-User-First-Name`, `X-User-Last-Name`.
- El microservicio de Notificaciones valida el recurso profesional asociado al correo del usuario logeado antes de permitir consultar `/recipient/{id}` o `/resource/{id}`.

## Registro y Recursos Humanos

- `authService` ya no llama a `http://localhost:8083` de forma fija para crear la ficha profesional.
- Ahora usa `resources.service.url`.
- En Docker se configura como `http://ms-recursos:8083`, por lo que al registrar usuarios también se crea su ficha en Recursos Humanos si el servicio está levantado.

## Comandos recomendados

Desde la raíz del proyecto:

```bash
docker compose down
docker compose up --build
```

Desde `frontend-ionic-capacitor`:

```bash
npm install
npm run build
npx cap sync android
npx cap open android
```

Si usas celular físico, copia `.env.example` a `.env` y cambia `VITE_ANDROID_API_BASE_URL` por la IPv4 de tu PC.
