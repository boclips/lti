package com.boclips.lti.v1p1.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Component
@Validated
@ConfigurationProperties(prefix = "boclips.api")
class VideoServiceProperties {
    @NotBlank
    var baseUrl: String = ""

    @NotBlank
    var tokenUrl: String = ""
}
