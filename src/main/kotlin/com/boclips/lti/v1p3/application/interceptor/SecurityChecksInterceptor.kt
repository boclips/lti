package com.boclips.lti.v1p3.application.interceptor

import com.boclips.lti.v1p3.application.command.PerformSecurityChecks
import com.boclips.lti.v1p3.application.command.SetUpSession
import com.boclips.lti.v1p3.application.exception.ParamValidationException
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.domain.model.getBoclipsUserId
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

//that is one fat interceptor for now, just to present the idea
//Would be broken down into smaller ones
class SecurityChecksInterceptor(
    private val performSecurityChecks: PerformSecurityChecks,
    private val jwtService: JwtService,
    private val setUpSession: SetUpSession
    ): HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        //one of the disadvantages is retrieving params by method and not by annotations
        //another disadvantage is not using validation annotations, like @NotBlank
        val state: String = request.getParameter("state")
            ?: throw ParamValidationException("'state' parameter must not be blank")

        val idToken: String = request.getParameter("id_token")
            ?: throw ParamValidationException("'id_token' parameter must not be blank")

        val httpSession: HttpSession = request.session

        if (httpSession.getBoclipsUserId().isNullOrBlank()) {
            performSecurityChecks(state, idToken, httpSession)
        }

        val decodedToken = jwtService.decode(idToken)

        request.setAttribute("decodedToken", decodedToken)

        setUpSession(httpSession, decodedToken)

        return true
    }
}
