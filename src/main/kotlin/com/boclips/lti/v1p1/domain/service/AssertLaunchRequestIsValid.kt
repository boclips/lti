package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.domain.exception.LaunchRequestInvalidException
import mu.KLogging
import org.imsglobal.lti.launch.LtiVerificationResult

class AssertLaunchRequestIsValid {
    companion object : KLogging()

    operator fun invoke(result: LtiVerificationResult) {
        if (!result.success) {
            throw LaunchRequestInvalidException("LTI launch verification failed: ${result.error} because of ${result.message}")
        }
        if (result.ltiLaunchResult.resourceLinkId?.isNotBlank() != true) {
            throw LaunchRequestInvalidException("LTI resource link id was not provided")
        }
    }
}
