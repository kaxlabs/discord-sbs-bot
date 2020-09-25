package tech.kaxon.projects.bot.files.types

import tech.kaxon.projects.bot.main.Main
import java.io.File

open class BasicTextFile(@field:Transient val fileName: String) {
    init {
        setup()
    }

    private fun setup() {
        val file = File(Main.configDir, this.fileName.replace("/", File.separator))
        if (!file.exists()) {
            file.parentFile.mkdir()
            file.createNewFile()
        }
    }

    fun file(): File {
        return File(Main.configDir, this.fileName.replace("/", File.separator))
    }
}