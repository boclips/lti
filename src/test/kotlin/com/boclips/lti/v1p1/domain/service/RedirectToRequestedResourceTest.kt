package com.boclips.lti.v1p1.domain.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import javax.servlet.http.HttpServletRequest

internal class RedirectToRequestedResourceTest {
    @Test
    fun `returns a See Other (303) redirect`() {
        val request = mock<HttpServletRequest> {
            on { requestURI } doReturn "https://lti.endpoint.com/v1p1/videos/1234"
        }

        val response: ResponseEntity<Unit> = redirectToRequestedResource(request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.SEE_OTHER)
    }

    @Test
    fun `'Location' header is set to the resource outside the LTI 1p1 context`() {
        val launchUri = "https://lti.endpoint.com/v1p1/videos/1234"
        val resourceUri = "https://lti.endpoint.com/videos/1234"
        val request = mock<HttpServletRequest> {
            on { requestURI } doReturn launchUri
        }

        val response: ResponseEntity<Unit> = redirectToRequestedResource(request)

        assertThat(response.headers.location.toString()).isEqualTo(resourceUri)
    }

    val redirectToRequestedResource = RedirectToRequestedResource()
}
