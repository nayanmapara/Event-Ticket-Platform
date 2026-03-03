# Data model and lifecycle

The backend uses JPA entities and UUID primary keys. Spring Data auditing populates `created_at` and `updated_at` through the listener configured in `META-INF/orm.xml`.

## Relationships

Most relationship join columns are not explicitly marked `nullable=false`, even when the service layer treats them as required.

## Entities

### User (`users`)

The primary key is the Keycloak user UUID rather than a generated database UUID. A user stores name/email, organized events, optional attending/staffing relationships, and audit timestamps. Users are lazily provisioned from authenticated JWT claims.

### Event (`events`)

Fields: UUID, name, optional start/end, venue, optional sales start/end, status, organizer, ticket types, attendee/staff collections, and audit timestamps.

An organizer owns events through the JWT subject. Deleting an event cascades through ticket types; ticket-type orphan removal is also used by event updates.

### TicketType (`ticket_types`)

Fields: UUID, name, `Double` price, optional description, optional total availability, parent event, tickets, and audit timestamps.

Purchase uses a pessimistic database lock on this row to serialize the count-and-create inventory check. A null `totalAvailable` is accepted by DTO/entity definitions but purchase unboxes it as an integer, so sellable ticket types should always have an inventory value.

### Ticket (`tickets`)

Fields: UUID, status, ticket type, purchaser, validation history, QR codes, and audit timestamps. New purchases use `PURCHASED`. `CANCELLED` is modeled but no cancellation service exists.

### QrCode (`qr_codes`)

The QR UUID is generated before persistence and encoded in the image. The base64 PNG is stored in a `TEXT` column. New codes are `ACTIVE`; `EXPIRED` exists but no expiration transition is implemented.

### TicketValidation (`ticket_validations`)

Each validation attempt records a generated UUID, `VALID`/`INVALID`/`EXPIRED` status, `QR_SCAN`/`MANUAL` method, ticket, and audit timestamps. The first validation with no earlier `VALID` record becomes `VALID`; later attempts become `INVALID`.

## Enums

| Enum | Values | Automated transitions |
| --- | --- | --- |
| Event status | `DRAFT`, `PUBLISHED`, `CANCELLED`, `COMPLETED` | Set directly by organizer request; public queries filter `PUBLISHED` |
| Ticket status | `PURCHASED`, `CANCELLED` | Purchase creates `PURCHASED`; no cancellation implementation |
| QR status | `ACTIVE`, `EXPIRED` | Generation creates `ACTIVE`; no expiry implementation |
| Validation method | `QR_SCAN`, `MANUAL` | Selected by staff request |
| Validation status | `VALID`, `INVALID`, `EXPIRED` | First/replay rule sets valid/invalid; no expired rule |

### Implemented state transitions

Solid entry transitions are implemented. Transitions labeled `modeled only` exist in enums but have no service that performs them automatically.

## Invariants currently enforced

- An event request has a nonblank name/venue, a status, and at least one ticket type.
- Ticket-type name is nonblank and price is nonnegative.
- Organizer queries scope events by organizer UUID.
- Purchaser queries scope tickets and QR codes by purchaser UUID.
- Concurrent purchase requests lock the ticket type before counting inventory.
- An event update cannot change the event ID and cannot reference another unknown ticket-type ID.

## Important unenforced rules

- Event start/end and sales start/end ordering
- Published status before purchase
- Sales-window or event-date eligibility
- Path `eventId` matching the purchased ticket type's event
- Positive/non-null inventory at purchase time
- Ticket/QR expiration and cancellation during validation
- Staff membership in the event being validated
- Unique user email or QR-per-ticket constraints

## Schema management

Development and production profiles use `spring.jpa.hibernate.ddl-auto=update`. This is convenient during prototyping but does not provide reviewable, repeatable, or reversible migrations. Add Flyway or Liquibase before production schema evolution and document backup/rollback procedures.

Prices should move from floating-point `Double` to `BigDecimal` plus an explicit currency if real financial processing is added. Date-time fields should also adopt an explicit offset/timezone policy.
