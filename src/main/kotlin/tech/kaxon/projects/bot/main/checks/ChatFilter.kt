package tech.kaxon.projects.bot.main.checks

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.sheets.UtilSheets

class ChatFilter {
    fun checkFilter(event: MessageReceivedEvent, msgRaw: String) {
        if (!Main.settings.chatFilter) return
        var found = false
        Main.roles.file().forEachLine { line ->
            try {
                val role = event.guild.getRolesByName(line, true)[0]
                if (event.member!!.roles.contains(role)) found = true
            } catch (ie: IndexOutOfBoundsException) {
            }
        }
        if (found || event.member!!.hasPermission(Permission.ADMINISTRATOR)) return
        when (UtilSheets.getChannelTypeByID(event.channel.idLong).first) {
            "normal" -> {
                var find = false
                Main.words.file().forEachLine { line ->
                    if (msgRaw.equals(line, true)) {
                        find = true
                    }
                }
                if (!find) event.message.delete().queue(null, { })
            }
        }
    }
}