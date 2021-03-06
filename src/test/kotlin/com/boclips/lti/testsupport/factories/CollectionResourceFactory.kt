package com.boclips.lti.testsupport.factories

import com.boclips.videos.api.response.HateoasLink
import com.boclips.videos.api.response.agerange.AgeRangeResource
import com.boclips.videos.api.response.collection.CollectionResource
import com.boclips.videos.api.response.video.VideoResource
import org.springframework.hateoas.Link
import java.util.UUID

object CollectionResourceFactory {
    fun sample(
        id: String = UUID.randomUUID().toString(),
        title: String = "The best collection you could ever imagine",
        videos: List<VideoResource> = emptyList(),
        apiBaseUrl: String = "http://server.net"
    ) = CollectionResource(
        id = id,
        title = title,
        ageRange = AgeRangeResource(10, 12),
        videos = videos,
        subjects = emptySet(),
        attachments = emptySet(),
        subCollections = emptyList(),
        _links = mapOf("self" to HateoasLink.of(Link.of("$apiBaseUrl/v1/collections/$id")))
    )
}
