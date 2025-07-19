variable "name" {
  type = string
}

variable "location" {
  type = string
}

output "name" {
  value = azurerm_resource_group.this.name
}
