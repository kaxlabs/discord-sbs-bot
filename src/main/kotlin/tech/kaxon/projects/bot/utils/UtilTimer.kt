package tech.kaxon.projects.bot.utils

import tech.kaxon.projects.bot.main.Main
import java.time.LocalDateTime


enum class Convert(val ms: Long) {
    SECOND(1000), MINUTE(60000), HOUR(3600000), HALF_DAY(43200000)
}

object UtilTimer {
    private var lastMs = 0L

    init {
        reset()
    }

    fun msPassed(ms: Long): Boolean {
        return System.currentTimeMillis() - lastMs >= ms
    }

    fun unitPassed(unit: Long): Boolean {
        return (System.currentTimeMillis() - this.lastMs) / unit >= 1
    }

    fun reset() {
        lastMs = System.currentTimeMillis()
    }

    fun getTime(): String {
        return Main.formatter.format(LocalDateTime.now())
    }
}