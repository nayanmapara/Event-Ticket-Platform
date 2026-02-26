FROM quay.io/keycloak/keycloak:26.0

# ---- BUILD TIME OPTIONS ----
ENV KC_DB=postgres
ENV KC_CACHE=local

# ---- RUNTIME OPTIONS ----
ENV KC_HTTP_ENABLED=true
ENV KC_PROXY=edge
ENV KC_PROXY_HEADERS=xforwarded
ENV KC_HOSTNAME=keycloak-latest-j99b.onrender.com
ENV KC_HOSTNAME_STRICT=false
ENV KC_HOSTNAME_STRICT_HTTPS=false

# Build happens with KC_DB locked in
RUN /opt/keycloak/bin/kc.sh build

EXPOSE 8080

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start", "--optimized", "--http-port=8080"]