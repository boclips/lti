package com.boclips.lti.v1p3.infrastructure.repository

import com.boclips.lti.v1p3.infrastructure.model.PlatformDocument
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoPlatformDocumentRepository : MongoRepository<PlatformDocument, ObjectId> {
    fun findByIssuer(issuer: String): PlatformDocument?
    fun findByClientId(clientId: String): PlatformDocument?
}
