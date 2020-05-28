package com.boclips.lti.core.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Component
@Validated
@ConfigurationProperties(prefix = "boclips.dev-support")
class DevSupportProperties {
    var integrationId: String = ""

    var initialiseDevelopmentSession: Boolean = false

    var developmentSessionUrl: String = ""
}
