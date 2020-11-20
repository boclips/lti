package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.core.domain.exception.ResourceNotFoundException
import com.boclips.lti.core.domain.model.CollectionQuery
import com.boclips.lti.core.domain.model.CollectionsQuery
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.CollectionResourceFactory
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ApiCollectionRepositoryTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FetchingSpecificCollection {
        @Test
        fun `returns a domain object which corresponds to requested resource`() {
            val id = "test-id"
            val resource = CollectionResourceFactory.sample(id = id)
            (collectionsClientFactory.getClient("integration-one") as CollectionsClientFake).add(resource)

            assertThat(
                collectionRepository.get(
                    CollectionQuery(
                        collectionId = id,
                        integrationId = "integration-one"
                    )
                )
            ).isEqualTo(
                CollectionResourceConverter.toCollection(
                    resource
                )
            )
        }

        @Test
        fun `throw a not found exception when client returns a 404`() {
            assertThatThrownBy {
                collectionRepository.get(
                    CollectionQuery(
                        collectionId = "123",
                        integrationId = "integration-one"
                    )
                )
            }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Collection with id 123 not found")
        }
    }

    @Nested
    inner class FetchingMyCollections {
        @Test
        fun `returns domain objects which correspond returned resources`() {
            val firstResource = CollectionResourceFactory.sample()
            (collectionsClientFactory.getClient("integration-one") as CollectionsClientFake).add(firstResource)
            val secondResource = CollectionResourceFactory.sample()
            (collectionsClientFactory.getClient("integration-one") as CollectionsClientFake).add(secondResource)

            val returnedCollections = collectionRepository
                .getMyCollections(CollectionsQuery(integrationId = "integration-one"))

            assertThat(returnedCollections).containsExactlyInAnyOrder(
                CollectionResourceConverter.toCollection(
                    firstResource
                ),
                CollectionResourceConverter.toCollection(
                    secondResource
                )
            )
        }
    }
}
