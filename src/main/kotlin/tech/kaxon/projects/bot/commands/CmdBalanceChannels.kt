package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdBalanceChannels : Command("balancechannel", "strike|adjustment #channel", "Change the penalty channel.", Pair("Invalid channel!", "Try the direct link (#channel) or the channel ID.")) {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 1, true)) return
        val id = getChannelID(args[1])
        if (!isChannel(id)) {
            showError(event)
            return
        }
        when (args[0].toLowerCase()) {
            "strike" -> bot.balanceChannels.strikeChannel = id.toLong()
            "adjustment" -> bot.balanceChannels.adjustmentChannel = id.toLong()
            else -> {
                showError(event)
                return
            }
        }
        bot.balanceChannels.save()
        UtilEmbed.result(event, null, "${args[0].capitalize()} channel set!", "Channel: `$id`", success = true)
        return
    }
}