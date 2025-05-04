# 🏗️ Initial Architecture - Event Ticketing Platform

This document outlines the initial system architecture for the **Event Ticketing Platform**. It describes the interaction between various services and how responsibilities are distributed across components.

---

## 🔧 Components Overview

| Component       | Tech Stack        | Description |
|----------------|-------------------|-------------|
| **Frontend**    | React.js          | UI for attendees, organizers, and staff to interact with the system |
| **Backend**     | Spring Boot (Java)| Handles business logic, database operations, and integrates with Keycloak |
| **Database**    | PostgreSQL        | Stores all persistent data such as users, events, tickets, and validations |
| **Auth Server** | Keycloak          | Provides OAuth2/OpenID Connect-based authentication and authorization |

---

## 🔁 Component Interaction Flow

1. **User (via Frontend)** logs in using credentials managed by **Keycloak**.
2. Keycloak returns an **access token (JWT)**.
3. The **React frontend** sends API requests to the **Spring Boot backend**, attaching the JWT.
4. The **backend** verifies the token with **Keycloak** before processing requests.
5. The backend accesses or modifies data in the **PostgreSQL** database as needed.

---

## 🔐 Authentication & Authorization

- Keycloak handles login, registration, token issuance, and role management.
- Frontend uses `Authorization: Bearer <JWT>` headers for secure communication.
- Backend verifies JWTs and enforces role-based access.

Roles:
- **Organizer** – Can create events and manage ticket types.
- **Attendee** – Can view events and purchase tickets.
- **Staff** – Can validate tickets at the event gate.

---

## 📊 Architecture Diagram

![Architecture Diagram](https://github.com/nayanmapara/Event-Ticket-Platform/blob/main/docs/Architecture/drawio/architecture.png)

---

## 📦 Deployment (Planned)

- Dockerized setup for PostgreSQL and Keycloak
- Spring Boot running locally or in container
- React served via Vite/dev server initially
- CI/CD and production pipeline planned for later stages

---

## 🧩 Next Steps

- Finalize Docker Compose file
- Connect backend to PostgreSQL & Keycloak
- Secure all endpoints and define scopes in Keycloak
- Begin integration of frontend API calls

---

> This document evolves alongside the project and reflects the current state of architectural planning.
