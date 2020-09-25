package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.UtilEmbed
import java.io.File
import java.io.PrintWriter


class CmdGiveaway : Command("giveaway", "<messageID|clear|get>", "Manage giveaways. :tada:") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 1, true)) return
        when (args[0].toLowerCase()) {
            "clear" -> {
                event.channel.retrieveMessageById(bot.giveaways.currentMessageID).queue {
                    it.clearReactions().queue q@{
                        bot.giveaways.currentMessageID = 0L
                        bot.giveaways.save()
                        val writer = PrintWriter(bot.giveaway.file())
                        writer.print("")
                        writer.close()
                        return@q
                    }
                }
            }
            "get" -> event.channel.sendFile(File(Main.configDir, Main.giveaway.fileName)).queue()
            else -> {
                val id = getChannelID(args[0])
                if (isChannel(id)) {
                    bot.giveaways.currentMessageID = id.toLong()
                    bot.giveaways.save()
                    event.channel.retrieveMessageById(id).queue { it.addReaction("\uD83C\uDF89").queue() }
                } else UtilEmbed.result(event, "Invalid channel!", "Try the direct link (#channel) or the channel ID.", success = false)
            }
        }
    }
}