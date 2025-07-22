# 🎟️ Event Ticket Platform

A full-stack event ticketing application built with modern technologies, supporting event creation, publication, ticket sales, and validation. Features include secure role-based access, CI/CD deployment, and infrastructure as code using Terraform.


## 🌐 Live Demo

- **Frontend**: [event-ticket-platform](https://kind-water-098b69f10.2.azurestaticapps.net)
- **Backend**: [Spring Boot API](https://tickets-backend.azurewebsites.net)
- **Keycloak Auth**: [Keycloak](https://keycloak-app-etp.azurewebsites.net)



## 🧰 Tech Stack

### 🔙 Backend
- **Java 21**, Spring Boot
- **Spring Security + Keycloak (OIDC)**
- **RESTful APIs + JWT Authorization**
- **PostgreSQL Flexible Server (Azure)**
- **Terraform (Infrastructure as Code)**

### 🌐 Frontend
- **React + TypeScript + Vite**
- **TailwindCSS + ShadCN + Lucide Icons**
- **Axios-based API integration**
- **Role-based UI (Attendee, Organizer, Staff)**

### ☁️ DevOps / Cloud
- **Azure App Services (Spring Boot + Docker for Keycloak)**
- **Azure Static Web App (React frontend)**
- **GitHub Actions for CI/CD**
- **Terraform Modules for reusable IaC**


## 🚀 Features

- 👤 **OIDC Login via Keycloak** with dynamic role assignment
- 🗓️ Event creation, editing, publishing
- 🎫 Ticket purchase & validation
- 📱 Mobile-first responsive UI
- 🔐 Protected routes based on roles (`ORGANIZER`, `STAFF`, `ATTENDEE`)
- 🔁 Fully automated deploy using GitHub Actions & Terraform


## ⚙️ Architecture

```plaintext
[ React Frontend (Vite) ]
        |
        | (VITE_BACKEND_URL from Azure Static Web App Env)
        v
[ Spring Boot Backend (App Service) ]
        |
        | Auth via Bearer Token (JWT)
        v
[ Keycloak (Docker on App Service) ]
        |
        | JDBC
        v
[ PostgreSQL (Azure Flexible Server) ]
```


## 🧪 Local Development

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

> Uses `application-dev.properties` for local PostgreSQL and Keycloak setup

### Frontend

```bash
cd frontend
npm install
cp .env.example .env
# Set VITE_BACKEND_URL
npm run dev
```


## ☁️ Deployment (Azure)


### 1. Terraform Setup

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

Resources provisioned:
- App Services (Backend, Keycloak)
- PostgreSQL Flexible Server
- Static Web App (Frontend)
- Secrets/Env vars (Keycloak + DB + VITE_BACKEND_URL)

### 2. CI/CD via GitHub Actions

Each module has workflows configured to:
- Build backend & frontend
- Deploy to Azure App Services / Static Web Apps
- Inject environment secrets from Terraform output



## 🔐 Roles & Permissions

| Role       | Permissions                             |
|------------|------------------------------------------|
| ORGANIZER  | Create/Edit Events, View Tickets         |
| STAFF      | Validate Tickets                         |
| ATTENDEE   | View Published Events, Buy Tickets       |

All roles are managed via **Keycloak Realm Roles**.


## 🗂️ Project Structure

```
/frontend         → React + Vite frontend
/backend          → Spring Boot API
/docs             → README, architecture, etc.
/terraform        → Infrastructure-as-Code (modularized)
  └─ /modules
      ├─ resource_group
      ├─ backend
      ├─ keycloak
      ├─ database
      └─ frontend
```