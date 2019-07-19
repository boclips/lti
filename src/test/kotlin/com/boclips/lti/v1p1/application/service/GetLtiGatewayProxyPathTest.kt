package com.boclips.lti.v1p1.application.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetLtiGatewayProxyPathTest {
  @Test
  fun `prefixes the path with 'api'`() {
    val getLtiGatewayProxyPath = GetLtiGatewayProxyPath()
    assertThat(getLtiGatewayProxyPath("/some/url")).isEqualTo("/api/some/url")
  }
}
