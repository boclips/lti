package com.boclips.lti.v1p1.domain.repository

import com.boclips.lti.v1p1.domain.model.Collection
import com.boclips.lti.v1p1.domain.model.CollectionRequest

interface CollectionRepository {
    fun get(collectionRequest: CollectionRequest): Collection

    fun getMyCollections(): List<Collection>
}
