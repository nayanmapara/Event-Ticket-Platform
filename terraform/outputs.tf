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

# output "fqdn" {
#   value = module.database.fqdn
# }

# output "db_name" {
#   value = module.database.db_name
# }

# output "db_username" {
#   value = module.database.db_username
# }