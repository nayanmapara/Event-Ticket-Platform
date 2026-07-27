# Testing guide

## Current test strategy

The backend currently has two complementary test types:

- Fast unit tests instantiate service implementations directly and replace repositories or collaborating services with Mockito mocks.
- One Spring context test starts the application against an in-memory H2 database to verify that the application can create its beans.

There is not yet a frontend test runner or an end-to-end test suite.

## Running the tests

From the backend project:

```bash
cd backend/tickets
./mvnw test
```

On Windows PowerShell:

```powershell
cd backend/tickets
./mvnw.cmd test
```

Run one test class:

```bash
./mvnw -Dtest=TicketTypeServiceImplTest test
```

Run one test method:

```bash
./mvnw -Dtest=TicketTypeServiceImplTest#purchaseTicketRejectsPurchaseWhenInventoryIsSoldOut test
```

PowerShell users should use `./mvnw.cmd` in those commands. Maven writes reports to `backend/tickets/target/surefire-reports/`.

## Test inventory

The suite currently contains 12 tests: 11 service unit tests and one application-context test.

### `TicketTypeServiceImplTest`

File: `backend/tickets/src/test/java/me/nayanm/tickets/services/impl/TicketTypeServiceImplTest.java`

| Test | Behavior protected |
| --- | --- |
| `purchaseTicketCreatesPurchasedTicketAndQrCodeWhenInventoryIsAvailable` | An available ticket type creates a `PURCHASED` ticket, associates its purchaser and type, saves it, and requests QR generation. |
| `purchaseTicketRejectsPurchaseWhenInventoryIsSoldOut` | A ticket is not saved and a QR is not generated when sold inventory equals capacity. |
| `purchaseTicketRejectsUnknownUserBeforeLookingUpTicketType` | User existence is checked first and an unknown user stops the purchase flow. |
| `purchaseTicketRejectsUnknownTicketType` | An unknown ticket type produces the domain exception without saving a ticket or generating a QR. |

The success test also documents the current implementation's two `ticketRepository.save` calls. If the service is simplified to one save, update this test alongside the intentional production change.

### `TicketValidationServiceImplTest`

File: `backend/tickets/src/test/java/me/nayanm/tickets/services/impl/TicketValidationServiceImplTest.java`

| Test | Behavior protected |
| --- | --- |
| `validateTicketByQrCodeRecordsValidFirstUse` | The first use of an active QR code is stored as `VALID` with method `QR_SCAN`. |
| `validateTicketByQrCodeRecordsInvalidReplayAfterValidUse` | A ticket with an earlier `VALID` record produces an `INVALID` replay record. |
| `validateTicketManuallyUsesTicketIdAndManualMethod` | Manual validation loads the ticket ID and records method `MANUAL`. |
| `validateTicketByQrCodeRejectsMissingActiveQrCode` | A missing or inactive QR code produces `QrCodeNotFoundException`. |
| `validateTicketManuallyRejectsMissingTicket` | An unknown manual ticket ID produces `TicketNotFoundException`. |

These tests capture the implemented replay rule. They do not imply that event dates, staff assignments, ticket cancellation, or QR expiration are validated; those rules are not implemented yet.

### `TicketServiceImplTest`

File: `backend/tickets/src/test/java/me/nayanm/tickets/services/impl/TicketServiceImplTest.java`

| Test | Behavior protected |
| --- | --- |
| `listTicketsForUserDelegatesPurchaserScopeAndPagination` | Ticket listing passes both purchaser UUID and `Pageable` to the ownership-scoped repository query. |
| `getTicketForUserDelegatesTicketAndPurchaserOwnershipScope` | Ticket retrieval uses both ticket and purchaser UUIDs rather than an unrestricted ID lookup. |

### `TicketsApplicationTests`

File: `backend/tickets/src/test/java/me/nayanm/tickets/TicketsApplicationTests.java`

`contextLoads` uses `@SpringBootTest` to verify that Spring can construct the application context. It is a smoke test, not a behavioral API test.

## Test database configuration

`backend/tickets/src/test/resources/application.properties` configures:

- an in-memory H2 database named `testdb`;
- blank-password `sa` access;
- Hibernate `create-drop` schema management;
- formatted SQL logging;
- a dummy JWT issuer URI so resource-server configuration can initialize.

The Mockito unit tests do not connect to H2, PostgreSQL, Keycloak, or the network.

## Known test warning

During the context test, H2 reports a schema-generation error for `qr_codes.value`. `VALUE` is reserved by H2, while PostgreSQL accepts the production mapping. Hibernate logs the H2 DDL failure and the current context test still completes successfully.

This means `contextLoads` passing does not prove that the complete H2 schema was created. Appropriate fixes include:

1. Rename the database column to a non-reserved name such as `image_data` through a migration.
2. Configure H2 compatibility or quoted identifiers only if doing so remains representative of production.
3. Prefer PostgreSQL integration tests with Testcontainers for repository and schema behavior.

Do not suppress the warning without ensuring the test database actually creates every table.

## Mockito conventions

Service tests use JUnit 5 with `@ExtendWith(MockitoExtension.class)`.

- Mock repositories and external collaborators, not the class under test.
- Construct the service directly in `@BeforeEach`.
- Stub only behavior needed by the scenario.
- Verify important side effects, especially writes and QR generation.
- Use `ArgumentCaptor` when the state of a newly created entity is part of the contract.
- Assert both the result and that forbidden side effects did not occur on failure paths.

Test names follow the pattern `methodExpectedBehaviorWhenCondition` and should describe business behavior rather than implementation syntax.

## Recommended next coverage

Highest-value backend additions are:

1. `EventServiceImpl` creation and organizer ownership tests.
2. Event-update reconciliation tests for create, update, remove, unknown ID, and mismatched event ID.
3. `QrCodeServiceImpl` image generation, decoding, ownership, and corrupt-data tests.
4. Controller tests with `MockMvc` for status codes, validation errors, JWT claims, and role enforcement.
5. Repository integration tests against PostgreSQL/Testcontainers, especially full-text search and pessimistic locking.
6. A concurrent purchase test proving inventory cannot be oversold.
7. Tests for the security gap around nested organizer routes before changing the matchers.

Frontend priorities are:

1. Configure Vitest and React Testing Library.
2. Test authentication redirects and callback restoration.
3. Test role-based dashboard routing.
4. Test event-form date conversion and ticket-type reconciliation.
5. Test API error parsing and pagination.
6. Add Playwright or Cypress flows for login, event publication, purchase, QR display, and validation replay.

## Adding a service unit test

Place new unit tests under the package matching production code:

```text
src/main/java/me/nayanm/tickets/services/impl/ExampleServiceImpl.java
src/test/java/me/nayanm/tickets/services/impl/ExampleServiceImplTest.java
```

Use Arrange–Act–Assert structure:

```java
@Test
void operationReturnsExpectedResultWhenDependencySucceeds() {
    // Arrange
    when(repository.findById(id)).thenReturn(Optional.of(entity));

    // Act
    Result result = service.operation(id);

    // Assert
    assertEquals(expected, result);
    verify(repository).findById(id);
}
```

Keep each test independent. Use new UUIDs and entities per test, and avoid relying on test execution order.

## Before committing tests

Run:

```bash
./mvnw test
git diff --check
```

Confirm that:

- the new test fails when the protected behavior is deliberately broken;
- production behavior was not changed merely to satisfy an incorrect test;
- failures test outcomes, not private implementation details unless those details are intentional contracts;
- no build output under `target/` is staged;
- unrelated working-tree changes remain untouched.
