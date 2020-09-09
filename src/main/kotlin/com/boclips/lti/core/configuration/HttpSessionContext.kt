package com.boclips.lti.core.configuration

import com.boclips.lti.core.configuration.properties.HttpSessionProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.session.data.mongo.JacksonMongoSessionConverter
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession
import org.springframework.session.web.http.CookieSerializer
import org.springframework.session.web.http.DefaultCookieSerializer

@Profile("!test")
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = 10)
class HttpSessionContext(private val httpSessionProperties: HttpSessionProperties) {
    @Bean
    fun mongoSessionConverter(): JacksonMongoSessionConverter {
        return JacksonMongoSessionConverter()
    }

    @Bean
    fun cookieSerializer(): CookieSerializer {
        val cookieSerializer = DefaultCookieSerializer()
        cookieSerializer.setUseHttpOnlyCookie(true)
        cookieSerializer.setUseSecureCookie(httpSessionProperties.useSecureCookie)
        if(httpSessionProperties.setSameSiteNone) {
            cookieSerializer.setSameSite("None")
        }
        return cookieSerializer
    }
}
