package com.boclips.lti.v1p1.testsupport

import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.client.RedirectStrategy
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.protocol.HttpContext

object DoNotFollowRedirectStrategy : RedirectStrategy {
    override fun isRedirected(request: HttpRequest?, response: HttpResponse?, context: HttpContext?): Boolean {
        return false
    }

    override fun getRedirect(request: HttpRequest?, response: HttpResponse?, context: HttpContext?): HttpUriRequest {
        throw IllegalStateException("This shouldn't be called ever")
    }
}
