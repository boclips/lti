package com.boclips.lti.core.presentation.service

import java.time.Duration

class FormatDuration {
    operator fun invoke(duration: Duration?): String {
        return when {
            duration == null || duration.isZero -> "0m"
            duration < Duration.ofMinutes(1) -> "${duration.seconds}s"
            else -> "${duration.toMinutes()}m"
        }
    }
}
