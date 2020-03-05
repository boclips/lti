package com.boclips.lti.core.infrastructure.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "integrations")
data class IntegrationDocument(
    @BsonId
    val id: ObjectId,
    val integrationId: String,
    val clientId: String,
    val clientSecret: String
)
