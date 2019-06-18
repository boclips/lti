package com.boclips.lti.v1p1.configuration

import com.boclips.videos.service.client.spring.EnableVideoServiceClient
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@EnableVideoServiceClient
@Configuration
class VideoServiceClientConfig
