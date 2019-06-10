package com.boclips.lti.v1p1.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.Valid

@Component
@Validated
@ConfigurationProperties(prefix = "boclips.lti.v1p1")
class LtiProperties {
    @Valid
    val consumer: LtiConsumer = LtiConsumer()
}
