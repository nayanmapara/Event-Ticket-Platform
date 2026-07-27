# Diagram gallery

This page provides a compact visual entry point to the platform. Detailed explanations and implementation caveats remain in the linked documents.

## System context

```mermaid
flowchart LR
    Attendee([Attendee])
    Organizer([Organizer])
    Staff([Staff])
    Platform[Event Ticket Platform]
    Keycloak[Keycloak identity provider]
    Postgres[(PostgreSQL)]

    Attendee -->|Browse, purchase, present QR| Platform
    Organizer -->|Create and manage events| Platform
    Staff -->|Scan or enter ticket IDs| Platform
    Platform <-->|Authenticate and obtain roles| Keycloak
    Platform <-->|Persist application data| Postgres
```

## Frontend route map

```mermaid
flowchart TD
    Root["Public catalog<br/>/"]
    Event["Published event detail<br/>/events/:id"]
    Login["Login and callback"]
    Purchase["Protected mock checkout"]
    Dashboard["Role-aware dashboard redirect"]
    Events["Organizer event list"]
    Manage["Create or update event"]
    Tickets["Purchaser ticket wallet"]
    Ticket["Ticket detail and QR"]
    Validate["Staff QR/manual validation"]

    Root --> Event
    Event --> Purchase
    Purchase -. unauthenticated .-> Login
    Login --> Purchase
    Root --> Dashboard
    Dashboard -->|ROLE_ORGANIZER| Events
    Dashboard -->|ROLE_STAFF| Validate
    Dashboard -->|default / attendee| Tickets
    Events --> Manage
    Tickets --> Ticket
```

## Backend responsibility map

```mermaid
flowchart LR
    subgraph HTTP[Controllers]
        EventsC[EventController]
        PublishedC[PublishedEventController]
        TypesC[TicketTypeController]
        TicketsC[TicketController]
        ValidationC[TicketValidationController]
    end

    subgraph Services[Transactional services]
        EventsS[EventService]
        TypesS[TicketTypeService]
        TicketsS[TicketService]
        QRS[QrCodeService]
        ValidationS[TicketValidationService]
    end

    subgraph Persistence[Repositories]
        EventR[EventRepository]
        TypeR[TicketTypeRepository]
        TicketR[TicketRepository]
        QRR[QrCodeRepository]
        ValidationR[TicketValidationRepository]
        UserR[UserRepository]
    end

    EventsC --> EventsS
    PublishedC --> EventsS
    TypesC --> TypesS
    TicketsC --> TicketsS
    TicketsC --> QRS
    ValidationC --> ValidationS

    EventsS --> EventR
    EventsS --> UserR
    TypesS --> TypeR
    TypesS --> TicketR
    TypesS --> UserR
    TypesS --> QRS
    TicketsS --> TicketR
    QRS --> QRR
    ValidationS --> TicketR
    ValidationS --> QRR
    ValidationS --> ValidationR
```

## Event-to-admission lifecycle

```mermaid
flowchart LR
    Draft[Organizer creates DRAFT event]
    Publish[Organizer sets PUBLISHED]
    Discover[Attendee discovers event]
    Buy[Purchase ticket]
    Lock[Lock inventory row]
    Issue[Create PURCHASED ticket]
    QR[Generate ACTIVE QR]
    Present[Present QR at venue]
    First{Earlier VALID record?}
    Valid[Record VALID]
    Invalid[Record INVALID]

    Draft --> Publish --> Discover --> Buy --> Lock --> Issue --> QR --> Present --> First
    First -->|No| Valid
    First -->|Yes| Invalid
```

## More detailed diagrams

- [Architecture and authentication sequences](README.md)
- [Entity relationship and state diagrams](../data-model.md)
- [Azure/Terraform deployment topology](../deployment.md)

All diagrams use Mermaid so they remain reviewable in source control and render on GitHub and other Mermaid-enabled Markdown viewers.
