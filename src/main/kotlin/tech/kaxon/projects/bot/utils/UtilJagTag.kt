package tech.kaxon.projects.bot.utils

import com.jagrosh.jagtag.Method
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.time.OffsetDateTime

object UtilJagTag {
    val methods: Collection<Method>
        get() = listOf(Method("title") { env, `in` ->
            val eb = env.get<EmbedBuilder>("builder")
            val parts = `in`[0].split("\\|".toRegex(), 2).toTypedArray()
            eb.setTitle(parts[0], if (parts.size > 1) parts[1] else null)
            ""
        }, Method("author") { env, `in` ->
            val eb = env.get<EmbedBuilder>("builder")
            val parts = `in`[0].split("\\|".toRegex(), 3).toTypedArray()
            eb.setAuthor(parts[0], if (parts.size > 2) parts[2] else null, if (parts.size > 1) parts[1] else null)
            ""
        }, Method("thumbnail") { env, `in` ->
            val eb = env.get<EmbedBuilder>("builder")
            eb.setThumbnail(`in`[0])
            ""
        }, Method("field") { env, `in` ->
            val eb = env.get<EmbedBuilder>("builder")
            val parts = `in`[0].split("\\|".toRegex(), 3).toTypedArray()
            eb.addField(parts[0], parts[1], if (parts.size > 2) parts[2].equals("true", ignoreCase = true) else true)
            ""
        }, Method("image") { env, `in` ->
            val eb = env.get<EmbedBuilder>("builder")
            eb.setImage(`in`[0])
            ""
        }, Method("color") { env, `in` ->
            val eb = env.get<EmbedBuilder>("builder")
            when (`in`[0].toLowerCase()) {
                "red" -> eb.setColor(Color.RED)
                "orange" -> eb.setColor(Color.ORANGE)
                "yellow" -> eb.setColor(Color.YELLOW)
                "green" -> eb.setColor(Color.GREEN)
                "cyan" -> eb.setColor(Color.CYAN)
                "blue" -> eb.setColor(Color.BLUE)
                "magenta" -> eb.setColor(Color.MAGENTA)
                "pink" -> eb.setColor(Color.PINK)
                "black" -> eb.setColor(Color.decode("#000001"))
                "dark_gray", "dark_grey" -> eb.setColor(Color.DARK_GRAY)
                "gray", "grey" -> eb.setColor(Color.GRAY)
                "light_gray", "light_grey" -> eb.setColor(Color.LIGHT_GRAY)
                "white" -> eb.setColor(Color.WHITE)
                "blurple" -> eb.setColor(Color.decode("#7289DA"))
                "greyple" -> eb.setColor(Color.decode("#99AAB5"))
                "darktheme" -> eb.setColor(Color.decode("#2C2F33"))
                else -> eb.setColor(Color.decode(`in`[0]))
            }
            ""
        }, Method("footer") { env, `in` ->
            val eb = env.get<EmbedBuilder>("builder")
            val parts = `in`[0].split("\\|".toRegex(), 2).toTypedArray()
            eb.setFooter(parts[0], if (parts.size > 1) parts[1] else null)
            ""
        }, Method("timestamp") { env ->
            val eb = env.get<EmbedBuilder>("builder")
            eb.setTimestamp(OffsetDateTime.now())
            ""
        })
}
