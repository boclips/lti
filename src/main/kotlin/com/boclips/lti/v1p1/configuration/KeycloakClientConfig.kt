package com.boclips.lti.v1p1.configuration

import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class KeycloakClientConfig {
    @Bean
    fun restTemplateWithVideoServiceAuth(videoServiceProperties: VideoServiceProperties): RestTemplate {
        return RestTemplateBuilder()
            .basicAuthentication(videoServiceProperties.clientId, videoServiceProperties.clientSecret)
            .build()
    }
}
