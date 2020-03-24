package com.boclips.lti.core.infrastructure.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "integrations")
data class IntegrationDocument(
    @Id
    val id: ObjectId,
    @Indexed
    val integrationId: String,
    val clientId: String,
    val clientSecret: String
)
