package com.boclips.lti.v1p3.infrastructure.exception

class InvalidJsonDataTypeException(obj: Any) : RuntimeException("Expect a list of strings, but got <$obj>")
