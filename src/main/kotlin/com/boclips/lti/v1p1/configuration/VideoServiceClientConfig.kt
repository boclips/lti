package com.boclips.lti.v1p1.configuration

import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.videos.api.httpclient.CollectionsClient
import com.boclips.videos.api.httpclient.VideosClient
import com.boclips.videos.api.httpclient.helper.ServiceAccountCredentials
import com.boclips.videos.api.httpclient.helper.ServiceAccountTokenFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class VideoServiceClientConfig {
    @Bean
    fun videosClient(properties: VideoServiceProperties): VideosClient {
        return VideosClient.create(
            apiUrl = properties.baseUrl,
            tokenFactory = ServiceAccountTokenFactory(
                ServiceAccountCredentials(
                    // TODO Use token URI?
                    properties.baseUrl,
                    properties.clientId,
                    properties.clientSecret
                )
            )
        )
    }

    @Bean
    fun collectionsClient(properties: VideoServiceProperties): CollectionsClient {
        return CollectionsClient.create(
            apiUrl = properties.baseUrl,
            tokenFactory = ServiceAccountTokenFactory(
                ServiceAccountCredentials(
                    // TODO Use token URI?
                    properties.baseUrl,
                    properties.clientId,
                    properties.clientSecret
                )
            )
        )
    }
}
