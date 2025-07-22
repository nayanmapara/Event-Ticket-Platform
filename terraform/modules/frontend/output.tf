output "frontend_url" {
  description = "The URL of the deployed frontend site"
  value = "https://${azurerm_static_web_app.frontend.default_host_name}"
}
