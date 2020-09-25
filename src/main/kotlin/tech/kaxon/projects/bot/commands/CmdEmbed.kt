package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdEmbed : Command("embed", "<args>", "Embed a message.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 1, false)) {
            UtilEmbed.result(event, null, "Usage",
                    "`{title:`TEXT`|`URL`}` or `{title:`TEXT`}`\n`{author:`NAME`|`IMAGE`|`URL`}` or `{author:`NAME`|`IMAGE`}` or `{author:`NAME`}`\n`{thumbnail:`IMAGE`}`\n`{field:`NAME`|`VALUE`|`true/false`}` or `{field:`NAME`|`VALUE`}`\n*can include multiple fields\n`{image:`IMAGE`}`\n`{color:`#HEX`}` or `{color:name}`\n`{footer:`TEXT`|`IMAGE`}` or `{footer:`TEXT`}`\n`{timestamp:`ISO`}` or `{timestamp}`\n*current time if nothing included\n\nAny remaining text goes into the description.",
                    success = true)
            return
        }
        val builder = EmbedBuilder()
        builder.setColor(event.guild.selfMember.color)
        parser.put("builder", builder)
        val description = parser.parse(event.message.contentRaw.substring(bot.config.prefix.length + name.length))
        if (description.isNotEmpty()) builder.setDescription(description)
        event.channel.sendMessage(builder.build()).queue()
    }
}