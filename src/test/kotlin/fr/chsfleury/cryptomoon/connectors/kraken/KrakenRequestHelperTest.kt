package fr.chsfleury.cryptomoon.connectors.kraken

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URLEncoder

class KrakenRequestHelperTest {

    @Test
    fun getSignature() {
        val signature = KrakenRequestHelper.getSignature(
            "/0/private/AddOrder",
            "nonce=1616492376594&ordertype=limit&pair=XBTUSD&price=37500&type=buy&volume=1.25",
            1616492376594,
            "kQH5HW/8p1uGOVjbgWA7FunAmGO8lsSUXNsu3eow76sz84Q18fWxnyRzBHCd3pd5nE9qa99HAZtuZuj6F1huXg=="
        )

        assertEquals("4/dpxb3iT4tp/ZCVEwSnEsLxx0bqyhLpdfOpc6fn7OR8+UClSV5n9E6aSS8MPtnRfp32bAb0nmbRn6H8ndwLUQ==", signature)
    }
}