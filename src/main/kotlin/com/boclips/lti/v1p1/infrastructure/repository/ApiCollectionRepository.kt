package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.videos.api.httpclient.CollectionsClient
import com.boclips.videos.api.response.collection.CollectionResource
import mu.KLogging
import org.springframework.stereotype.Repository

@Repository
class ApiCollectionRepository(
    private val collectionsClient: CollectionsClient
) : CollectionRepository {
    companion object : KLogging()

    override fun get(collectionId: String): CollectionResource {
        return collectionsClient.getCollection(collectionId)
    }

    override fun getMyCollections(): List<CollectionResource> {
        return collectionsClient.getCollections()._embedded.collections
    }
}
