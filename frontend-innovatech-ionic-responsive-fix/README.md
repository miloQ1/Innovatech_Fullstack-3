# Frontend Innovatech - Ionic React

Proyecto React transformado a **Ionic React + Vite + Capacitor** y adaptado para trabajar contra los 5 servicios actuales del backend Innovatech.

## Backend usado por el frontend

El frontend consume el backend a través del **BFF Gateway**:

| Servicio | Puerto | Rutas usadas desde Ionic |
|---|---:|---|
| BFF Gateway | 8090 | `/api/**` |
| Auth Service | 8080 | `/api/auth`, `/api/users` |
| Projects Service | 8081 | `/api/clients`, `/api/projects`, `/api/phases`, `/api/tasks` |
| Resources Service | 8083 | `/api/professionals`, `/api/skills`, `/api/assignments`, `/api/availability`, `/api/absences` |
| Collaboration Service | 8084 | `/api/threads`, `/api/comments` |

> Nota: el microservicio de colaboración también expone `/api/activity-logs`, `/api/attachments`, `/api/mentions` y `/api/resource-skills`, pero en el `application.properties` del BFF entregado no aparecen esas rutas completas. El frontend ya dejó servicios preparados para ellas, pero para usarlas por gateway debes agregar esas rutas en el BFF.

## Cambios principales realizados

- Se separaron los servicios frontend por dominio:
  - `src/api/authService.ts`
  - `src/api/projectService.ts`
  - `src/api/resourcesService.ts`
  - `src/api/collaborationService.ts`
- Se agregó `src/config/backend.ts` con puertos y rutas centrales del backend.
- Se ajustó `apiClient` para usar `VITE_API_BASE_URL` cuando corra en móvil/Capacitor.
- Se mantuvo el proxy web de Vite hacia `http://localhost:8090`.
- Se removió la pantalla de prueba `/backend`; el menú queda enfocado en módulos funcionales.
- Se mantuvo compatibilidad con React Router 5.3.4, requerido por `@ionic/react-router`.
- Se mantuvieron las pantallas Ionic ya migradas: login, registro, dashboard, clientes, proyectos, recursos, detalle de proyecto, board de tareas y comentarios.
- Se corrigió la navegación real de Ionic con `IonReactRouter`, `IonSplitPane`, `IonMenu` e `IonRouterOutlet`.
- La app ahora inicia siempre en `/login` y limpia sesiones antiguas guardadas en el navegador para evitar aparecer logeado al abrir.
- Dashboard, menú, navbar, clientes, proyectos y recursos tienen botones funcionales de navegación y CRUD según endpoints disponibles.
- Se agregaron acciones de ver/editar/eliminar en proyectos y profesionales.

## Instalar dependencias

```bash
npm install
```

## Ejecutar en navegador

Primero levanta tus microservicios. El BFF debe estar activo en el puerto `8090`.

```bash
npm run dev
```

También puedes usar:

```bash
npm run ionic:serve
```

## Compilar

```bash
npm run build
```

## Variables de entorno

El archivo `.env.example` incluye la configuración sugerida:

```env
VITE_API_BASE_URL=
VITE_API_PROXY_TARGET=http://localhost:8090
```

Para navegador con Vite, puedes dejar `VITE_API_BASE_URL` vacío y usar el proxy `/api`.

Para Android Emulator con Capacitor, normalmente debes usar:

```env
VITE_API_BASE_URL=http://10.0.2.2:8090
```

## Preparar Android con Capacitor

```bash
npm run build
npm run cap:add:android
npm run cap:sync
```

Luego abre Android Studio desde la carpeta `android` generada.

## Orden recomendado para probar

1. Levantar MySQL.
2. Levantar `authService-Innovatech` en `8080`.
3. Levantar `proyectsService-Innovatech` en `8081`.
4. Levantar `ms_recursos_innovatech` en `8083`.
5. Levantar `MS_Colaboracion_Innovatech` en `8084`.
6. Levantar `bbfGatewayInnovatech` en `8090`.
7. Ejecutar el frontend con `npm run dev`.

## Corrección aplicada: rutas NaN, login y errores 503

Esta versión corrige problemas detectados en consola:

- Se evita que `/clients/create` sea interpretado como `/clients/:id`. Las rutas dinámicas ahora aceptan solo IDs numéricos.
- Se validan los IDs antes de llamar al backend para impedir llamadas como `/api/clients/NaN` o `/api/projects/client/NaN`.
- Se agregaron helpers para aceptar respuestas del backend con `id`, `clientId`, `projectId`, `resourceId` o `professionalId`.
- Las pantallas de cliente, proyecto, dashboard y proyectos ya no se rompen cuando una ruta secundaria del BFF responde `503`.
- El login intenta compatibilidad con distintos nombres de campo comunes: `identifier`, `usernameOrEmail`, `userNameOrEmail`, `login`, `email` o `userName`.
- La pestaña Backend no existe en esta versión.

### Sobre las URLs `http://localhost:5173/api/...`

Eso es normal en desarrollo: Vite recibe `/api/...` en el puerto `5173` y lo proxyea hacia el BFF en `http://localhost:8090` según `vite.config.ts`.

Si aparece `503 Service Unavailable`, el frontend sí está llamando, pero el BFF no puede llegar al microservicio correspondiente. Revisa que estén levantados:

```bash
# Auth
http://localhost:8080

# Proyectos / clientes / fases / tareas
http://localhost:8081

# Recursos
http://localhost:8083

# Colaboración
http://localhost:8084

# BFF Gateway
http://localhost:8090
```

