package com.boclips.lti.core.configuration.application

import com.boclips.lti.core.infrastructure.service.UriComponentsBuilderFactory
import com.boclips.lti.core.configuration.properties.BoclipsApiProperties
import com.boclips.lti.core.domain.repository.CollectionRepository
import com.boclips.lti.core.domain.repository.VideoRepository
import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.core.infrastructure.repository.ApiCollectionRepository
import com.boclips.lti.core.infrastructure.repository.ApiVideoRepository
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.lti.core.infrastructure.service.CollectionsClientFactory
import com.boclips.lti.core.infrastructure.service.IntegrationsClientFactory
import com.boclips.lti.core.infrastructure.service.KeycloakClientFactory
import com.boclips.lti.core.infrastructure.service.MongoBackedCollectionsClientFactory
import com.boclips.lti.core.infrastructure.service.MongoBackedIntegrationsClientFactory
import com.boclips.lti.core.infrastructure.service.MongoBackedUsersClientFactory
import com.boclips.lti.core.infrastructure.service.MongoBackedVideosClientFactory
import com.boclips.lti.core.infrastructure.service.SpringRequestAwareResourceLinkService
import com.boclips.lti.core.infrastructure.service.UsersClientFactory
import com.boclips.lti.core.infrastructure.service.VideosClientFactory
import io.opentracing.Tracer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration("coreInfrastructureContext")
class InfrastructureContext(
    private val boclipsApiProperties: BoclipsApiProperties,
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository,
    private val tracer: Tracer
) {
    @Bean
    @Profile("!test")
    fun videosClientFactory(): VideosClientFactory {
        return MongoBackedVideosClientFactory(
            boclipsApiProperties = boclipsApiProperties,
            integrationDocumentRepository = integrationDocumentRepository,
            tracer = tracer
        )
    }

    @Bean
    @Profile("!test")
    fun collectionsClientFactory(): CollectionsClientFactory {
        return MongoBackedCollectionsClientFactory(
            boclipsApiProperties = boclipsApiProperties,
            integrationDocumentRepository = integrationDocumentRepository,
            tracer = tracer
        )
    }

    @Bean
    @Profile("!test")
    fun usersClientFactory(): UsersClientFactory {
        return MongoBackedUsersClientFactory(
            boclipsApiProperties = boclipsApiProperties,
            integrationDocumentRepository = integrationDocumentRepository,
            tracer = tracer
        )
    }

    @Bean
    @Profile("!test")
    fun integrationsClientFactory(): IntegrationsClientFactory {
        return MongoBackedIntegrationsClientFactory(
            boclipsApiProperties = boclipsApiProperties,
            integrationDocumentRepository = integrationDocumentRepository,
            tracer = tracer
        )
    }

    @Bean
    fun videoRepository(videosClientFactory: VideosClientFactory): VideoRepository {
        return ApiVideoRepository(videosClientFactory)
    }

    @Bean
    fun collectionRepository(collectionsClientFactory: CollectionsClientFactory): CollectionRepository {
        return ApiCollectionRepository(collectionsClientFactory)
    }

    @Bean
    fun keycloakClientFactory(): KeycloakClientFactory {
        return KeycloakClientFactory(integrationDocumentRepository)
    }

    @Bean
    fun resourceLinkService(uriComponentsBuilderFactory: UriComponentsBuilderFactory): ResourceLinkService =
        SpringRequestAwareResourceLinkService(uriComponentsBuilderFactory)
}
