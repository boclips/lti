package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.v1p1.domain.model.Collection
import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.videos.api.httpclient.CollectionsClient
import feign.FeignException
import org.springframework.stereotype.Repository

@Repository
class ApiCollectionRepository(
    private val collectionsClient: CollectionsClient
) : CollectionRepository {
    override fun get(collectionId: String): Collection {
        try {
            return CollectionResourceConverter.toCollection(collectionsClient.getCollection(collectionId))
        } catch (exception: FeignException.NotFound) {
            throw ResourceNotFoundException("Collection with id $collectionId not found")
        }
    }

    override fun getMyCollections(): List<Collection> {
        return collectionsClient.getCollections()._embedded.collections.map(CollectionResourceConverter::toCollection)
    }
}
