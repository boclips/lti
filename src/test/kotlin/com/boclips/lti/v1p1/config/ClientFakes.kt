package com.boclips.lti.v1p1.config

import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
class ClientFakes {
    @Bean
    fun videosClient(): VideosClientFake {
        return VideosClientFake()
    }

    @Bean
    fun collectionsClient(): CollectionsClientFake {
        return CollectionsClientFake()
    }
}
