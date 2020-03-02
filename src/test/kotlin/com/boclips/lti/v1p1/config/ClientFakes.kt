package com.boclips.lti.v1p1.config

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.infrastructure.service.CollectionsClientFactory
import com.boclips.lti.v1p1.infrastructure.service.VideosClientFactory
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
class ClientFakes {
    @Bean
    fun videosClientFactory(ltiProperties: LtiProperties): VideosClientFactory {
        return VideosClientFactory(VideosClientFake(), ltiProperties)
    }

    @Bean
    fun collectionsClientFactory(ltiProperties: LtiProperties): CollectionsClientFactory {
        return CollectionsClientFactory(CollectionsClientFake(), ltiProperties)
    }
}
