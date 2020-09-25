package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command

class CmdSay : Command("say", "<message>", "Say a message as the bot.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 1, true)) return
        val description = parser.parse(event.message.contentRaw.substring(bot.config.prefix.length + name.length))
        event.channel.sendMessage(description).queue()
    }
}