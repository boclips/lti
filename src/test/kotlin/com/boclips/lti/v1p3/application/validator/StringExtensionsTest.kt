package com.boclips.lti.v1p3.application.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StringExtensionsTest {
    @Test
    fun `returns true for a valid URL string`() {
        assertThat("https://google.com".isURLString()).isEqualTo(true)
    }

    @Test
    fun `returns false for a string that is not a URL`() {
        assertThat("woogie boogie".isURLString()).isEqualTo(false)
    }
}
