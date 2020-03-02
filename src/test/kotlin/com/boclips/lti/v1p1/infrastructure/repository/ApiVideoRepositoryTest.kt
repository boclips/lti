package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.v1p1.domain.model.VideoRequest
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p1.testsupport.factories.VideoResourcesFactory
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
private class ApiVideoRepositoryTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FetchingSpecificVideo {
        @Test
        fun `returns a domain object corresponding to returned resource`() {
            val id = "test-id"
            val resource = VideoResourcesFactory.sampleVideo(videoId = id)
            (videosClientFactory.getClient("integration-one") as VideosClientFake).add(resource)

            assertThat(videoRepository.get(VideoRequest(videoId = id, integrationId = "integration-one"))).isEqualTo(
                VideoResourceConverter.toVideo(resource)
            )
        }

        @Test
        fun `throws a not found error when requested video is not found`() {
            assertThatThrownBy { videoRepository.get(VideoRequest(videoId = "123", integrationId = "integration-one")) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Video with id 123 not found")
        }
    }
}
