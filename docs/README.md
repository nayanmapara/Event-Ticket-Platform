# Event Ticket Platform documentation

This directory is organized by how readers use the material. The documents describe behavior present in the repository unless a section is explicitly labeled historical, modeled-only, planned, or recommended.

## Start here

| Need | Document |
| --- | --- |
| Understand the whole system | [Architecture overview](architecture/README.md) |
| Set up a development environment | [Local-development guide](guides/local-development.md) |
| Run or extend the tests | [Testing guide](guides/testing.md) |
| Integrate with the REST API | [API reference](reference/api.md) |
| Understand persistence and lifecycle rules | [Data-model reference](reference/data-model.md) |
| Deploy or operate the platform | [Deployment guide](operations/deployment.md) |
| Browse all system diagrams | [Diagram gallery](architecture/diagrams.md) |
| Review the original modeling process | [ERD evolution](history/erd-evolution.md) |

## Directory structure

```text
docs/
├── README.md
├── architecture/
│   ├── README.md              # System design, boundaries, and flows
│   ├── diagrams.md            # Consolidated Mermaid diagrams
│   └── assets/                # Editable and rendered architecture sources
├── guides/
│   ├── local-development.md   # Contributor setup and troubleshooting
│   └── testing.md             # Test inventory, commands, and conventions
├── reference/
│   ├── api.md                 # Implemented HTTP contract
│   └── data-model.md          # Entities, states, and invariants
├── operations/
│   └── deployment.md          # Build, infrastructure, release, and recovery
└── history/
    ├── erd-evolution.md       # Historical domain-model narrative
    └── assets/                # Original ERD images and editable sources
```

## Folder responsibilities

### `architecture/`

Explains how components interact and why the system is shaped this way. Put cross-component flows, trust boundaries, sequence diagrams, and architecture decision records here.

### `guides/`

Contains task-oriented instructions. A guide should help a contributor complete a concrete workflow from start to finish.

### `reference/`

Contains implementation-backed contracts and facts that readers consult rather than follow sequentially. Keep endpoint, payload, entity, enum, and invariant details here.

### `operations/`

Contains deployment, infrastructure, release, observability, backup, rollback, and incident-related material.

### `history/`

Preserves earlier design thinking without presenting it as the current implementation. Historical documents should link forward to the current reference where useful.

## Source-of-truth order

When documentation and implementation disagree, use this order:

1. Controllers and security configuration for HTTP and authorization behavior
2. Services for business rules and transaction behavior
3. DTO/entity validation for payload and persistence constraints
4. Application properties, container definitions, and Terraform for runtime configuration
5. Tests for explicitly protected behavior
6. These documents

Fix contradictory documentation in the same change that modifies an endpoint, role, entity, environment variable, test contract, or deployment resource.

## Documentation conventions

- Use relative repository links so documentation works in forks and local clones.
- Keep images and editable diagram sources beside the document family that owns them.
- Prefer Mermaid for new diagrams that benefit from source review.
- Label historical and planned behavior explicitly.
- Never copy credentials, tokens, private URLs, or untracked environment files into documentation.
- Use exact commands and repository-relative paths.
- Update this index when adding a new top-level document.

## Important current-state notes

- The production URLs in `frontend/src/main.tsx` and `frontend/src/lib/api.ts` override the environment-driven setup implied by Terraform.
- `WebConfig` and `SecurityConfig` both define hard-coded CORS origins.
- Only `GET /api/v1/published-events/**` is anonymous. Additional role checks are limited to the exact `/api/v1/events` and `/api/v1/ticket-validations` matchers.
- Ticket validation records every attempt: the first successful validation is `VALID`, and later attempts are `INVALID`.
- Event and ticket date values use ISO-8601 local date-times without an explicit timezone contract.
