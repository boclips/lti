package com.boclips.lti.core.application.model

object SessionKeys {
    const val customLogo = "customLogo"
    const val userId = "userId"
    @Deprecated("""
        consumerKey is LTI 1.1 nomenclature, in the core package we should be in our own world.
        
        Use integrationId instead.
    """)
    const val consumerKey = "consumerKey"
    const val integrationId = "integrationId"
}
