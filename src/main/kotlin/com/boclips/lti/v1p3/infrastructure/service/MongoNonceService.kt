package com.boclips.lti.v1p3.infrastructure.service

import com.boclips.lti.v1p3.application.service.NonceService
import com.boclips.lti.v1p3.infrastructure.model.NonceDocument
import com.boclips.lti.v1p3.infrastructure.repository.MongoNonceDocumentRepository
import org.bson.types.ObjectId
import java.time.Instant.now
import java.time.temporal.ChronoUnit

class MongoNonceService(
    private val nonceDocumentRepository: MongoNonceDocumentRepository,
    private val nonceLifetimeInHours: Long
) : NonceService {
    override fun hasNonceBeenUsedAlready(value: String): Boolean {
        val countOfNoncesYoungerThanNonceLifetime = nonceDocumentRepository.countByValueAndCreatedAtAfter(
            value,
            now().minus(nonceLifetimeInHours, ChronoUnit.HOURS)
        )
        return countOfNoncesYoungerThanNonceLifetime > 0L
    }

    override fun storeNonce(value: String) {
        nonceDocumentRepository.insert(NonceDocument(id = ObjectId(), value = value, createdAt = now()))
    }
}
