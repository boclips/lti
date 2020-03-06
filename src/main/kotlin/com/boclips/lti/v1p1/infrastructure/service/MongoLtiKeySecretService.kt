package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.infrastructure.repository.LtiOnePointOneConsumerRepository
import org.imsglobal.aspect.LtiKeySecretService

class MongoLtiKeySecretService(private val ltiOnePointOneConsumerRepository: LtiOnePointOneConsumerRepository) :
    LtiKeySecretService {
    override fun getSecretForKey(key: String?): String? {
        return if (key == null) null else ltiOnePointOneConsumerRepository.findOneByKey(key)?.secret
    }
}
