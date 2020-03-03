package com.boclips.lti.v1p1.config

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.lti.v1p1.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.lti.v1p1.infrastructure.service.CollectionsClientFactory
import com.boclips.lti.v1p1.infrastructure.service.VideosClientFactory
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
class FakeClientsConfig {
    @Bean
    fun videosClientFactory(
        ltiProperties: LtiProperties,
        videoServiceProperties: VideoServiceProperties,
        integrationDocumentRepository: MongoIntegrationDocumentRepository
    ): VideosClientFactory {
        return VideosClientFactory(
            preconfiguredVideosClient = VideosClientFake(),
            ltiProperties = ltiProperties,
            videoServiceProperties = videoServiceProperties,
            integrationDocumentRepository = integrationDocumentRepository
        )
    }

    @Bean
    fun collectionsClientFactory(
        ltiProperties: LtiProperties,
        videoServiceProperties: VideoServiceProperties,
        integrationDocumentRepository: MongoIntegrationDocumentRepository
    ): CollectionsClientFactory {
        return CollectionsClientFactory(
            preconfiguredCollectionsClient = CollectionsClientFake(),
            ltiProperties = ltiProperties,
            videoServiceProperties = videoServiceProperties,
            integrationDocumentRepository = integrationDocumentRepository
        )
    }
}
