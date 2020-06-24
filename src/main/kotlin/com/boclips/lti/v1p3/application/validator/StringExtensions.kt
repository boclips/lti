package com.boclips.lti.v1p3.application.validator

import java.net.URL

fun String.isURLString(): Boolean {
    return try {
        URL(this)
        true
    } catch (_: Exception) {
        return false
    }
}
