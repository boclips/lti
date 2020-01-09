package com.boclips.lti.v1p1.domain.repository

import com.boclips.videos.api.response.collection.CollectionResource

interface CollectionRepository {
    fun get(collectionId: String): CollectionResource

    fun getMyCollections(): List<CollectionResource>
}
