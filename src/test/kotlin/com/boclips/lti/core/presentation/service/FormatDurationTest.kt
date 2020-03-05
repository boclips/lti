package com.boclips.lti.core.presentation.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration

class FormatDurationTest {
  @Test
  fun `returns 0m when given a null`() {
    assertThat(formatDuration(null)).isEqualTo("0m")
  }

  @Test
  fun `returns 0m when given a value of 0`() {
    assertThat(formatDuration(Duration.ZERO)).isEqualTo("0m")
  }

  @Test
  fun `returns a value in seconds when given a positive value below one minute`() {
    assertThat(formatDuration(Duration.ofSeconds(11))).isEqualTo("11s")
  }

  @Test
  fun `returns 1m when given a value of one minute`() {
    assertThat(formatDuration(Duration.ofMinutes(1))).isEqualTo("1m")
  }

  @Test
  fun `returns 120m when given a value of 2 hours`() {
    assertThat(formatDuration(Duration.ofHours(2))).isEqualTo("120m")
  }

  val formatDuration = FormatDuration()
}
