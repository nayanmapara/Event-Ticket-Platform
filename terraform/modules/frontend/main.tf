resource "azurerm_static_web_app" "frontend" {
  name                = var.static_site_name
  resource_group_name = var.resource_group_name
  location            = "Central US"
  sku_size            = "Free"

  tags = {
    environment = "production"
  }

  app_settings = {
    VITE_BACKEND_URL = var.backend_url
  }
}
