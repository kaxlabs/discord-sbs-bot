package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdTickets : Command("tickets", "<role|category|channel> [staff|bmah|payment|tokens]|categoryID role|#channel", "Manage ticket system.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 1, true)) return
        when (args[0].toLowerCase()) {
            "role" -> setRole(event, args)
            "category" -> setCategory(event, args)
            "channel" -> setChannel(event, args)
            else -> UtilEmbed.result(event, "Invalid argument! Valid arguments: `role, category, channel`.", success = false)
        }
    }

    private fun setCategory(event: MessageReceivedEvent, args: MutableList<String>) {
        val id = getChannelID(args[1])
        if (isChannel(id) && event.jda.getCategoryById(id.toLong()) != null) {
            bot.ticketChannels.categoryID = id.toLong()
            bot.ticketChannels.save()
            UtilEmbed.result(event, null, "Ticket category set!", "Category: `${event.jda.getCategoryById(id.toLong())?.name}`", success = true)
            return
        }
        UtilEmbed.result(event, "Invalid category ID or category doesn't exist!", success = false)
    }

    private fun setChannel(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 2, true)) return
        val id = getChannelID(args[2])
        if (isChannel(id)) {
            val type = getType(event, args, id.toLong())
            UtilEmbed.result(event, null, "Ticket channel set!", "$type Channel: ${event.guild.getTextChannelById(id)?.name ?: id}", success = true)
            val channel = event.guild.getTextChannelById(id) ?: return
            val embed = when (type.toLowerCase()) {
                "bmah" -> Main.bmahEmbeds.submitEmbed
                "payment" -> Main.paymentEmbeds.submitEmbed
                "tokens" -> Main.tokensEmbeds.submitEmbed
                else -> return
            }
            channel.sendMessage(embed).queue { message ->
                message.addReaction("❔").queue()
            }
            return
        }
        UtilEmbed.result(event, null, "Invalid channel!", "Try the direct link (#channel) or the channel ID.", success = false)
    }

    private fun setRole(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 2, true)) return
        val role = argsToString(args.subList(2, args.size))
        when (args[1].toLowerCase()) {
            "staff" -> bot.ticketChannels.staffRole = role
            "bmah" -> bot.ticketChannels.bmahRole = role
            "payment" -> bot.ticketChannels.paymentRole = role
            "tokens" -> bot.ticketChannels.tokensRole = role
            else -> {
                UtilEmbed.result(event, "Invalid role type! Valid roles: `staff, bmah, payment, tokens`", success = false)
                return
            }
        }
        bot.ticketChannels.save()
        UtilEmbed.result(event, null, "${args[2].capitalize()} role set!", "Role: `$role`", success = true)
    }

    private fun getType(event: MessageReceivedEvent, args: MutableList<String>, id: Long): String {
        when (args[1].toLowerCase()) {
            "bmah" -> bot.ticketChannels.bmahChannelID = id
            "payment" -> bot.ticketChannels.paymentChannelID = id
            "tokens" -> bot.ticketChannels.tokensChannelID = id
            else -> {
                UtilEmbed.result(event, "Invalid channel type! Valid channel types: `bmah, payment, tokens`", success = false)
                return String()
            }
        }
        bot.ticketChannels.save()
        return args[1].capitalize()
    }
}
