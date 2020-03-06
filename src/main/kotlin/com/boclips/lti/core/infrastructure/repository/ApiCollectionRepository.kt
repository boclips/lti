package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.core.domain.exception.ResourceNotFoundException
import com.boclips.lti.core.domain.model.Collection
import com.boclips.lti.core.domain.model.CollectionRequest
import com.boclips.lti.core.domain.model.CollectionsRequest
import com.boclips.lti.core.domain.repository.CollectionRepository
import com.boclips.lti.core.infrastructure.service.CollectionsClientFactory
import com.boclips.videos.api.request.Projection
import com.boclips.videos.api.request.collection.CollectionFilterRequest
import feign.FeignException

class ApiCollectionRepository(
    private val collectionsClientFactory: CollectionsClientFactory
) : CollectionRepository {
    override fun get(collectionRequest: CollectionRequest): Collection {
        try {
            return CollectionResourceConverter.toCollection(
                collectionsClientFactory.getClient(collectionRequest.integrationId).getCollection(
                    collectionId = collectionRequest.collectionId,
                    projection = Projection.details
                )
            )
        } catch (exception: FeignException.NotFound) {
            throw ResourceNotFoundException("Collection with id ${collectionRequest.collectionId} not found")
        }
    }

    override fun getMyCollections(collectionsRequest: CollectionsRequest): List<Collection> {
        return collectionsClientFactory.getClient(collectionsRequest.integrationId)
            .getCollections(CollectionFilterRequest(projection = Projection.details))
            ._embedded.collections.map(CollectionResourceConverter::toCollection)
    }
}
