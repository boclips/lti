package com.boclips.lti.v1p3.infrastructure.repository

import com.boclips.lti.v1p3.domain.exception.PlatformNotFoundException
import com.boclips.lti.v1p3.domain.model.Platform
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import java.net.URL

class MongoPlatformRepository(private val documentRepository: MongoPlatformDocumentRepository) : PlatformRepository {
    override fun getByIssuer(issuer: URL): Platform {
        return documentRepository.findByIssuer(issuer.toString())
            ?.let { PlatformDocumentConverter.toDomainInstance(it) }
            ?: throw PlatformNotFoundException("Platform not found for issuer $issuer")
    }
}
