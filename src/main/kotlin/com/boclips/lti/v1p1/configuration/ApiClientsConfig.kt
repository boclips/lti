package com.boclips.lti.v1p1.configuration

import com.boclips.lti.core.infrastructure.configuration.properties.BoclipsApiProperties
import com.boclips.lti.core.infrastructure.service.CollectionsClientFactory
import com.boclips.lti.core.infrastructure.service.MongoBackedCollectionsClientFactory
import com.boclips.lti.core.infrastructure.service.MongoBackedVideosClientFactory
import com.boclips.lti.core.infrastructure.service.VideosClientFactory
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class ApiClientsConfig {
    @Bean
    fun videosClientFactory(
        properties: BoclipsApiProperties,
        boclipsApiProperties: BoclipsApiProperties,
        integrationDocumentRepository: MongoIntegrationDocumentRepository
    ): VideosClientFactory {
        return MongoBackedVideosClientFactory(
            boclipsApiProperties = boclipsApiProperties,
            integrationDocumentRepository = integrationDocumentRepository
        )
    }

    @Bean
    fun collectionsClientFactory(
        properties: BoclipsApiProperties,
        boclipsApiProperties: BoclipsApiProperties,
        integrationDocumentRepository: MongoIntegrationDocumentRepository
    ): CollectionsClientFactory {
        return MongoBackedCollectionsClientFactory(
            boclipsApiProperties = boclipsApiProperties,
            integrationDocumentRepository = integrationDocumentRepository
        )
    }
}
