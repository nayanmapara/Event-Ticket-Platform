variable "resource_group_name" {
  type = string
}

variable "location" {
  type = string
}

variable "keycloak_admin_password" {
  type      = string
  sensitive = true
}

variable "db_fqdn" {
  type = string
}

variable "db_name" {
  type = string
}

variable "db_username" {
  type = string
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "kc_hostname_admin_url" {
  type        = string
  description = "Admin URL for Keycloak"
}

variable "kc_hostname_url" {
  type        = string
  description = "Public URL for Keycloak"
}