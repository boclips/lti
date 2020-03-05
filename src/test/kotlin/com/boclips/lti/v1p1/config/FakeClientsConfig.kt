package com.boclips.lti.v1p1.config

import com.boclips.lti.core.infrastructure.configuration.properties.BoclipsApiProperties
import com.boclips.lti.v1p1.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.lti.core.infrastructure.service.CollectionsClientFactory
import com.boclips.lti.core.infrastructure.service.VideosClientFactory
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
        boclipsApiProperties: BoclipsApiProperties,
        integrationDocumentRepository: MongoIntegrationDocumentRepository
    ): VideosClientFactory {
        return object : VideosClientFactory {
            private val clientsMap: MutableMap<String, VideosClientFake> = HashMap()

            override fun getClient(integrationId: String) = clientsMap.getOrPut(integrationId, { VideosClientFake() })
        }
    }

    @Bean
    fun collectionsClientFactory(
        boclipsApiProperties: BoclipsApiProperties,
        integrationDocumentRepository: MongoIntegrationDocumentRepository
    ): CollectionsClientFactory {
        return object : CollectionsClientFactory {
            private val clientsMap: MutableMap<String, CollectionsClientFake> = HashMap()

            override fun getClient(integrationId: String) =
                clientsMap.getOrPut(integrationId, { CollectionsClientFake() })
        }
    }
}
