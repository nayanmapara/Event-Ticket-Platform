resource "azurerm_service_plan" "plan" {
  name                = "keycloak-plan"
  location            = var.location
  resource_group_name = var.resource_group_name
  os_type             = "Linux"
  sku_name            = "F1"
}

resource "azurerm_linux_web_app" "app" {
  name                = "keycloak-app-etp"
  location            = var.location
  resource_group_name = var.resource_group_name
  service_plan_id     = azurerm_service_plan.plan.id

  site_config {
    always_on = false
  }

  app_settings = {
    WEBSITES_ENABLE_APP_SERVICE_STORAGE = "false"
    WEBSITES_CONTAINER_START_TIME_LIMIT = "1800"
    DOCKER_CUSTOM_IMAGE_NAME            = "nayanmapara/keycloak:azure"
    WEBSITES_PORT                       = "8080"

    KEYCLOAK_ADMIN          = "admin"
    KEYCLOAK_ADMIN_PASSWORD = var.keycloak_admin_password
    KC_HOSTNAME_ADMIN_URL   = var.kc_hostname_admin_url
    KC_HOSTNAME_URL         = var.kc_hostname_url
    KC_DB                   = "postgres"
    KC_DB_URL_HOST          = var.db_fqdn
    KC_DB_USERNAME          = var.db_username
    KC_DB_PASSWORD          = var.db_password
    KC_DB_URL_DATABASE      = var.db_name

    KC_DB_PORT = "5432"

    KC_HTTP_ENABLED          = "true"
    KC_PROXY_HEADERS         = "xforwarded"
    PROXY_ADDRESS_FORWARDING = "true"
  }
}

output "url" {
  value = "https://${azurerm_linux_web_app.app.default_hostname}"
}

output "issuer_uri" {
  value = "https://${azurerm_linux_web_app.app.default_hostname}/realms/event-ticket-platform"
}
