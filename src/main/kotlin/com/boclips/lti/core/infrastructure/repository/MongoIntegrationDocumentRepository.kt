package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoIntegrationDocumentRepository : MongoRepository<IntegrationDocument, ObjectId> {
    fun findOneByIntegrationId(integrationId: String): IntegrationDocument?
}
