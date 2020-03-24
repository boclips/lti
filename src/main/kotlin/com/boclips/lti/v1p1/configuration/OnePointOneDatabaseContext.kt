package com.boclips.lti.v1p1.configuration

import com.boclips.lti.v1p1.infrastructure.model.LtiOnePointOneConsumerDocument
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@Configuration
class OnePointOneDatabaseContext(
    private val mongoTemplate: MongoTemplate
) {
    @EventListener(ApplicationReadyEvent::class)
    fun initIndicesAfterStartup() {
        mongoTemplate.indexOps(LtiOnePointOneConsumerDocument::class.java).ensureIndex(
            Index().on(
                LtiOnePointOneConsumerDocument::class.java.getDeclaredField("key").name,
                Sort.Direction.ASC
            )
        )
    }
}
