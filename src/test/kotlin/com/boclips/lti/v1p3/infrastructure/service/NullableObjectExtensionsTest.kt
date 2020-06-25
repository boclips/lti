package com.boclips.lti.v1p3.infrastructure.service

import com.boclips.lti.v1p3.infrastructure.exception.InvalidJsonDataTypeException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NullableObjectExtensionsTest {
    @Nested
    inner class ToNullableListOfStrings {
        @Test
        fun `returns null when the object is null`() {
            assertThat(null.toNullableListOfStrings()).isNull()
        }

        @Test
        fun `throws when invoked on a non-list instance`() {
            assertThatThrownBy { 123.toNullableListOfStrings() }
                .isInstanceOf(InvalidJsonDataTypeException::class.java)
        }

        @Test
        fun `throws when invoked on a list of non-string values`() {
            assertThatThrownBy { listOf(123, Object(), "hello").toNullableListOfStrings() }
                .isInstanceOf(InvalidJsonDataTypeException::class.java)
        }

        @Test
        fun `returns content as a list of strings when invoked on a list of strings`() {
            assertThat(listOf("a", "b", "c").toNullableListOfStrings()).containsExactlyInAnyOrder("a", "b", "c")
        }
    }
}
