package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.core.domain.exception.ResourceNotFoundException
import com.boclips.lti.core.domain.model.VideoRequest
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.VideoResourcesFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private class ApiVideoRepositoryTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FetchingSpecificVideo {
        @Test
        fun `returns a domain object corresponding to returned resource`() {
            val id = "test-id"
            val resource = VideoResourcesFactory.sampleVideo(videoId = id)
            saveVideo(resource, "integration-one")

            assertThat(
                videoRepository.get(
                    VideoRequest(
                        videoId = id,
                        integrationId = "integration-one"
                    )
                )
            ).isEqualTo(
                VideoResourceConverter.toVideo(
                    resource
                )
            )
        }

        @Test
        fun `throws a not found error when requested video is not found`() {
            assertThatThrownBy {
                videoRepository.get(
                    VideoRequest(
                        videoId = "123",
                        integrationId = "integration-one"
                    )
                )
            }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Video with id 123 not found")
        }
    }
}
