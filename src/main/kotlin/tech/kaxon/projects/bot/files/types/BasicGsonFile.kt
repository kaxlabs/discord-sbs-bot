package tech.kaxon.projects.bot.files.types

import com.google.gson.JsonParser
import tech.kaxon.projects.bot.main.Main
import java.io.*

open class BasicGsonFile(@Transient var fileName: String, val template: Boolean = false) {
    fun save() {
        try {
            val file = File(Main.configDir, fileName)
            file.parentFile.mkdirs()
            file.createNewFile()
            val fileWriter = FileWriter(file)
            fileWriter.write(Main.gson.toJson(this, this.javaClass))
            fileWriter.flush()
            fileWriter.close()
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    companion object {
        fun <T : BasicGsonFile> load(obj: BasicGsonFile): T {
            try {
                val file = File(Main.configDir, obj.fileName.replace("/", File.separator))
                if (!file.exists()) obj.save()
                val fileReader = FileReader(file)
                val obj2 = Main.gson.fromJson<T>(JsonParser.parseReader(fileReader), obj.javaClass) as T
                if (obj2.template) obj2.fileName = file.parentFile.name + File.separatorChar + file.name
                return obj2
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            throw NullPointerException()
        }
    }
}