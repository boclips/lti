package com.boclips.lti.core.presentation.service

import com.boclips.lti.core.presentation.model.CollectionViewModel

class SortByCollectionTitle {
    val titlesWithNumbersRegex = "[^\\d]*(\\d+)[^\\d]*".toRegex()

    operator fun invoke(collections: List<CollectionViewModel>): List<CollectionViewModel> {
        val titlesWithNumbers: (CollectionViewModel) -> Boolean = { it.title.matches(titlesWithNumbersRegex) }

        return sortItemsWithNumbers(collections.filter(titlesWithNumbers)) +
            sortItemsWithoutNumbers(collections.filterNot(titlesWithNumbers))
    }

    private fun sortItemsWithNumbers(collections: List<CollectionViewModel>): List<CollectionViewModel> {
        return collections.sortedBy { titlesWithNumbersRegex.find(it.title)!!.groupValues.component2().toInt() }
    }

    private fun sortItemsWithoutNumbers(collections: List<CollectionViewModel>): List<CollectionViewModel> {
        return collections.sortedBy { it.title.toLowerCase() }
    }
}
