package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.infrastructure.connector.binance.BinanceImporter
import io.javalin.http.Context

class ImportController {

    fun extractBalanceFromBinanceStakingExport(ctx: Context) {
        val exportFile = ctx.uploadedFile("file")
        if (exportFile == null) {
            ctx.status(400)
        } else {
            val balances = BinanceImporter.extractBalancesFromStakingHistoryFile(exportFile.content)
            ctx.json(balances)
        }
    }

}