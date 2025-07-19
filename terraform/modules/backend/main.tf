resource "azurerm_service_plan" "plan" {
  name                = "backend-plan"
  location            = var.location
  resource_group_name = var.resource_group_name
  os_type             = "Linux"
  sku_name            = "B1"
}

resource "azurerm_linux_web_app" "backend" {
  name                = "tickets-backend"
  location            = azurerm_service_plan.plan.location
  resource_group_name = azurerm_service_plan.plan.resource_group_name
  service_plan_id     = azurerm_service_plan.plan.id

  site_config {
    application_stack {
      java_version        = "17"
      java_server         = "JAVA"
      java_server_version = "17"
    }
  }

  app_settings = {
    SPRING_PROFILES_ACTIVE     = "prod"
    SPRING_DATASOURCE_URL      = "jdbc:postgresql://${var.db_fqdn}:5432/${var.db_name}"
    SPRING_DATASOURCE_USERNAME = var.db_username
    SPRING_DATASOURCE_PASSWORD = var.db_password
    JWT_ISSUER_URI             = var.jwt_issuer_uri
  }
}

output "url" {
  value = "https://${azurerm_linux_web_app.backend.default_hostname}"
}