package fr.chsfleury.cryptomoon.infrastructure.wallet

import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchService

class FileWatcher(private val filename: String, private val watchService: WatchService): Runnable, Logging {
    private val log = logger()
    private var keepRunning = true

    fun stop() {
        keepRunning = false
    }

    override fun run() {
        while (keepRunning) {
            val watchKey = watchService.take()

            watchKey.pollEvents().forEach { event ->
                if(filename == event?.context()?.toString()) {
                    when (event.kind()) {
                        ENTRY_CREATE -> {
                            log.info("wallets file created")
                            LocalWalletsFile.updateWallets()
                        }
                        ENTRY_MODIFY -> {
                            log.info("wallets file edited")
                            LocalWalletsFile.updateWallets()
                        }
                        ENTRY_DELETE -> {
                            log.info("wallets file deleted")
                            LocalWalletsFile.updateWallets()
                        }
                    }
                }
            }
        }
    }

}