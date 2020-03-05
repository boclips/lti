package com.boclips.lti.core.domain.repository

import com.boclips.lti.core.domain.model.Collection
import com.boclips.lti.core.domain.model.CollectionRequest
import com.boclips.lti.core.domain.model.CollectionsRequest

interface CollectionRepository {
    fun get(collectionRequest: CollectionRequest): Collection

    fun getMyCollections(collectionsRequest: CollectionsRequest): List<Collection>
}
