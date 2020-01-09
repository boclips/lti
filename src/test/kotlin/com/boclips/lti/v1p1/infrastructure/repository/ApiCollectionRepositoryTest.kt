package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p1.testsupport.factories.CollectionResourceFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration

@ExtendWith(MockitoExtension::class)
@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class])
class ApiCollectionRepositoryTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FetchingSpecificCollection {
        @Test
        fun `returns a domain object which corresponds to requested resource`() {
            val id = "test-id"
            val resource = CollectionResourceFactory.sample(id = id)
            collectionsClient.add(resource)

            assertThat(collectionRepository.get(id)).isEqualTo(CollectionResourceConverter.toCollection(resource))
        }

        @Test
        fun `throw a not found exception when client returns a 404`() {
            assertThatThrownBy { collectionRepository.get("123") }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Collection with id 123 not found")
        }
    }

    @Nested
    inner class FetchingMyCollections {
        @Test
        fun `returns domain objects which correspond returned resources`() {
            val firstResource = CollectionResourceFactory.sample()
            collectionsClient.add(firstResource)
            val secondResource = CollectionResourceFactory.sample()
            collectionsClient.add(secondResource)

            assertThat(collectionRepository.getMyCollections()).containsExactlyInAnyOrder(
                CollectionResourceConverter.toCollection(firstResource),
                CollectionResourceConverter.toCollection(secondResource)
            )
        }
    }
}
