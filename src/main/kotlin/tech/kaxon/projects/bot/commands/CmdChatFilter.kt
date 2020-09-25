package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdChatFilter : Command("chatfilter", "<none>", "Toggle the chat filter on or off (default: on).") {
    private enum class Status(val message: String) {
        ENABLED("Enabled"), DISABLED("Disabled")
    }

    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        bot.settings.chatFilter = !bot.settings.chatFilter
        bot.settings.save()
        val status = if (bot.settings.chatFilter) Status.ENABLED.message
        else Status.DISABLED.message
        UtilEmbed.result(event, null, "Chat filter state changed!", "State: `$status`", success = true)
    }
}