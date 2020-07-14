package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.configuration.properties.RsaKeyPairProperties
import java.time.Instant

object RsaKeyPairPropertiesFactory {
    fun sample(
        generationTimestamp: Long = Instant.now().epochSecond,
        privateKey: String = """
            -----BEGIN PRIVATE KEY-----
            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
            bvIX/uplHEUG/6Qv0nZJGiX0EH5t
            -----END PRIVATE KEY-----
        """.trimIndent(),
        publicKey: String = """
            -----BEGIN PUBLIC KEY-----
            MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA6mFxLbioZLCDNO5euRPa
            ytm8UdEoNacw6s1DNZZ+CqHy32Eaj8S+z2XlKRO4Evj8y1XTTp2+xCaUJuW7efFP
            Yysx7OPXqKuIJeqV0b5qlX2RtSlLAFq/xFeVobZmyFBt23c60iov2iSxcXy30ikE
            iaSGuYjQ96LJ6J1XAY7Tt9uj6R9MAHJb6DoWEbbYsx0hwz+1PcNSyuhPbpfODt67
            pFU1NKIRC2ikOc7fJKlJBCjo8N0o0Ca7DkNJUc2sFasR1Ep1b64/t3nrdnmu/o9+
            un5Y/Y++/RQr1Mv1xmdMKtDVhXyon1HnY8soom+cYvnh8Mw5K46sglA4ZvAho+46
            FYoh4gFNYBXllAlMZzuJE8x4TbYH8DOA7v27GotgfIs7GM9ATTTp8GlXc8psrxaP
            DxxbR/bR13v+dizEh8lLORK6yy1/4cFh5EgKYzSvpSU+BZTS37nIz5nDfFDkcLIw
            FQQlmTmpmgClybn2KnEPXga4etvhGDDzGKAUd1Edi2Tt6ZVAbFEZ+cBVV5XdF+yZ
            C5R50R2rdP01aldmTuu8BBcTkY6zMfkgD6ejdpRJI/5cQ+qMF8+zGjXaDj+oXiNg
            8/e+2unCpR/Bk+BlQRq3u89psJy0e1/4N+bcNiLtlr0D3vXI1D+P5YFzmPhWZ3fh
            RuDQH9KPBuDQKIWcCxdEsy8CAwEAAQ==
            -----END PUBLIC KEY-----
        """.trimIndent()
    ) = RsaKeyPairProperties().also {
        it.generationTimestamp = generationTimestamp
        it.privateKey = privateKey
        it.publicKey = publicKey
    }
}
