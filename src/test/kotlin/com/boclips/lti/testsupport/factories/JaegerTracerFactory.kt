package com.boclips.lti.testsupport.factories

import io.jaegertracing.internal.JaegerTracer

object JaegerTracerFactory {

    fun createTracer() = JaegerTracer.Builder("serviceName").build()
}
