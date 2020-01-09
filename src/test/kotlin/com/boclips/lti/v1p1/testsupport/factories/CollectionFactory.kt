package com.boclips.lti.v1p1.testsupport.factories

import com.boclips.lti.v1p1.domain.model.Collection
import com.boclips.lti.v1p1.domain.model.CollectionId
import com.boclips.lti.v1p1.domain.model.Video
import java.net.URI
import java.util.UUID

object CollectionFactory {
    fun sample(
        collectionId: String = UUID.randomUUID().toString(),
        title: String = "Greatest collection of all time",
        videos: List<Video> = emptyList()
    ) = Collection(
        collectionId = CollectionId(URI("http://server.net/v1/collections/$collectionId")),
        title = title,
        videos = videos
    )
}
