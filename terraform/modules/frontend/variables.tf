variable "resource_group_name" {
  description = "The resource group to deploy into"
  type        = string
}

variable "location" {
  description = "Azure region"
  type        = string
  default     = "East US"
}

variable "static_site_name" {
  description = "Globally unique name for the static site"
  type        = string
}

variable "backend_url" {
  description = "URL of the backend API"
  type        = string
}