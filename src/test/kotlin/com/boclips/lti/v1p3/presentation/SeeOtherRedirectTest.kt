package com.boclips.lti.v1p3.presentation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.net.URI
import java.net.URL

class SeeOtherRedirectTest {
    @Test
    fun `has a 303 response status`() {
        val redirect = seeOtherRedirect(URL("https://google.com"))

        assertThat(redirect.statusCode).isEqualTo(HttpStatus.SEE_OTHER)
    }

    @Test
    fun `sets the location to given URL`() {
        val redirect = seeOtherRedirect(URL("https://google.com"))

        assertThat(redirect.headers.location).isEqualTo(URI("https://google.com"))
    }
}
