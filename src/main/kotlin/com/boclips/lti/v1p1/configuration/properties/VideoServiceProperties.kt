package com.boclips.lti.v1p1.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Component
@Validated
@ConfigurationProperties(prefix = "video-service")
class VideoServiceProperties {
    @NotBlank
    var baseUrl: String = ""

    @NotBlank
    var accessTokenUri: String = ""

    @NotBlank
    var clientId: String = ""

    @NotBlank
    var clientSecret: String = ""
}
