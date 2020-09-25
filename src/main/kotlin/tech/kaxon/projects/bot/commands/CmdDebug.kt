package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command

class CmdDebug : Command("debug") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        event.textChannel.history.retrievePast(1).queue { messages ->
            messages.forEach { message ->
                println(message.contentRaw)
            }
        }
    }
}