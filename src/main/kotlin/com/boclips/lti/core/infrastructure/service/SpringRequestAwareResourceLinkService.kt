package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.domain.model.Collection
import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.v1p3.domain.model.DeepLinkingMessage
import java.net.URL

class SpringRequestAwareResourceLinkService(
    private val uriComponentsBuilderFactory: UriComponentsBuilderFactory
) : ResourceLinkService {
    override fun getVideoLink(video: Video): URL {
        return URL(
            uriComponentsBuilderFactory.getInstance()
                .replacePath("/videos/${video.videoId.value}")
                .replaceQuery(null)
                .toUriString()
        )
    }

    override fun getEmbeddableVideoLink(video: Video): URL {
        return URL(
            uriComponentsBuilderFactory.getInstance()
                .replacePath("/embeddable-videos/${video.videoId.value}")
                .replaceQuery(null)
                .toUriString()
        )
    }

    override fun getCollectionLink(collection: Collection): URL {
        return URL(
            uriComponentsBuilderFactory.getInstance()
                .replacePath("/collections/${collection.collectionId.uri.toString().substringAfterLast("/")}")
                .replaceQuery(null)
                .toUriString()
        )
    }

    override fun getCollectionsLink(): URL {
        return URL(
            uriComponentsBuilderFactory.getInstance()
                .replacePath("/collections")
                .replaceQuery(null)
                .toUriString()
        )
    }

    override fun getBaseDeepLinkingLink(targetUri: String?): URL {
        val rawUrl = uriComponentsBuilderFactory.getInstance()
            .replacePath("/search-and-embed")
            .replaceQuery(null)
            .toUriString()
        return URL(rawUrl)
    }

    override fun getDeepLinkingLinkWithUrlQuery(message: DeepLinkingMessage): URL {
        val rawUrl = uriComponentsBuilderFactory.getInstance()
            .replacePath("/search-and-embed")
            .replaceQuery(null)
            .queryParam("deep_link_return_url", message.returnUrl.toString())
            .queryParam("deployment_id", message.deploymentId)
            .apply {
                message.data?.let { queryParam("data", it) }
            }
            .toUriString()

        return URL(rawUrl)
    }

    override fun getAccessTokenLink(): URL {
        return URL(
            uriComponentsBuilderFactory.getInstance()
                .replacePath("/auth/token")
                .replaceQuery(null)
                .toUriString()
        )
    }

    override fun getOnePointThreeAuthResponseLink(): URL {
        return URL(
            uriComponentsBuilderFactory.getInstance()
                .replacePath("/v1p3/authentication-response")
                .replaceQuery(null)
                .toUriString()
        )
    }

    override fun getSearchVideoLink(): URL {
        val requestUrlBuilder = uriComponentsBuilderFactory.getInstance()
        val embeddableVideoUrl = requestUrlBuilder.cloneBuilder()
            .replacePath("embeddable-videos/{id}")
            .toUriString()

        return requestUrlBuilder
            .replaceQuery(null)
            .replacePath("/search")
            .queryParam("embeddable_video_url", embeddableVideoUrl)
            .build()
            .toUri()
            .toURL()
    }
}
