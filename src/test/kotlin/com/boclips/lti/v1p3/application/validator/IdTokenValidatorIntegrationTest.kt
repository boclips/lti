package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.application.exception.JwtClaimValidationException
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.Instant.now

class IdTokenValidatorIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `does not throw when valid token is provided`() {
        val validToken = DecodedJwtTokenFactory.sample(
            issuerClaim = testIssuer,
            audienceClaim = listOf(testClientId)
        )
        assertDoesNotThrow { validator.assertHasValidClaims(validToken) }
    }

    @Nested
    inner class Nonce {
        @Test
        fun `throws a validation error when nonce is empty`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                nonceClaim = ""
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("nonce")
        }

        @Test
        fun `throws a validation error when nonce is blank`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                nonceClaim = "  "
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("nonce")
        }

        @Test
        fun `throws a validation error when nonce is not provided on the token`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                nonceClaim = null
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("nonce")
        }
    }

    @Nested
    inner class Issuer {
        @Test
        fun `throws an error when the issuer is not provided`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = null,
                deploymentIdClaim = "deployment-id",
                subjectClaim = "external-user-id",
                audienceClaim = listOf(testClientId)
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iss")
        }

        @Test
        fun `throws an error when the issuer is empty`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "",
                deploymentIdClaim = "deployment-id",
                subjectClaim = "external-user-id",
                audienceClaim = listOf(testClientId)
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iss")
        }

        @Test
        fun `throws an error when the issuer is blank`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "   ",
                deploymentIdClaim = "deployment-id",
                subjectClaim = "external-user-id",
                audienceClaim = listOf(testClientId)
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iss")
        }
    }

    @Nested
    inner class DeploymentId {
        @Test
        fun `throws an error when deploymentId is not provided`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "issuer",
                deploymentIdClaim = null,
                subjectClaim = "external-user-id",
                audienceClaim = listOf(testClientId)
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("Deployment ID")
        }

        @Test
        fun `throws an error when the deploymentId is empty`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "issuer",
                deploymentIdClaim = "",
                subjectClaim = "external-user-id",
                audienceClaim = listOf(testClientId)
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("Deployment ID")
        }

        @Test
        fun `throws an error when the deploymentId is blank`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "isser",
                deploymentIdClaim = "   ",
                subjectClaim = "external-user-id",
                audienceClaim = listOf(testClientId)
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("Deployment ID")
        }
    }

    @Nested
    inner class Subject {
        @Test
        fun `throws an error when subject is not provided`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "issuer",
                deploymentIdClaim = "deployment-id",
                subjectClaim = null,
                audienceClaim = listOf(testClientId)
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("Subject")
        }

        @Test
        fun `throws an error when the subject is empty`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "issuer",
                deploymentIdClaim = "deployment-id",
                subjectClaim = "",
                audienceClaim = listOf(testClientId)
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("Subject")
        }

        @Test
        fun `throws an error when the subject is blank`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "isser",
                deploymentIdClaim = "deployment-id",
                subjectClaim = "   ",
                audienceClaim = listOf(testClientId)
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("Subject")
        }
    }

    @Nested
    inner class AudienceAndAuthorizedParty {
        @Test
        fun `throws a validation error when audience is not provided on the token`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "https://platform.com",
                audienceClaim = null
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("aud")
        }

        @Test
        fun `throw an error when audience array is empty`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "https://platform.com",
                audienceClaim = emptyList()
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("aud")
        }

        @Test
        fun `throw an error when audience array does not contain platform's clientId`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "https://platform.com",
                audienceClaim = listOf("hello", "hi")
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("aud")
        }

        @Test
        fun `throws an error if aud contains multiple incorrect values and azp does not equal platform clientId`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "https://platform.com",
                audienceClaim = listOf("boclips", "boclaps"),
                authorizedPartyClaim = "hehehe"
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("azp")
        }

        @Test
        fun `does not throw if aud contains one incorrect value and azp equals platform clientId`() {
            val clientId = "correct-id"
            mongoPlatformDocumentRepository.insert(
                PlatformDocumentFactory.sample(
                    issuer = "https://another-platform.com",
                    authenticationEndpoint = "https://another-platform.com/auth",
                    clientId = clientId
                )
            )

            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "https://another-platform.com",
                audienceClaim = listOf("wrong"),
                authorizedPartyClaim = clientId
            )
            assertDoesNotThrow { validator.assertHasValidClaims(token) }
        }

        @Test
        fun `throws an error if aud has a single value and azp is present, but does not equal platform's client id`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = "https://platform.com",
                audienceClaim = listOf("boclips"),
                authorizedPartyClaim = "hehehe"
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("azp")
        }
    }

    @Nested
    inner class TimeConstraints {
        @Test
        fun `throws an error if exp claim is not available`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                expClaim = null
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("exp")
        }

        @Test
        fun `throws an error if current time is after the value defined by exp claim`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                expClaim = now().minusSeconds(10).epochSecond
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("exp")
        }

        @Test
        fun `throws an error if the iat claim is null`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                issuedAtClaim = null
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iat")
        }

        @Test
        fun `throws an error if the iat claim is in the future`() {
            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                issuedAtClaim = now().plusSeconds(10).epochSecond
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iat")
        }

        @Test
        fun `throws an error if iat is too far in the past`() {
            validator = IdTokenValidator(
                platformRepository = platformRepository,
                maxTokenAgeInSeconds = 60,
                currentTime = { Instant.ofEpochSecond(1500000061) }
            )

            val token = DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                issuedAtClaim = 1500000000
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iat")
        }
    }

    private lateinit var validator: IdTokenValidator

    @Autowired
    private lateinit var platformRepository: PlatformRepository

    @BeforeEach
    fun initialise() {
        validator = IdTokenValidator(platformRepository = platformRepository, maxTokenAgeInSeconds = 60)
    }

    private val testIssuer = "https://platform.com"
    private val testClientId = "client-id"

    @BeforeEach
    fun insertPlatform() {
        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = testIssuer,
                authenticationEndpoint = "https://platform.com/auth",
                clientId = testClientId
            )
        )
    }
}
