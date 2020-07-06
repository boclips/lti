package com.boclips.lti.v1p3.application.command

import com.boclips.lti.v1p3.application.exception.UnknownPlatformException
import com.boclips.lti.v1p3.domain.exception.PlatformNotFoundException
import com.boclips.lti.v1p3.domain.model.Platform
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import java.net.MalformedURLException
import java.net.URL

class GetPlatformForIntegration(private val platformRepository: PlatformRepository) {
    operator fun invoke(integrationId: String): Platform {
        return try {
            platformRepository.getByIssuer(URL(integrationId))
        } catch (e: PlatformNotFoundException) {
            throw UnknownPlatformException(integrationId)
        } catch (e: MalformedURLException) {
            throw UnknownPlatformException(integrationId)
        }
    }
}
