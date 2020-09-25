package tech.kaxon.projects.bot.main.checks

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import tech.kaxon.projects.bot.main.Main
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.Writer

class Giveaways {
    fun checkGiveaway(event: GuildMessageReactionAddEvent) {
        if (Main.giveaways.currentMessageID == 0L || event.user.isBot) return
        val authorFormatted = "${event.user.name}#${event.user.discriminator} (${event.user.idLong})"
        var find = false
        Main.giveaway.file().forEachLine { line ->
            if (authorFormatted.equals(line, true)) {
                find = true
                return@forEachLine
            }
        }
        if (!find) {
            val writer: Writer = BufferedWriter(OutputStreamWriter(FileOutputStream(Main.giveaway.file(), true), "UTF-8"))
            writer.appendLine(authorFormatted)
            writer.close()
        }
    }
}