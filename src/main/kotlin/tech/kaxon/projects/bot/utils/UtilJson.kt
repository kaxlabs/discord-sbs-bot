package tech.kaxon.projects.bot.utils

import com.google.gson.Gson
import org.json.simple.parser.JSONParser
import tech.kaxon.projects.bot.main.Main
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type


class UtilJson(private val gson: Gson) {
    private val filePath = Main.configDir.path

    fun <T> getObject(fileName: String, type: Type): T {
        return gson.fromJson(getJson(fileName), type)
    }

    fun getJson(fileName: String): String? {
        val parser = JSONParser()
        try {
            val obj = parser.parse(FileReader(filePath + File.separator + fileName))
            return gson.toJson(obj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun <T> write(fileName: String, obj: T) {
        try {
            val outFile = File(filePath + File.separator + fileName)
            outFile.parentFile.mkdirs()
            outFile.createNewFile()
            val fileWriter = FileWriter(filePath + File.separator + fileName)
            fileWriter.write(gson.toJson(obj))
            fileWriter.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun write(fileName: String, json: String) {
        try {
            val outFile = File(filePath + File.separator + fileName)
            outFile.parentFile.mkdirs()
            outFile.createNewFile()
            val fileWriter = FileWriter(filePath + File.separator + fileName)
            fileWriter.write(json)
            fileWriter.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}