package com.boclips.lti.v1p1.domain.service

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI
import javax.servlet.http.HttpServletRequest

class RedirectToRequestedResource {
    operator fun invoke(request: HttpServletRequest): ResponseEntity<Unit> {
        val responseHeaders = HttpHeaders()
        responseHeaders.location = URI(request.requestURI.toString().replace("/v1p1", ""))
        return ResponseEntity(responseHeaders, HttpStatus.SEE_OTHER)
    }
}
