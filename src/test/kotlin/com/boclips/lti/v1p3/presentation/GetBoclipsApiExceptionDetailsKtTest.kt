package com.boclips.lti.v1p3.presentation

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GetBoclipsApiExceptionDetailsKtTest {
    class TestBoclipsApiException(details: ExceptionDetails) : BoclipsApiException(details)

    @Test
    fun `displays the message passed in exception details`() {
        val ex = TestBoclipsApiException(
            ExceptionDetails(
                error = "",
                message = "something bad happened",
            )
        )

        val formattedExceptionDetails = formatBoclipsApiExceptionDetails(ex)

        assertThat(formattedExceptionDetails).contains("something bad happened")
    }

    @Test
    fun `displays a text if detailed message is blank`() {
        val ex = TestBoclipsApiException(
            ExceptionDetails(
                error = "",
                message = "",
            )
        )

        val formattedExceptionDetails = formatBoclipsApiExceptionDetails(ex)

        assertThat(formattedExceptionDetails).contains("<message unavailable>")
    }

    @Test
    fun `displays the error passed in exception details`() {
        val ex = TestBoclipsApiException(
            ExceptionDetails(
                error = "BadError",
                message = "",
            )
        )

        val formattedExceptionDetails = formatBoclipsApiExceptionDetails(ex)

        assertThat(formattedExceptionDetails).contains("BadError")
    }

    @Test
    fun `displays a text if error is blank`() {
        val ex = TestBoclipsApiException(
            ExceptionDetails(
                error = "",
                message = "",
            )
        )

        val formattedExceptionDetails = formatBoclipsApiExceptionDetails(ex)

        assertThat(formattedExceptionDetails).contains("<error unavailable>")
    }

    @Test
    fun `displays the status code passed in exception details`() {
        val ex = TestBoclipsApiException(
            ExceptionDetails(
                error = "",
                message = "",
                status = HttpStatus.UNPROCESSABLE_ENTITY
            )
        )

        val formattedExceptionDetails = formatBoclipsApiExceptionDetails(ex)

        assertThat(formattedExceptionDetails).contains("422")
    }
}
