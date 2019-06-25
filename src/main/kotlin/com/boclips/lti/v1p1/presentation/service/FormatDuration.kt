package com.boclips.lti.v1p1.presentation.service

import org.springframework.stereotype.Service
import java.time.Duration

@Service
class FormatDuration {
    operator fun invoke(duration: Duration?): String {
        return when {
            duration == null || duration.isZero -> "0m"
            duration < Duration.ofMinutes(1) -> "${duration.seconds}s"
            else -> "${duration.toMinutes()}m"
        }
    }
}
