# Correcciones finales - perfiles, notificaciones, puertos y CORS

## Cuenta administradora inicial

Al levantar `auth-service` se crea automáticamente una cuenta administradora si no existe:

- Usuario: `admin`
- Email: `admin@innovatech.cl`
- Contraseña: `Admin12345`
- Rol: `ADMIN`

También se crea una ficha profesional inicial en `ms-recursos` vinculada al admin con `employeeCode = USR-ADMIN-0001`.

## Edición de perfiles profesionales

- Un usuario normal puede editar únicamente su propia ficha profesional.
- La vinculación se realiza con `employeeCode = X-User-Id`.
- El admin puede crear, editar y eliminar cualquier ficha profesional.
- El endpoint `/api/professionals/me` permite obtener, crear si falta, y actualizar la ficha del usuario autenticado.

## Notificaciones privadas

- La página de notificaciones ya no permite elegir otros profesionales.
- Cada usuario ve únicamente su propia bandeja mediante `/api/notifications/me`.
- Las preferencias personales se administran mediante `/api/preferences/me`.
- Al abrir la bandeja por primera vez se asegura una notificación de bienvenida `WELCOME`.
- Al registrarse un usuario, `auth-service` intenta crear su ficha profesional y generar una notificación de bienvenida.
- Las asignaciones, tareas/proyectos y colaboración publican notificaciones automáticas hacia el microservicio `notificaciones`.

## Puertos y URLs

Frontend web:

```env
VITE_API_BASE_URL=http://localhost:8090
```

Android emulador:

```env
VITE_ANDROID_API_BASE_URL=http://10.0.2.2:8090
```

Android físico con la red actual indicada:

```env
VITE_ANDROID_API_BASE_URL=http://192.168.1.9:8090
```

Archivos de referencia:

- `frontend-ionic-capacitor/.env.example`
- `frontend-ionic-capacitor/.env.android-fisico.example`

## CORS

El CORS queda centralizado en `bffGateway`. Los microservicios que tenían Spring Security ahora desactivan CORS local para evitar errores como:

```text
Access-Control-Allow-Origin contains multiple values
```

El Gateway también aplica `DedupeResponseHeader` para limpiar headers duplicados.

## Ejecución recomendada

Backend:

```bash
docker compose down -v
docker compose up --build
```

Frontend web:

```bash
cd frontend-ionic-capacitor
npm install
npm run dev
```

Android:

```bash
cd frontend-ionic-capacitor
npm install
npm run build
npx cap sync android
npx cap open android
```
