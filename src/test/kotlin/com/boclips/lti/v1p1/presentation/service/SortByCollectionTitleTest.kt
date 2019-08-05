package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.presentation.model.CollectionMetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SortByCollectionTitleTest {
  @Test
  fun `sorts collections by title`() {
    val collections = listOf(
      CollectionMetadata("D", "", "", emptyList()),
      CollectionMetadata("a", "", "", emptyList()),
      CollectionMetadata("c2", "", "", emptyList()),
      CollectionMetadata("A", "", "", emptyList()),
      CollectionMetadata("c1", "", "", emptyList()),
      CollectionMetadata("z", "", "", emptyList())
    )

    val sorted = sortByCollectionTitle(collections)

    assertThat(sorted)
      .extracting("title")
      .containsExactly("a", "A", "c1", "c2", "D", "z")
  }

  val sortByCollectionTitle = SortByCollectionTitle()
}
