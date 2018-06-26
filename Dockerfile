from jboss/keycloak:4.0.0.Final

ADD target/keycloak-idp-oauth2-1.0.jar /opt/jboss/keycloak/modules/system/layers/keycloak/org/keycloak/keycloak-services/main

RUN sed -i 's\</resources>\<resource-root path="keycloak-idp-oauth2-1.0.jar"/></resources>\g' /opt/jboss/keycloak/modules/system/layers/keycloak/org/keycloak/keycloak-services/main/module.xml

RUN echo "<div data-ng-include data-src=\"resourceUrl + '/partials/realm-identity-provider-oidc.html'\"></div>" > /opt/jboss/keycloak/themes/base/admin/resources/partials/realm-identity-provider-oauth.html
