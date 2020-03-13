package com.boclips.lti.core.presentation.service

import com.boclips.lti.core.presentation.model.CollectionViewModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SortByCollectionTitleTest {
    @Test
    fun `sorts collections alphabetically by title, ignoring case`() {
        val collections = listOf(
            CollectionViewModel("Doge", "", "", emptyList()),
            CollectionViewModel("apple", "", "", emptyList()),
            CollectionViewModel("car", "", "", emptyList()),
            CollectionViewModel("Air", "", "", emptyList()),
            CollectionViewModel("City", "", "", emptyList())
        )

        val sorted = sortByCollectionTitle(collections)

        assertThat(sorted)
            .extracting("title")
            .containsExactly("Air", "apple", "car", "City", "Doge")
    }

    @Test
    fun `sorts titles with numbers according to the number`() {
        val collections = listOf(
            CollectionViewModel(
                "Grade 11",
                "",
                "",
                emptyList()
            ),
            CollectionViewModel(
                "Grade 10",
                "",
                "",
                emptyList()
            ),
            CollectionViewModel(
                "Grade 9",
                "",
                "",
                emptyList()
            ),
            CollectionViewModel(
                "Grade 66",
                "",
                "",
                emptyList()
            ),
            CollectionViewModel(
                "Grade 1",
                "",
                "",
                emptyList()
            )
        )

        val sorted = sortByCollectionTitle(collections)

        assertThat(sorted)
            .extracting("title")
            .containsExactly("Grade 1", "Grade 9", "Grade 10", "Grade 11", "Grade 66")
    }

    @Test
    fun `places titles with numbers before other ones`() {
        val collections = listOf(
            CollectionViewModel(
                "Grade 11",
                "",
                "",
                emptyList()
            ),
            CollectionViewModel(
                "Grade 2",
                "",
                "",
                emptyList()
            ),
            CollectionViewModel(
                "Beagle",
                "",
                "",
                emptyList()
            ),
            CollectionViewModel("Apple", "", "", emptyList()),
            CollectionViewModel(
                "Grade 1",
                "",
                "",
                emptyList()
            )
        )

        val sorted = sortByCollectionTitle(collections)

        assertThat(sorted)
            .extracting("title")
            .containsExactly("Grade 1", "Grade 2", "Grade 11", "Apple", "Beagle")
    }

    val sortByCollectionTitle = SortByCollectionTitle()
}
