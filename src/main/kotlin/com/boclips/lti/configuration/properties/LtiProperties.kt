package com.boclips.lti.configuration.properties

import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.Valid

@Component
@Validated
@ConfigurationProperties(prefix = "boclips.lti")
class LtiProperties {
    @Valid
    val consumer: LtiConsumer = LtiConsumer()
    @URL
    lateinit var landingPage: String
    @URL
    lateinit var errorPage: String
}