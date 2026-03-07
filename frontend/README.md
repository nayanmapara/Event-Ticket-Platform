# Event Ticket Platform frontend

React 19 and TypeScript single-page application built with Vite. Authentication uses `react-oidc-context`; the UI uses Tailwind CSS 4, Radix primitives, shadcn-style components, and Lucide icons.

## Commands

```bash
npm ci          # install the lockfile exactly
npm run dev     # Vite development server
npm run build   # TypeScript project build and production bundle
npm run lint    # ESLint
npm run format  # Prettier (writes changes)
npm run preview # serve the production bundle locally
npm run mocks   # json-server using db.json (not wired into api.ts)
```

## Application routes

| Route | Authentication | Purpose |
| --- | --- | --- |
| `/` | Public | Search and browse published events |
| `/events/:id` | Public | Published event details and ticket types |
| `/events/:eventId/purchase/:ticketTypeId` | Required | Mock checkout and ticket creation |
| `/organizers` | Public | Organizer landing page |
| `/login` and `/callback` | OIDC flow | Login redirect and return handling |
| `/dashboard` | Required | Redirect based on JWT realm role |
| `/dashboard/events` | Required; UI expects organizer | Organizer event list |
| `/dashboard/events/create` | Required; UI expects organizer | Event creation |
| `/dashboard/events/update/:id` | Required; UI expects organizer | Event editing |
| `/dashboard/tickets` | Required | Current user's tickets |
| `/dashboard/tickets/:id` | Required | Ticket details and QR image |
| `/dashboard/validate-qr` | Required; API requires staff | QR/manual admission validation |

`ProtectedRoute` checks authentication only. Role-aware navigation and dashboard redirection are convenience UI behavior; the backend must remain the authorization boundary.

## Configuration

OIDC authority/client settings are currently defined in `src/main.tsx`. The API base URL is currently defined in `src/lib/api.ts`. Vite's `/api` proxy is therefore not used by the API module.

For local work, either change those constants temporarily or refactor them to Vite variables such as `VITE_API_BASE_URL`, `VITE_OIDC_AUTHORITY`, and `VITE_OIDC_CLIENT_ID`. Do not commit local-only URLs.

The callback URI is computed as `${window.location.origin}/callback`. Each frontend origin must therefore be registered as a valid redirect URI and web origin on the Keycloak client.

## Key implementation details

- `src/domain/domain.ts` mirrors backend DTOs and Spring's page envelope.
- `src/lib/api.ts` owns all HTTP calls and attaches bearer tokens to protected routes.
- `src/hooks/use-roles.tsx` reads `realm_access.roles` and recognizes `ROLE_ORGANIZER`, `ROLE_ATTENDEE`, and `ROLE_STAFF`.
- `src/pages/dashboard-manage-event-page.tsx` handles both create and update forms and converts browser dates into UTC-shaped JSON values.
- Ticket checkout is a demo: entered card fields are neither validated nor transmitted.

## Testing gaps

There is no frontend unit, component, or end-to-end test suite. High-value additions are API error handling, auth redirects, role routing, event form date conversion, ticket purchase, and QR validation flows.
