output "keycloak_url" {
  value = module.keycloak.url
}

output "backend_url" {
  value = module.backend.url
}

output "keycloak_issuer_uri" {
  value = module.keycloak.issuer_uri
}

output "resource_group_name" {
  value = module.resource_group.name
}

output "static_site_url" {
  value = module.frontend.frontend_url
}