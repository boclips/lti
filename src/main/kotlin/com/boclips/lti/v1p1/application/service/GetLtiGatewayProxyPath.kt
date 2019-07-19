package com.boclips.lti.v1p1.application.service

import org.springframework.stereotype.Service

@Service
class GetLtiGatewayProxyPath {
    operator fun invoke(path: String) = "/api$path"
}
