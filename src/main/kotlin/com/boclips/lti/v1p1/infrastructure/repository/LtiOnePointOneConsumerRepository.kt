package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.infrastructure.model.LtiOnePointOneConsumerDocument
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface LtiOnePointOneConsumerRepository : MongoRepository<LtiOnePointOneConsumerDocument, ObjectId> {
    fun findOneByKey(key: String): LtiOnePointOneConsumerDocument?
}
