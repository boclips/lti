package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.application.model.SelectedVideo
import java.net.URL

object SelectedVideoFactory {
    fun sample(
        url: String = "http://sample-url.com",
        title: String = "Greatest video of all time",
        text: String = "just a description",
        type: String = "ltiResourceLink"
    ) = SelectedVideo(
        url = URL(url),
        title = title,
        text = text,
        type = type
    )
}
