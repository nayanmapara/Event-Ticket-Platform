# Local development

## Prerequisites

- JDK 21 (`java -version`)
- Docker with Compose v2
- Node.js and npm compatible with the checked-in lockfile
- Git
- Optional: Terraform >= 1.3 and Azure CLI for infrastructure work

Local ports used by the checked-in configuration:

| Service | Port |
| --- | --- |
| PostgreSQL | `5432` |
| Spring Boot API | `8080` |
| Adminer | `8888` |
| Keycloak | `9090` |
| Vite | normally `5173` |

## 1. Start dependencies

From `backend/tickets`:

```bash
docker compose up -d
docker compose ps
```

Compose starts PostgreSQL, Adminer, and a development Keycloak instance. PostgreSQL uses database/user `postgres` and the development password from `application-dev.properties`. These credentials are intentionally local-only.

Keycloak data is persisted in the named `keycloak-data` volume. PostgreSQL has no named volume in the current compose file, so do not assume local database data survives container recreation.

## 2. Configure Keycloak

Open `http://localhost:9090` and sign in to the administration console with the Compose development admin credentials.

The repository does not contain a realm export. Create the following manually:

1. Realm: `event-ticket-platform`
2. Realm roles: `ROLE_ATTENDEE`, `ROLE_ORGANIZER`, `ROLE_STAFF`
3. OIDC client: `event-ticket-platform-app`
4. Configure the client as a public browser client using the authorization-code flow.
5. Add local redirect URI such as `http://localhost:5173/callback`.
6. Add the local frontend origin, such as `http://localhost:5173`, as an allowed web origin.
7. Create test users, set non-temporary passwords, and assign realm roles.
8. Ensure access tokens contain `preferred_username`, `email`, and `realm_access.roles`. The token subject must be a UUID (Keycloak's normal user ID format).

The production/demo account names displayed by the UI are not provisioned by Compose.

## 3. Start the backend

The default active profile is `dev`.

```bash
cd backend/tickets
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
cd backend/tickets
./mvnw.cmd spring-boot:run
```

The dev profile connects to `jdbc:postgresql://localhost:5432/postgres`, uses Hibernate schema update, and validates tokens from `http://localhost:9090/realms/event-ticket-platform`.

Useful overrides can be supplied with standard Spring environment variables, for example `SPRING_PROFILES_ACTIVE`, `SPRING_DATASOURCE_URL`, and `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI`.

## 4. Start the frontend

```bash
cd frontend
npm ci
npm run dev
```

Configuration caveat: `src/lib/api.ts` currently uses a hard-coded production Render URL and `src/main.tsx` uses a hard-coded Azure Keycloak authority. The Vite `/api` proxy and Terraform `VITE_BACKEND_URL` setting do not change those constants. A local end-to-end session therefore requires a temporary source change or, preferably, a code change that reads Vite environment variables.

Recommended future contract:

```dotenv
VITE_API_BASE_URL=http://localhost:8080
VITE_OIDC_AUTHORITY=http://localhost:9090/realms/event-ticket-platform
VITE_OIDC_CLIENT_ID=event-ticket-platform-app
```

No `.env.example` currently exists, so these names are recommendations rather than implemented configuration.

## Commands and checks

Backend:

```bash
./mvnw test
./mvnw clean package
```

Frontend:

```bash
npm run lint
npm run build
npm run format
```

`npm run format` writes files. Use it deliberately. `npm run mocks` starts `json-server` from `frontend/db.json`, but the live API module is not configured to call it.

## Troubleshooting

### Issuer or JWT validation errors

- Confirm Keycloak is available at the exact issuer URL in the backend profile.
- The token's `iss` claim must exactly match that URL, including scheme, port, realm, and hostname.
- Confirm realm roles begin with `ROLE_` and appear under `realm_access.roles`.

### Login redirect loop

- Register the exact frontend origin plus `/callback` in the Keycloak client.
- Clear stale browser OIDC/local-storage state after changing realm/client settings.
- Confirm the frontend authority points at the Keycloak instance you configured.

### CORS failure

Localhost is not included in the backend's checked-in allowed origins. Update both backend CORS definitions or consolidate them into one environment-driven configuration for local use.

### Database connection failure

- Run `docker compose ps` and check PostgreSQL logs.
- Confirm port `5432` is free.
- Match the Spring URL/user/password to Compose.

### Event search fails in tests

Published-event search uses PostgreSQL `to_tsvector`/`plainto_tsquery`. H2 cannot execute that native query unchanged.

### Empty or erroring pagination

Frontend page sizes are fixed in `src/lib/api.ts` (events: 2, public events: 4, tickets: 8). The page index is zero-based.
