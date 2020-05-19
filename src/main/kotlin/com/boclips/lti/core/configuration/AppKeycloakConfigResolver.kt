package com.boclips.lti.core.configuration

import com.boclips.lti.core.configuration.properties.KeycloakProperties
import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.KeycloakDeployment
import org.keycloak.adapters.KeycloakDeploymentBuilder
import org.keycloak.adapters.spi.HttpFacade
import org.keycloak.representations.adapters.config.AdapterConfig
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class AppKeycloakConfigResolver(private val keycloakProperties: KeycloakProperties) : KeycloakConfigResolver {
    init {
        assert(keycloakProperties.realm.isNotBlank())
        assert(keycloakProperties.url.isNotBlank())
    }

    override fun resolve(facade: HttpFacade.Request?): KeycloakDeployment =
        KeycloakDeploymentBuilder.build(
            AdapterConfig().apply {
                isBearerOnly = true
                sslRequired = "external"
                confidentialPort = 0
                isUseResourceRoleMappings = true

                resource = "lti-pearson-myrealize"
                realm = keycloakProperties.realm
                authServerUrl = keycloakProperties.url
            }
        )
}
