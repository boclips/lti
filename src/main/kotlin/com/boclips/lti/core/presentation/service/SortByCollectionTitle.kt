package com.boclips.lti.core.presentation.service

import com.boclips.lti.core.presentation.model.CollectionMetadata

class SortByCollectionTitle {
    val titlesWithNumbersRegex = "[^\\d]*(\\d+)[^\\d]*".toRegex()

    operator fun invoke(collections: List<CollectionMetadata>): List<CollectionMetadata> {
        val titlesWithNumbers: (CollectionMetadata) -> Boolean = { it.title.matches(titlesWithNumbersRegex) }

        return sortItemsWithNumbers(collections.filter(titlesWithNumbers)) +
            sortItemsWithoutNumbers(collections.filterNot(titlesWithNumbers))
    }

    private fun sortItemsWithNumbers(collections: List<CollectionMetadata>): List<CollectionMetadata> {
        return collections.sortedBy { titlesWithNumbersRegex.find(it.title)!!.groupValues.component2().toInt() }
    }

    private fun sortItemsWithoutNumbers(collections: List<CollectionMetadata>): List<CollectionMetadata> {
        return collections.sortedBy { it.title.toLowerCase() }
    }
}
