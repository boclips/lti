package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.configuration.properties.ServiceUrls
import org.springframework.cloud.gateway.mvc.ProxyExchange
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LtiGateway(val serviceUrls: ServiceUrls) {
    @GetMapping("/api/**")
    fun proxy(
        proxy: ProxyExchange<ByteArray>
    ): ResponseEntity<*> {
        return proxy.uri("${serviceUrls.apiUrl}${proxy.path().substringAfter("/api")}").get()
    }
}
