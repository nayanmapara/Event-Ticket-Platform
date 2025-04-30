# 📘 API Documentation - Event Ticketing Platform

This document provides an overview of the **REST API endpoints** available for the Event Ticketing Platform, categorized by user roles: **Organizer**, **Attendee**, and **Staff**.

---

## 🔐 Authentication
All protected routes require a valid JWT token issued by **Keycloak**.

```
Authorization: Bearer <your_token>
```

---

## 🎤 Organizer API Endpoints

### 🎫 Event Management

- **Create Event**
```http
POST /api/v1/events
```
- **List Events**
```http
GET /api/v1/events
```
- **Get Event by ID**
```http
GET /api/v1/events/{event_id}
```
- **Update Event**
```http
PUT /api/v1/events/{event_id}
```
- **Delete Event**
```http
DELETE /api/v1/events/{event_id}
```

### 🪙 Ticket Types
- **Create Ticket Type**
```http
POST /api/v1/events/{event_id}/ticket-types
```
- **List Ticket Types**
```http
GET /api/v1/events/{event_id}/ticket-types
```
- **Get Ticket Type**
```http
GET /api/v1/events/{event_id}/ticket-types/{ticket_type_id}
```
- **Update Ticket Type**
```http
PATCH /api/v1/events/{event_id}/ticket-types/{ticket_type_id}
```
- **Delete Ticket Type**
```http
DELETE /api/v1/events/{event_id}/ticket-types/{ticket_type_id}
```

### 📊 Ticket Sales
- **List Ticket Sales for Event**
```http
GET /api/v1/events/{event_id}/tickets
```
- **Get Ticket Sale by ID**
```http
GET /api/v1/events/{event_id}/tickets/{ticket_id}
```

---

## 👤 Attendee API Endpoints

### 🔍 Browse Events
- **Search Published Events**
```http
GET /api/v1/published-events
```
- **Get Event Details**
```http
GET /api/v1/published-events/{published_event_id}
```

### 🎟️ Purchase Tickets
- **Buy Ticket**
```http
POST /api/v1/published-events/{event_id}/ticket-types/{ticket_type_id}
```

### 🎫 View Tickets
- **List My Tickets**
```http
GET /api/v1/tickets
```
- **Get Ticket by ID**
```http
GET /api/v1/tickets/{ticket_id}
```
- **Get Ticket QR Code**
```http
GET /api/v1/tickets/{ticket_id}/qr-code
```

---

## 🧾 Staff API Endpoints

### ✅ Validate Tickets
- **Validate Ticket**
```http
POST /api/v1/events/{event_id}/ticket-validations
```
- **List Validated Tickets**
```http
GET /api/v1/events/{event_id}/ticket-validations
```

### 📄 View Ticket Info
- **Get Ticket Info**
```http
GET /api/v1/tickets/{ticket_id}
```