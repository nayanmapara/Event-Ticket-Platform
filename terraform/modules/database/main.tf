resource "azurerm_postgresql_flexible_server" "pg" {
  name                   = "eventticketdb"
  resource_group_name    = var.resource_group_name
  location               = var.location
  administrator_login    = var.postgres_username
  administrator_password = var.postgres_password
  sku_name               = "B_Standard_B1ms"
  version                = "14"
  storage_mb             = 32768
  zone                   = "1"
}

resource "azurerm_postgresql_flexible_server_firewall_rule" "allow_all" {
  name       = "allow-all"
  server_id  = azurerm_postgresql_flexible_server.pg.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "255.255.255.255"
}

resource "azurerm_postgresql_flexible_server_database" "app" {
  name      = "tickets"
  server_id = azurerm_postgresql_flexible_server.pg.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

resource "azurerm_postgresql_flexible_server_database" "keycloak" {
  name      = "keycloak"
  server_id = azurerm_postgresql_flexible_server.pg.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

output "fqdn" {
  value = azurerm_postgresql_flexible_server.pg.fqdn
}

output "db_name" {
  value = azurerm_postgresql_flexible_server_database.app.name
}

output "db_username" {
  value = azurerm_postgresql_flexible_server.pg.name
}
