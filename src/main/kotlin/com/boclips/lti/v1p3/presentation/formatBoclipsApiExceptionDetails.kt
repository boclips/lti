package com.boclips.lti.v1p3.presentation

import com.boclips.web.exceptions.BoclipsApiException

fun formatBoclipsApiExceptionDetails(ex: BoclipsApiException): String {
    return "${formatError(ex.exceptionDetails.error)} (response status ${ex.exceptionDetails.status.value()}): ${
        formatDetailedMessage(
            ex.exceptionDetails.message
        )
    }"
}

private fun formatDetailedMessage(message: String): String {
    return if (message == "") "<message unavailable>" else message
}

private fun formatError(error: String): String {
    return if (error == "") "<error unavailable>" else error
}
