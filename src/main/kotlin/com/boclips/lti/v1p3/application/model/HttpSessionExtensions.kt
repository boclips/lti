package com.boclips.lti.v1p3.application.model

import com.boclips.lti.v1p3.application.exception.MissingSessionAttributeException
import com.boclips.lti.v1p3.domain.model.SessionKeys
import javax.servlet.http.HttpSession

fun HttpSession.mapStateToTargetLinkUri(state: String, targetLinkUri: String) {
    this.getMapAttribute(SessionKeys.statesToTargetLinkUris)[state] = targetLinkUri
}

fun HttpSession.getTargetLinkUri(state: String): String {
    return this.getMapAttribute(SessionKeys.statesToTargetLinkUris)[state]
        ?: throw MissingSessionAttributeException("state")
}

fun HttpSession.containsMappingForState(state: String): Boolean =
    this.getMapAttribute(SessionKeys.statesToTargetLinkUris).containsKey(state)

private fun HttpSession.getMapAttribute(value: String): MutableMap<String, String> {
    return this.getAttribute(value).let {
        if (it == null) {
            val newMap = serializableMapOf<String, String>()
            this.setAttribute(value, newMap)
            return@let newMap
        }
        it
    } as MutableMap<String, String>
}

private fun <K, V> serializableMapOf(): Map<K, V> = hashMapOf()

fun HttpSession.setIntegrationId(value: String) =
    this.setAttribute(com.boclips.lti.core.application.model.SessionKeys.integrationId, value)

fun HttpSession.getIntegrationId(): String =
    (this.getAttribute(com.boclips.lti.core.application.model.SessionKeys.integrationId)
        ?: throw MissingSessionAttributeException("integrationId")) as String
