package com.boclips.lti.v1p3.infrastructure.repository

import com.boclips.lti.v1p3.infrastructure.model.NonceDocument
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

interface MongoNonceDocumentRepository : MongoRepository<NonceDocument, ObjectId> {
    fun findOneByValue(value: String): NonceDocument?
    fun countByValueAndCreatedAtAfter(value: String, timestamp: Instant): Long
}
