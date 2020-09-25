package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdPrefix : Command("prefix", "<prefix>", "Change the prefix for the bot interactions.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        bot.config.prefix = args[0]
        bot.config.save()
        UtilEmbed.result(event, null, "Bot prefix set!", "Prefix: `${args[0]}`", success = true)
    }
}