package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.configuration.properties.BoclipsApiProperties
import com.boclips.lti.core.infrastructure.exception.ClientNotFoundException
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.videos.api.httpclient.CollectionsClient
import com.boclips.videos.api.httpclient.helper.ServiceAccountCredentials
import com.boclips.videos.api.httpclient.helper.ServiceAccountTokenFactory
import feign.okhttp.OkHttpClient
import feign.opentracing.TracingClient
import io.opentracing.Tracer

class MongoBackedCollectionsClientFactory(
    private val boclipsApiProperties: BoclipsApiProperties,
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository,
    private val tracer: Tracer
) : CollectionsClientFactory {
    override fun getClient(integrationId: String): CollectionsClient {
        val integration = integrationDocumentRepository.findOneByIntegrationId(integrationId)

        if (integration != null) {
            return CollectionsClient.create(
                apiUrl = boclipsApiProperties.baseUrl,
                tokenFactory = ServiceAccountTokenFactory(
                    ServiceAccountCredentials(
                        // TODO Use token URI?
                        boclipsApiProperties.baseUrl,
                        integration.clientId,
                        integration.clientSecret
                    )
                ),
                feignClient = TracingClient(OkHttpClient(), tracer)
            )
        } else {
            throw ClientNotFoundException(integrationId)
        }
    }
}
