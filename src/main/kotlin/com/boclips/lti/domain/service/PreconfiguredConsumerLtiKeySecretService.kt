package com.boclips.lti.domain.service

import com.boclips.lti.configuration.LtiContext
import org.imsglobal.aspect.LtiKeySecretService
import org.springframework.stereotype.Service

@Service
class PreconfiguredConsumerLtiKeySecretService : LtiKeySecretService {
    override fun getSecretForKey(key: String?): String {
        return LtiContext.CONSUMER_SECRET
    }
}
