package com.boclips.lti.v1p1.domain.repository

import com.boclips.videos.service.client.Collection

interface CollectionRepository {
    fun get(collectionId: String): Collection

    fun getMyCollections(): List<Collection>
}
