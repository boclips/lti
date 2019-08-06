package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.presentation.model.CollectionMetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SortByCollectionTitleTest {
    @Test
    fun `sorts collections alphabetically by title, ignoring case`() {
        val collections = listOf(
            CollectionMetadata("Doge", "", "", emptyList()),
            CollectionMetadata("apple", "", "", emptyList()),
            CollectionMetadata("car", "", "", emptyList()),
            CollectionMetadata("Air", "", "", emptyList()),
            CollectionMetadata("City", "", "", emptyList())
        )

        val sorted = sortByCollectionTitle(collections)

        assertThat(sorted)
            .extracting("title")
            .containsExactly("Air", "apple", "car", "City", "Doge")
    }

    @Test
    fun `sorts titles with numbers according to the number`() {
        val collections = listOf(
            CollectionMetadata("Grade 11", "", "", emptyList()),
            CollectionMetadata("Grade 10", "", "", emptyList()),
            CollectionMetadata("Grade 9", "", "", emptyList()),
            CollectionMetadata("Grade 66", "", "", emptyList()),
            CollectionMetadata("Grade 1", "", "", emptyList())
        )

        val sorted = sortByCollectionTitle(collections)

        assertThat(sorted)
            .extracting("title")
            .containsExactly("Grade 1", "Grade 9", "Grade 10", "Grade 11", "Grade 66")
    }

    @Test
    fun `places titles with numbers before other ones`() {
        val collections = listOf(
            CollectionMetadata("Grade 11", "", "", emptyList()),
            CollectionMetadata("Grade 2", "", "", emptyList()),
            CollectionMetadata("Beagle", "", "", emptyList()),
            CollectionMetadata("Apple", "", "", emptyList()),
            CollectionMetadata("Grade 1", "", "", emptyList())
        )

        val sorted = sortByCollectionTitle(collections)

        assertThat(sorted)
            .extracting("title")
            .containsExactly("Grade 1", "Grade 2", "Grade 11", "Apple", "Beagle")
    }

    val sortByCollectionTitle = SortByCollectionTitle()
}
