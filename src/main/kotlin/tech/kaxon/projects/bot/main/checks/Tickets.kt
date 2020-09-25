package tech.kaxon.projects.bot.main.checks

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.requests.restaction.ChannelAction
import tech.kaxon.projects.bot.main.Main

class Tickets {
    private val readWritePermissions = listOf(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_HISTORY)

    fun checkTickets(event: GuildMessageReactionAddEvent) {
        if (event.user.isBot) return
        val category = event.jda.getCategoryById(Main.ticketChannels.categoryID) ?: return
        if (event.reactionEmote.name != "❔" && event.reactionEmote.name != "❌") return

        Main.ticketChannels.channels.forEach { channel ->
            if (event.channel.idLong == channel) {
                if (event.reactionEmote.name == "❌") {
                    Main.ticketChannels.channels.remove(channel)
                    Main.ticketChannels.save()
                    event.channel.delete().queue()
                    return
                }
            }
        }

        val channels = listOf(Main.ticketChannels.bmahChannelID, Main.ticketChannels.paymentChannelID, Main.ticketChannels.tokensChannelID)
        var found = false
        channels.forEach { channel ->
            if (event.channel.idLong == channel) found = true
        }
        if (!found) return

        event.channel.retrieveMessageById(event.messageIdLong).queue { message ->
            message.retrieveReactionUsers("❔").queue q@{ users ->
                if (users.size > 2) {
                    event.channel.clearReactionsById(event.messageIdLong).queue { message.addReaction("❔").queue() }
                } else event.channel.removeReactionById(event.messageIdLong, "❔", event.user).queue()

                val type = when (message.embeds[message.embeds.size - 1]) {
                    Main.bmahEmbeds.submitEmbed -> "bmah"
                    Main.paymentEmbeds.submitEmbed -> "payment"
                    Main.tokensEmbeds.submitEmbed -> "tokens"
                    else -> return@q
                }

                val channelName = "$type-${event.member.effectiveName}"

                category.textChannels.forEach { channel ->
                    if (channel.name.contains(channelName, true)) return@q
                }


                val newChannel = category.createTextChannel(channelName).addMemberPermissionOverride(event.member.idLong, readWritePermissions, emptyList())
                        .addRolePermissionOverride(event.guild.publicRole.idLong, emptyList(), readWritePermissions)
                val embed = when (type) {
                    "bmah" -> Main.bmahEmbeds.createEmbed
                    "payment" -> Main.paymentEmbeds.createEmbed
                    "tokens" -> Main.tokensEmbeds.createEmbed
                    else -> return@q
                }
                val roleMention = try {
                    when (embed) {
                        Main.bmahEmbeds.createEmbed -> {
                            addPermissions(event, newChannel, Main.ticketChannels.bmahRole)
                            event.guild.getRolesByName(Main.ticketChannels.bmahRole, true)[0].asMention
                        }
                        Main.paymentEmbeds.createEmbed -> {
                            addPermissions(event, newChannel, Main.ticketChannels.paymentRole)
                            event.guild.getRolesByName(Main.ticketChannels.paymentRole, true)[0].asMention
                        }
                        Main.tokensEmbeds.createEmbed -> {
                            addPermissions(event, newChannel, Main.ticketChannels.tokensRole)
                            event.guild.getRolesByName(Main.ticketChannels.tokensRole, true)[0].asMention
                        }
                        else -> String()
                    }
                } catch (ie: IllegalArgumentException) {
                } catch (ioe: IndexOutOfBoundsException) {
                }
                addPermissions(event, newChannel, Main.ticketChannels.staffRole)
                newChannel.queue q2@{ channel ->
                    Main.ticketChannels.channels.add(channel.idLong)
                    Main.ticketChannels.save()
                    channel.sendMessage("$roleMention " + event.user.asMention).queue {
                        channel.sendMessage(embed).queue { message ->
                            message.addReaction("❌").queue()
                        }
                        event.channel.sendMessage("${event.member.asMention} Click here: ${channel.asMention}").queue { message ->
                            Thread.sleep(10000L)
                            message.delete().queue()
                        }
                    }
                }
            }
        }
    }

    private fun addPermissions(event: GuildMessageReactionAddEvent, channel: ChannelAction<TextChannel>, role: String) {
        val checkedRole = event.guild.getRolesByName(role, true)[0]
        if (checkedRole != null) channel.addRolePermissionOverride(checkedRole.idLong, readWritePermissions, emptyList())
    }
}