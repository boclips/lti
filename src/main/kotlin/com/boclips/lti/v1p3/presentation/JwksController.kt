package com.boclips.lti.v1p3.presentation

import com.boclips.lti.v1p3.application.service.KeyPairService
import com.boclips.lti.v1p3.presentation.model.JsonWebKey
import com.boclips.lti.v1p3.presentation.model.JsonWebKeySet
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Base64

@RestController
class JwksController(private val keyPairService: KeyPairService) {
    @GetMapping(".well-known/jwks")
    fun getJwks(): JsonWebKeySet = JsonWebKeySet(keys = keyPairService.getAllKeyPairs().map { JsonWebKey(
        kty = it.publicKey.algorithm,
        kid = it.generationTimestamp.toString(),
        n = Base64.getUrlEncoder().encodeToString(it.publicKey.modulus.toByteArray()),
        e = Base64.getUrlEncoder().encodeToString(it.publicKey.publicExponent.toByteArray()),
        alg = "RSA256",
        use = "sig"
    ) })
}
