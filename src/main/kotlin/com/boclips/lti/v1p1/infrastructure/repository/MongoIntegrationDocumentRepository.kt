package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.infrastructure.model.IntegrationDocument
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoIntegrationDocumentRepository : MongoRepository<IntegrationDocument, ObjectId> {
    fun findOneByIntegrationId(integrationId: String): IntegrationDocument?
}
