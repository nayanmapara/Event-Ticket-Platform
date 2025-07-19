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