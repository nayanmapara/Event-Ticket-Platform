# REST API reference

Base path: `/api/v1`. JSON is used unless an endpoint says otherwise. UUID path and body values use the standard hyphenated UUID format.

## Authentication and roles

Protected endpoints require:

```http
Authorization: Bearer <Keycloak access token>
```

The backend reads realm roles from the JWT claim `realm_access.roles`. Only roles whose names begin with `ROLE_` are converted to Spring authorities.

| Access | Implemented security rule |
| --- | --- |
| Anonymous | `GET /published-events` and `GET /published-events/{eventId}` |
| Organizer | Exact matcher `/events` requires `ROLE_ORGANIZER` |
| Staff | Exact matcher `/ticket-validations` requires `ROLE_STAFF` |
| Authenticated | All remaining routes |

Warning: path matchers currently leave `/events/{eventId}` and `/events/{eventId}/ticket-types/...` under the generic authenticated rule. Treat this as a security gap to fix, not as the intended product permission model.

On the first authenticated request, `UserProvisioningFilter` creates a local user using the JWT subject as the UUID and the `preferred_username` and `email` claims.

## Pagination

List endpoints accept Spring Data pagination parameters:

| Parameter | Meaning |
| --- | --- |
| `page` | Zero-based page index |
| `size` | Requested page size |
| `sort` | Optional Spring sort expression, for example `name,asc` |

Responses use Spring's `Page` JSON envelope, including `content`, `number`, `size`, `totalElements`, `totalPages`, `first`, `last`, `sort`, and `pageable`.

## Error format

Handled application and validation failures return:

```json
{ "error": "Human-readable message" }
```

Most domain/validation errors return `400`. QR retrieval/generation failures return `500`. Missing organizer-owned or purchaser-owned resources returned through controller optionals use `404` with no body. Authentication and authorization failures are handled by Spring Security.

## Events owned by the organizer

### `POST /events`

Creates an event and its initial ticket types. Requires a JWT and, under the current exact matcher, `ROLE_ORGANIZER`.

```json
{
  "name": "Summer Festival",
  "start": "2026-08-15T18:00:00",
  "end": "2026-08-15T23:00:00",
  "venue": "Harbour Stage",
  "salesStart": "2026-07-01T09:00:00",
  "salesEnd": "2026-08-15T17:00:00",
  "status": "DRAFT",
  "ticketTypes": [
    {
      "name": "General Admission",
      "price": 49.99,
      "description": "Standing admission",
      "totalAvailable": 500
    }
  ]
}
```

Required: nonblank `name`, nonblank `venue`, non-null `status`, and at least one valid ticket type. Each ticket type requires a nonblank name and a nonnegative price. Date fields, description, and inventory are optional at DTO validation level.

Returns `201` with the created event, generated UUIDs, ticket types, and audit timestamps.

### `GET /events`

Lists events whose `organizer_id` matches the JWT subject. Accepts pagination. Returns `200 Page<EventSummary>`.

### `GET /events/{eventId}`

Returns an organizer-owned event with ticket types and audit timestamps. Returns `200` or `404`.

### `PUT /events/{eventId}`

Replaces editable event fields and reconciles ticket types. The body has the create shape plus a required event `id`; it must equal the path ID.

- A ticket type with no `id` is created.
- A ticket type with an ID already on the event is updated.
- Existing ticket types omitted from the body are deleted through orphan removal.
- An unknown ticket-type ID produces `400`.

Returns `200` with the updated event.

### `DELETE /events/{eventId}`

Deletes the organizer-owned event if it exists. Returns `204` whether or not an owned event was found.

## Public published events

### `GET /published-events`

Lists only events with status `PUBLISHED`. Optional query parameter `q` performs PostgreSQL English full-text search across event name and venue. Accepts pagination and returns `200 Page<PublishedEventSummary>`.

Search is PostgreSQL-specific and is not portable to H2 without an alternative implementation.

### `GET /published-events/{eventId}`

Returns a published event and its ticket types. Returns `200` or `404`.

## Ticket purchase and ownership

### `POST /events/{eventId}/ticket-types/{ticketTypeId}/tickets`

Purchases one ticket for the authenticated user and returns `204`.

Current implementation details:

- `eventId` is present in the route but is not checked by the controller/service.
- The ticket type is loaded with a pessimistic write lock.
- Sold inventory is counted from ticket rows and compared with `totalAvailable`.
- The new ticket status is `PURCHASED`.
- A unique active QR code is generated and stored as base64-encoded PNG data.
- No payment provider is called.

Returns `400` for a missing user/ticket type or sold-out inventory and `500` if QR generation fails.

### `GET /tickets`

Returns the authenticated purchaser's tickets as a paginated list.

### `GET /tickets/{ticketId}`

Returns ticket/event display details only when the ticket belongs to the authenticated purchaser. Returns `200` or `404`.

### `GET /tickets/{ticketId}/qr-codes`

Returns the purchaser's QR image as `image/png` with a content length. Returns `500` under the current exception mapping when no QR code is found or stored data cannot be decoded.

## Ticket validation

### `POST /ticket-validations`

Requires `ROLE_STAFF` under the current exact matcher.

QR scan request:

```json
{ "id": "<qr-code-uuid>", "method": "QR_SCAN" }
```

Manual request:

```json
{ "id": "<ticket-uuid>", "method": "MANUAL" }
```

Any method value other than `MANUAL` follows the QR branch. The request DTO currently has no bean-validation annotations, so null/invalid input may result in framework or repository errors.

The service records a validation attempt. If the ticket has no previous `VALID` attempt, the result is `VALID`; otherwise it is `INVALID`.

```json
{ "ticketId": "<ticket-uuid>", "status": "VALID" }
```

## State values

- Event: `DRAFT`, `PUBLISHED`, `CANCELLED`, `COMPLETED`
- Ticket: `PURCHASED`, `CANCELLED`
- QR code: `ACTIVE`, `EXPIRED`
- Validation method: `QR_SCAN`, `MANUAL`
- Validation result: `VALID`, `INVALID`, `EXPIRED`

`CANCELLED`, `COMPLETED`, and `EXPIRED` are modeled but not automatically transitioned by current services.
