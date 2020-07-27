package com.boclips.lti.core.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "boclips.session")
class HttpSessionProperties {
    var useSecureCookie: Boolean = true
}
