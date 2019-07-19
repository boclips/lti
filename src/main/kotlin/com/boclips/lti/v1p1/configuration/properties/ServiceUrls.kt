package com.boclips.lti.v1p1.configuration.properties

import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotNull

@Component
@Validated
@ConfigurationProperties("boclips.lti.gateway.services")
class ServiceUrls {
    @URL
    @NotNull
    lateinit var apiUrl: String
}
