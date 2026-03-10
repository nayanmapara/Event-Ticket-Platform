# Documentation index

These documents describe the behavior present in the repository, not a future product plan.

| Document | Audience | Contents |
| --- | --- | --- |
| [Project README](../README.md) | Everyone | Capabilities, layout, quick start, and limitations |
| [Architecture](Architecture/architecture.md) | Developers/architects | Components, request paths, auth, domain flows, and runtime topology |
| [API endpoints](api-endpoints.md) | Frontend/API developers | Routes, authorization, request/response shapes, errors, and pagination |
| [Local development](local-development.md) | Contributors | Dependencies, local services, Keycloak setup, commands, and troubleshooting |
| [Data model](data-model.md) | Backend/data developers | JPA entities, relationships, enums, lifecycle rules, and schema caveats |
| [Deployment](deployment.md) | Operators | Images, environment variables, Terraform resources, outputs, and risks |
| [ERD evolution](ERD/erd_evolution.md) | Historical context | How the original domain model was developed |

## Source-of-truth order

When docs and implementation disagree, use this order:

1. Controller and security configuration for HTTP behavior
2. DTO/entity validation for payload and persistence behavior
3. Application properties and Terraform for runtime configuration
4. These documents

Please update the corresponding document in the same change whenever an endpoint, role rule, entity, environment variable, or deployment resource changes.

## Important current-state notes

- The production URLs in `frontend/src/main.tsx` and `frontend/src/lib/api.ts` override the environment-driven setup implied by Terraform.
- `WebConfig` and `SecurityConfig` both define the same hard-coded CORS origins.
- Only public `GET /api/v1/published-events/**` is anonymous. Every other route requires a JWT; additional role checks are limited to the exact `/api/v1/events` matcher and `/api/v1/ticket-validations`.
- Ticket validation records every attempt. A ticket's first successful validation is `VALID`; later validations are `INVALID`.
- Event and ticket date values are serialized as ISO-8601 local date-times without an explicit timezone contract.
