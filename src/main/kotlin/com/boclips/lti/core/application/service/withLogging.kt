package com.boclips.lti.core.application.service

import mu.KLogging

private object ExceptionLogger : KLogging()

fun <T> withLogging(lambda: () -> T): T {
    try {
        return lambda()
    } catch (e: Exception) {
        ExceptionLogger.logger.error { e }
        throw e
    }
}
