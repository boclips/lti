package com.boclips.lti.v1p1.infrastructure.service

import org.imsglobal.aspect.LtiKeySecretService
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Primary
@Service
class CompositeLtiKeySecretService(
    private val preconfiguredConsumerLtiKeySecretService: PreconfiguredConsumerLtiKeySecretService,
    private val mongoLtiKeySecretService: MongoLtiKeySecretService
) : LtiKeySecretService {
    override fun getSecretForKey(key: String?): String? {
        return mongoLtiKeySecretService.getSecretForKey(key)
            ?: preconfiguredConsumerLtiKeySecretService.getSecretForKey(key)
    }
}
