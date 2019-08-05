package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.presentation.model.CollectionMetadata
import org.springframework.stereotype.Service

@Service
class SortByCollectionTitle {
    operator fun invoke(collections: List<CollectionMetadata>): List<CollectionMetadata> {
        return collections.sortedBy { it.title.toLowerCase() }
    }
}
