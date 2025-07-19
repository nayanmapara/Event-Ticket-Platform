variable "resource_group_name" {
  type = string
}

variable "location" {
  type = string
}

variable "postgres_username" {
  type = string
}

variable "postgres_password" {
  type      = string
  sensitive = true
}