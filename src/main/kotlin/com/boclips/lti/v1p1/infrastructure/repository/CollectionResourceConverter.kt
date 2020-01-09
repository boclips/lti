package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.model.Collection
import com.boclips.lti.v1p1.domain.model.CollectionId
import com.boclips.videos.api.response.collection.CollectionResource
import java.net.URI

object CollectionResourceConverter {
    fun toCollection(resource: CollectionResource) =
        Collection(
            collectionId = CollectionId(URI(resource._links!!["self"]?.href!!)),
            title = resource.title!!,
            videos = resource.videos.map(VideoResourceConverter::toVideo)
        )
}
