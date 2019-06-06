package com.boclips.lti.v1p1.domain.service

import mu.KLogging
import org.imsglobal.lti.launch.LtiVerificationResult
import org.springframework.stereotype.Service

@Service
class IsLaunchRequestValid {
    companion object : KLogging()

    operator fun invoke(result: LtiVerificationResult): Boolean {
        if (!result.success) {
            logger.info { "LTI launch verification failed: ${result.error}" }
            return false
        }
        if (result.ltiLaunchResult.resourceLinkId?.isNotBlank() != true) {
            logger.info { "LTI resource link id was not provided" }
            return false
        }

        return true
    }
}
