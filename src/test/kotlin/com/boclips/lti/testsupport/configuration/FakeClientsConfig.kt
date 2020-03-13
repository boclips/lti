package com.boclips.lti.testsupport.configuration

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
    object FakeVideoClientFactory : VideosClientFactory {
        private val clientsMap: MutableMap<String, VideosClientFake> = HashMap()

        override fun getClient(integrationId: String) = clientsMap.getOrPut(integrationId, { VideosClientFake() })

        fun clear() {
            clientsMap.clear()
        }
    }

    @Bean
    fun videosClientFactory() = FakeVideoClientFactory

    object FakeCollectionsClientFactory : CollectionsClientFactory {
        private val clientsMap: MutableMap<String, CollectionsClientFake> = HashMap()

        override fun getClient(integrationId: String) = clientsMap.getOrPut(integrationId, { CollectionsClientFake() })

        fun clear() {
            clientsMap.clear()
        }
    }

    @Bean
    fun collectionsClientFactory() = FakeCollectionsClientFactory
}
