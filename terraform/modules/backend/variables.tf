variable "resource_group_name" {
  type = string
}

variable "location" {
  type = string
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

variable "jwt_issuer_uri" {
  type = string
}