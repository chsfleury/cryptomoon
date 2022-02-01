package fr.chsfleury.cryptomoon.infrastructure.wallet

import com.fasterxml.jackson.module.kotlin.readValue
import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.utils.Json
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

object LocalWalletsFile {
    private const val WALLET_FILENAME = "wallets.json"

    private val walletsFilePath: Path
    private lateinit var wallets: Map<String, Wallet>

    private val alive = AtomicBoolean(false)
    private lateinit var watchThreadExecutor: ExecutorService
    private val watchThread: FileWatcher

    init {
        val currentDirectory = Paths.get(System.getProperty("user.dir"))
        walletsFilePath = currentDirectory.resolve(WALLET_FILENAME)
        val watchService: WatchService = FileSystems.getDefault().newWatchService()
        watchThread = FileWatcher(WALLET_FILENAME, watchService)
        currentDirectory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
        updateWallets()
        startWatch()
    }

    fun getWallets(): Map<String, Wallet> = wallets

    fun updateWallets() {
        wallets = loadWallets()
    }

    private fun loadWallets(): Map<String, Wallet> {
        return if (Files.exists(walletsFilePath)) {
            val walletContent = Files.readAllBytes(walletsFilePath)
            val walletInputMap: WalletInputMap = Json.readValue(walletContent)
            walletInputMap.mapValues { e ->
                val balances = e.value.mapKeys { Currencies[it.key] }
                Wallet(e.key, balances)
            }
        } else {
            emptyMap()
        }
    }

    fun startWatch() {
        if (!alive.get()) {
            watchThreadExecutor = Executors.newSingleThreadExecutor()
            watchThreadExecutor.execute(watchThread)
            alive.set(true)
        }
    }

    fun stopWatch() {
        if (alive.get()) {
            watchThreadExecutor.shutdownNow()
            alive.set(false)
        }
    }
}