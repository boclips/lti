package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.core.domain.exception.ResourceNotFoundException
import com.boclips.lti.core.domain.model.Collection
import com.boclips.lti.core.domain.model.CollectionQuery
import com.boclips.lti.core.domain.model.CollectionsQuery
import com.boclips.lti.core.domain.repository.CollectionRepository
import com.boclips.lti.core.infrastructure.service.CollectionsClientFactory
import com.boclips.videos.api.request.Projection
import com.boclips.videos.api.request.collection.CollectionFilterRequest
import feign.FeignException

class ApiCollectionRepository(
    private val collectionsClientFactory: CollectionsClientFactory
) : CollectionRepository {
    override fun get(collectionQuery: CollectionQuery): Collection {
        try {
            return CollectionResourceConverter.toCollection(
                collectionsClientFactory.getClient(collectionQuery.integrationId).getCollection(
                    collectionId = collectionQuery.collectionId,
                    projection = Projection.details
                )
            )
        } catch (exception: FeignException.NotFound) {
            throw ResourceNotFoundException("Collection with id ${collectionQuery.collectionId} not found")
        }
    }

    override fun getMyCollections(collectionsQuery: CollectionsQuery): List<Collection> {
        return collectionsClientFactory.getClient(collectionsQuery.integrationId)
            .getCollections(CollectionFilterRequest(projection = Projection.details))
            ._embedded.collections.map(CollectionResourceConverter::toCollection)
    }
}
