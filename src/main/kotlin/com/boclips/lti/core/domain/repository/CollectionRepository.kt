package com.boclips.lti.core.domain.repository

import com.boclips.lti.core.domain.model.Collection
import com.boclips.lti.core.domain.model.CollectionQuery
import com.boclips.lti.core.domain.model.CollectionsQuery

interface CollectionRepository {
    fun get(collectionQuery: CollectionQuery): Collection

    fun getMyCollections(collectionsQuery: CollectionsQuery): List<Collection>
}
