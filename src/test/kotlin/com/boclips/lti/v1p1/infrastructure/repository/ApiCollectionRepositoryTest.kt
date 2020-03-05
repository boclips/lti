package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.core.domain.model.CollectionRequest
import com.boclips.lti.core.domain.model.CollectionsRequest
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p1.testsupport.factories.CollectionResourceFactory
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
                    CollectionRequest(
                        collectionId = id,
                        integrationId = "integration-one"
                    )
                )
            ).isEqualTo(
                CollectionResourceConverter.toCollection(resource)
            )
        }

        @Test
        fun `throw a not found exception when client returns a 404`() {
            assertThatThrownBy {
                collectionRepository.get(
                    CollectionRequest(
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
                .getMyCollections(CollectionsRequest(integrationId = "integration-one"))

            assertThat(returnedCollections).containsExactlyInAnyOrder(
                CollectionResourceConverter.toCollection(firstResource),
                CollectionResourceConverter.toCollection(secondResource)
            )
        }
    }
}
