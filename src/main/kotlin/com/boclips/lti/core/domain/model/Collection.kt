package com.boclips.lti.core.domain.model

import java.net.URI

data class Collection(val collectionId: CollectionId, val title: String, val videos: List<Video>)

data class CollectionId(val uri: URI)
