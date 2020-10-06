package com.boclips.lti.v1p3.application.command


import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.boclips.lti.testsupport.factories.PlatformFactory
import com.boclips.lti.v1p3.domain.exception.PlatformNotFoundException
import com.boclips.lti.v1p3.domain.model.getBoclipsUserId
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import com.boclips.lti.v1p3.domain.service.SynchroniseUser
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.net.URL

class SetUpSessionTest {

    private lateinit var synchroniseUser: SynchroniseUser
    private lateinit var platformRepository: PlatformRepository

    private lateinit var setUpSession: SetUpSession

    @BeforeEach
    fun setup() {
        synchroniseUser = mock()
        platformRepository = mock()
        `when`(synchroniseUser(any(), any(), any())).doReturn("internal-boclips-id")
        `when`(platformRepository.getByIssuer(any())).doReturn(
            PlatformFactory.sample(issuer = URL("https://platform.com"), clientId = "a-client-id")
        )
        `when`(platformRepository.getByIssuer(URL("https://www.doesn't-exist.com"))).thenThrow(PlatformNotFoundException("blah"))
        setUpSession = SetUpSession(synchroniseUser, platformRepository)
    }

    @Test
    fun `successfully sets boclips user id and integration id`() {
        val token = DecodedJwtTokenFactory.sample(
            issuerClaim = "https://platform.com",
            subjectClaim = "this-stores-external-user-id"
        )
        val session = LtiTestSessionFactory.unauthenticated()

        setUpSession(session, token)

        assertThat(session.getBoclipsUserId()).isEqualTo("internal-boclips-id")
        assertThat(session.getAttribute(SessionKeys.integrationId)).isEqualTo("https://platform.com")
    }

    @Test
    fun `does not synchronise user when not necessary`() {
        val token = DecodedJwtTokenFactory.sample(
            issuerClaim = "https://www.issuer.com",
            subjectClaim = "this-stores-external-user-id"
        )
        val session = LtiTestSessionFactory.authenticated(
            integrationId = "integ-id",
            mapOf("boclipsUserId" to "previously-stored-internal-boclips-id")
        )

        setUpSession(session, token)

        Mockito.verifyNoInteractions(synchroniseUser)
        assertThat(session.getBoclipsUserId()).isEqualTo("previously-stored-internal-boclips-id")
        assertThat(session.getAttribute(SessionKeys.integrationId)).isEqualTo("https://platform.com")
    }

    @Test
    fun `throw an exception if given issuer does not exist on our side`() {
        val token = DecodedJwtTokenFactory.sample(
            issuerClaim = "https://www.doesn't-exist.com",
            subjectClaim = "this-stores-external-user-id"
        )
        val session = LtiTestSessionFactory.authenticated(
            integrationId = "integ-id",
            mapOf("boclipsUserId" to "previously-stored-internal-boclips-id")
        )
        assertThrows<PlatformNotFoundException> {
            setUpSession(session, token)
        }
    }


}


