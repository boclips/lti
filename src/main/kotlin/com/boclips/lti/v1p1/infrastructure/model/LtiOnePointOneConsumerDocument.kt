package com.boclips.lti.v1p1.infrastructure.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "ltiOnePointOneConsumers")
data class LtiOnePointOneConsumerDocument(
    @BsonId
    val id: ObjectId,
    val key: String,
    val secret: String
)
