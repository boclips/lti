package com.boclips.lti.core.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@ConfigurationProperties("keycloak")
@Component
class KeycloakProperties {
   lateinit var realm: String
   lateinit var url: String
}
