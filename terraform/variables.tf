variable "subscription_id" {
  description = "The Azure subscription ID"
  type        = string
}

variable "location" {
  default     = "Canada Central"
  description = "Azure region"
}

variable "resource_group_name" {
  default     = "event-ticket-platform-rg"
  description = "Name of the resource group"
}

variable "postgres_username" {
  type        = string
  description = "Username for the PostgreSQL database"
}

variable "postgres_password" {
  type        = string
  sensitive   = true
}

variable "keycloak_admin_password" {
  type        = string
  sensitive   = true
}

variable "db_name" {
  type        = string
  description = "Name of the PostgreSQL database"
}

variable "db_username" {
  type        = string
  description = "Username for the PostgreSQL database"
}

variable "db_password" {
  type        = string
  sensitive   = true
  description = "Password for the PostgreSQL database"
}

variable "db_fqdn" {
  type        = string
  description = "Fully qualified domain name of the PostgreSQL database"
}
