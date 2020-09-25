package tech.kaxon.projects.bot.utils

import tech.kaxon.projects.bot.main.Main
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object UtilFile {
    @Throws(Exception::class)
    fun exportResource(resourceName: String): String? {
        var stream: InputStream? = null
        var resStreamOut: OutputStream? = null
        val jarFolder: String
        try {
            stream = Main::class.java.getResourceAsStream(resourceName) //note that each / is a directory down in the "jar tree" been the jar the root of the tree
            var readBytes: Int
            val buffer = ByteArray(4096)
            jarFolder = File(Main.configDir.toURI()).path
            resStreamOut = FileOutputStream(jarFolder + resourceName)
            while (stream.read(buffer).also {
                        readBytes = it
                    } > 0) {
                resStreamOut.write(buffer, 0, readBytes)
            }
        } catch (ex: Exception) {
            throw ex
        } finally {
            stream?.close()
            resStreamOut?.close()
        }
        return jarFolder + resourceName
    }
}