package com.boclips.lti.v1p3.infrastructure.service

import com.boclips.lti.v1p3.infrastructure.exception.InvalidJsonDataTypeException

fun Any?.toNullableListOfStrings(): List<String>? {
    return when {
        this == null -> null
        this is List<*> -> this.map { if (it is String) it else throw InvalidJsonDataTypeException(this) }
        else -> throw InvalidJsonDataTypeException(this)
    }
}
