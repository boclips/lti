package com.boclips.lti.v1p3.presentation

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URL

fun seeOtherRedirect(url: URL) =
    ResponseEntity<Nothing>(
        HttpHeaders().apply { location = url.toURI() },
        HttpStatus.SEE_OTHER
    )
