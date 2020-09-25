package tech.kaxon.projects.bot.utils

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.requests.RestAction
import tech.kaxon.projects.bot.main.Main
import java.awt.Color

object UtilEmbed {
    private var title = Main.botName
    private var footer: String? = null
    private var thumbnailUrl: String? = null
    private var footerUrl: String? = null

    val em: EmbedBuilder = EmbedBuilder()

    fun resetEmbed() {
        em.clear()
    }

    private enum class Status(val message: String) {
        SUCCESS("Success ✅"), FAILURE("Error ❌")
    }

    fun log(guild: Guild, message: String) {
        if (Main.logChannel.logChannel == 0L) return
        result(guild, Main.logChannel.logChannel, message, footer = UtilTimer.getTime(), success = true, delay = 0)
    }

    fun result(guild: Guild, channel: Long, description: String? = null, vararg fields: Triple<String, String, Boolean>, success: Boolean? = null, color: Color? = null, footer: String? = null, thumbnailUrl: String? = null,
               footerUrl: String? =
                       null, author: String? = null, delay: Int = 6) {
        val ch = guild.getTextChannelById(channel)

        if (author != null) em.setAuthor(author)

        if (success != null) em.setTitle(if (success) Status.SUCCESS.message else Status.FAILURE.message)

        if (!description.isNullOrBlank()) em.appendDescription(description)

        for (field in fields) {
            UtilNull.notNull(field.first, field.second, field.third) { vars ->
                em.addField(vars[0], vars[1], vars[2].toBoolean())
            }
        }

        val col = color ?: Color.decode("#000000")// event.guild.selfMember.color
        em.setColor(col)
        em.setFooter(footer ?: this.footer, footerUrl ?: this.footerUrl)
        em.setThumbnail(thumbnailUrl ?: this.thumbnailUrl)
        val action: RestAction<Message> = ch?.sendMessage(MessageBuilder().setEmbed(em.build()).build()) ?: throw NullPointerException("Message not found for embed!")
        if (delay > 0) action.queue { m ->
            Thread.sleep(delay * 1000L)
            m.delete().queue()
        } else action.queue()
        resetEmbed()
    }

    fun result(event: MessageReceivedEvent, description: String? = null, result: String? = null, message: String? = null, success: Boolean? = null, color: Color? = null, footer: String? = null, footerIcon: String? = null,
               delay: Int = 0,
               channel: MessageChannel? = null) {
        if (success != null) em.setTitle(if (success) Status.SUCCESS.message else Status.FAILURE.message)

        if (!description.isNullOrBlank()) em.appendDescription(description)

        UtilNull.notNull(result, message) { vars ->
            em.addField(vars[0], vars[1], false)
        }

        val col = color ?: event.guild.selfMember.color
        em.setColor(col)
        val f = footer ?: ""
        em.setFooter(f, footerIcon)
        val ch = channel ?: event.channel
        val action: RestAction<Message> = ch.sendMessage(MessageBuilder().setEmbed(em.build()).build())
        if (delay > 0) action.queue { m ->
            Thread.sleep(delay.toLong() * 1000L)
            m.delete().queue()
        } else action.queue()
        resetEmbed()
    }

    fun result(event: GuildMessageReactionAddEvent, description: String? = null, field: String? = null, message: String? = null, success: Boolean? = null, color: Color? = null, footer: String? = null, footerIcon: String? = null,
               delay: Int = 0,
               channel: MessageChannel? = null) {
        if (success != null) em.setTitle(if (success) Status.SUCCESS.message else Status.FAILURE.message)

        if (!description.isNullOrBlank()) em.appendDescription(description)

        UtilNull.notNull(field, message) { vars ->
            em.addField(vars[0], vars[1], false)
        }

        val col = color ?: event.guild.selfMember.color
        em.setColor(col)
        val f = footer ?: ""
        em.setFooter(f, footerIcon)
        val ch = channel ?: event.channel
        val action: RestAction<Message> = ch.sendMessage(MessageBuilder().setEmbed(em.build()).build())

        if (delay > 0) action.queue { m ->
            Thread.sleep(delay * 1000L)
            m.delete().queue()
        } else action.queue()
        resetEmbed()
    }
}