# Event Ticket Platform

A full-stack event ticketing application for publishing events, purchasing tickets, issuing QR codes, and validating admission. The repository contains a React frontend, a Spring Boot API, local PostgreSQL/Keycloak services, and Terraform for the original Azure deployment.

## What is implemented

- Public event browsing, full-text search, and event detail pages
- Keycloak OpenID Connect login
- Organizer event creation, editing, publication, and deletion
- Ticket types with price, description, and inventory limits
- Authenticated ticket purchase with pessimistic inventory locking
- Purchaser ticket list, ticket detail, and PNG QR code retrieval
- Staff validation by QR scan or manual ticket ID
- Automatic local user provisioning from JWT claims
- PostgreSQL persistence with JPA auditing

Payment is intentionally mocked: the purchase page does not process or store payment-card data.

## Repository layout

| Path | Purpose |
| --- | --- |
| `frontend/` | React 19, TypeScript, Vite, Tailwind CSS, and Radix/shadcn UI |
| `backend/tickets/` | Java 21 and Spring Boot 3 REST API |
| `keycloak/` | Production-oriented Keycloak container image |
| `terraform/` | Azure App Service, Static Web Apps, and PostgreSQL resources |
| `docs/` | Architecture, API, development, deployment, and data-model documentation |

## Quick start

Prerequisites: Java 21, Docker, Node.js/npm, and Maven (or the included Maven wrapper).

1. Start PostgreSQL and Keycloak:

   ```bash
   cd backend/tickets
   docker compose up -d
   ```

2. Configure Keycloak as described in [`docs/guides/local-development.md`](docs/guides/local-development.md), then start the API:

   ```bash
   cd backend/tickets
   ./mvnw spring-boot:run
   ```

   On Windows PowerShell, use `./mvnw.cmd spring-boot:run`.

3. Start the frontend:

   ```bash
   cd frontend
   npm ci
   npm run dev
   ```

The API uses the `dev` Spring profile by default. Important: the frontend currently calls the production Render API directly; see the configuration warning in [`docs/guides/local-development.md`](docs/guides/local-development.md) before testing local end-to-end changes.

## Documentation

- [`docs/README.md`](docs/README.md) - documentation index and current-state notes
- [`docs/architecture/README.md`](docs/architecture/README.md) - components, request flows, authorization, and runtime topology
- [`docs/architecture/diagrams.md`](docs/architecture/diagrams.md) - consolidated Mermaid diagram gallery
- [`docs/reference/api.md`](docs/reference/api.md) - implemented REST API contract
- [`docs/reference/data-model.md`](docs/reference/data-model.md) - entities, relationships, states, and lifecycle rules
- [`docs/guides/local-development.md`](docs/guides/local-development.md) - local setup, Keycloak configuration, commands, and troubleshooting
- [`docs/guides/testing.md`](docs/guides/testing.md) - test inventory, commands, conventions, warnings, and recommended coverage
- [`docs/operations/deployment.md`](docs/operations/deployment.md) - containers, production variables, Terraform, and known infrastructure gaps
- [`docs/history/erd-evolution.md`](docs/history/erd-evolution.md) - historical domain-model evolution

## Verification

```bash
# backend
cd backend/tickets
./mvnw test

# frontend
cd frontend
npm run lint
npm run build

# infrastructure
cd terraform
terraform fmt -check -recursive
terraform validate
```

## Current limitations

- Backend coverage currently includes service unit tests for ticket purchase, purchaser scoping, and admission validation plus a Spring context smoke test. Controller, repository integration, concurrency, and frontend tests are still missing.
- Frontend API and Keycloak URLs are hard-coded in source, despite Terraform exposing a `VITE_BACKEND_URL` setting.
- CORS origins are hard-coded in two backend configuration classes.
- The repository does not include a Keycloak realm export, so realm/client/role setup is manual.
- Terraform mixes Java 17 runtime settings with a Java 21 Maven build and includes inputs that duplicate module outputs.
- Database schema changes use `hibernate.ddl-auto=update`; there are no Flyway/Liquibase migrations.
- The PostgreSQL Terraform firewall currently permits all IPv4 addresses and should be restricted before production use.

See the linked documents for precise behavior and remediation notes.

## License

See [`LICENSE`](LICENSE).
