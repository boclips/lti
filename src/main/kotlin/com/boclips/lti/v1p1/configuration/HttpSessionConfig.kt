package com.boclips.lti.v1p1.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.session.data.mongo.JacksonMongoSessionConverter
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession

@Profile("!test")
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = 3600)
class HttpSessionConfig {
    @Bean
    fun mongoSessionConverter(): JacksonMongoSessionConverter {
        return JacksonMongoSessionConverter()
    }
}
