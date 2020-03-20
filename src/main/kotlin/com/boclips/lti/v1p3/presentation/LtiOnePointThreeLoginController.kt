package com.boclips.lti.v1p3.presentation

import org.hibernate.validator.constraints.URL
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@Controller
@RequestMapping("/v1p3/login")
class LtiOnePointThreeLoginController {
    @PostMapping
    fun login(
        @NotNull @URL @RequestParam("iss") issuer: String?,
        @NotBlank @RequestParam("login_hint") loginHint: String?,
        @NotNull @URL @RequestParam("target_link_uri") targetLinkUri: String?
    ): String {
        return "redirect:https://rafal.lewandowski"
    }
}
