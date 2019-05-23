package com.boclips.lti.domain.service

import com.boclips.lti.configuration.properties.LtiProperties
import org.imsglobal.aspect.LtiKeySecretService
import org.springframework.stereotype.Service

@Service
class PreconfiguredConsumerLtiKeySecretService(val ltiProperties: LtiProperties) : LtiKeySecretService {
    override fun getSecretForKey(key: String?): String? {
        return if (key.equals(ltiProperties.consumer.key)) {
            ltiProperties.consumer.secret
        } else {
            null
        }
    }
}
