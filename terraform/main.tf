terraform {
  required_version = ">= 1.3"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.90"
    }
  }
}

module "resource_group" {
  source   = "./modules/resource_group"
  location = var.location
  name     = var.resource_group_name
}

module "database" {
  source              = "./modules/database"
  resource_group_name = module.resource_group.name
  location            = var.location
  postgres_username   = var.postgres_username
  postgres_password   = var.postgres_password
}

module "keycloak" {
  source                  = "./modules/keycloak"
  resource_group_name     = module.resource_group.name
  location                = var.location
  keycloak_admin_password = var.keycloak_admin_password
  db_fqdn                 = var.db_fqdn
  db_name                 = var.db_name
  db_username             = var.db_username
  db_password             = var.db_password
  kc_hostname_admin_url   = var.kc_hostname_admin_url
  kc_hostname_url         = var.kc_hostname_url
}

module "backend" {
  source              = "./modules/backend"
  resource_group_name = module.resource_group.name
  location            = var.location
  db_fqdn             = module.database.fqdn
  db_name             = module.database.db_name
  db_username         = module.database.db_username
  db_password         = var.postgres_password
  jwt_issuer_uri      = module.keycloak.issuer_uri
}

module "frontend" {
  source              = "./modules/frontend"
  resource_group_name = module.resource_group.name
  location            = var.location
  static_site_name    = var.static_site_name
  backend_url         = var.backend_url
}
