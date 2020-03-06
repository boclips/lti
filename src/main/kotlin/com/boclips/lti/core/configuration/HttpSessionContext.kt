package com.boclips.lti.core.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.session.data.mongo.JacksonMongoSessionConverter
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession
import org.springframework.session.web.http.CookieSerializer
import org.springframework.session.web.http.DefaultCookieSerializer

@Profile("!test")
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = 3600)
class HttpSessionContext {
    @Bean
    fun mongoSessionConverter(): JacksonMongoSessionConverter {
        return JacksonMongoSessionConverter()
    }

    @Bean
    fun cookieSerializer(): CookieSerializer {
        val cookieSerializer = DefaultCookieSerializer()
        cookieSerializer.setSameSite(null)
        return cookieSerializer
    }
}
