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

    override fun getDeepLinkingLink(message: DeepLinkingMessage?): URL {
        return URL(
            uriComponentsBuilderFactory.getInstance()
                .replacePath("/search-and-embed")
                .replaceQuery(null)
                .apply {
                    if (message != null) {
                        queryParam("deep_link_return_url", message.returnUrl.toString())
                        queryParam("deployment_id", message.deploymentId)
                        message.data?.let { queryParam("data", it) }
                    }
                }
                .toUriString()
        )
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
}
