package com.boclips.lti.v1p1.configuration.properties

import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Component
@Validated
@ConfigurationProperties(prefix = "boclips.api")
class ApiProperties {
    @URL
    @NotBlank
    lateinit var url: String
}
