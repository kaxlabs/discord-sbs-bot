package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdLogChannel : Command("logchannel", "<channel>", "Change the log channel.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 1, true)) return
        val id = getChannelID(args[0])
        if (isChannel(id)) {
            bot.logChannel.logChannel = id.toLong()
            bot.logChannel.save()
            UtilEmbed.result(event, null, "Log channel set!", "Channel: `${bot.logChannel.logChannel}`", success = true)
            return
        }
        UtilEmbed.result(event, null, "Invalid channel!", "Try the direct link (#channel) or the channel ID.", success = false)
    }
}