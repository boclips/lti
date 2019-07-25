package com.boclips.lti.v1p1.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "video-service")
class VideoServiceProperties {
    var accessTokenUri: String = ""
    var clientId: String = ""
    var clientSecret: String = ""
}
