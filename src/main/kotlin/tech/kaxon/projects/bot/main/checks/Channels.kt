package tech.kaxon.projects.bot.main.checks

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.UtilAcronymConverter
import tech.kaxon.projects.bot.utils.UtilEmbed
import tech.kaxon.projects.bot.utils.UtilRegex
import tech.kaxon.projects.bot.utils.sheets.UtilSheets
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

class Channels {
    private fun getCut(value: Double, percentage: Double): Double {
        return value * (percentage / 100.0f)
    }

    fun getUsers(event: GuildMessageReactionAddEvent, ids: List<Long>): CompletableFuture<HashMap<Long, String>> {
        return CompletableFuture.supplyAsync {
            val map = hashMapOf<Long, String>()
            for (id in ids) {
                try {
                    map[id] = event.jda.retrieveUserById(id).submit().get().name
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }
            }
            map
        }
    }

    fun checkChannels(event: GuildMessageReactionAddEvent) {
        if (event.reactionEmote.isEmoji && event.reactionEmote.emoji == "\uD83D\uDCB0" && event.member.hasPermission(Permission.ADMINISTRATOR)) {
            event.channel.retrieveMessageById(event.messageIdLong).queue { message ->
                val lines = message.contentRaw.lines()
                val peopleIDs = HashMap<Long, String>()
                val gold: Double
                val ownerCut: Double
                val raidLeaderCut: Double
                val adCut: Double
                val boosterCut: Double
                try {
                    gold = UtilAcronymConverter.convertBigDecimal(UtilRegex.getRegex(message.contentRaw, ".+gold.+\\s+(\\d+.?(?:\\.\\d+[KM])?(?=\\s|\$|[^0-9]))", 1)).toDouble()
                    ownerCut = getCut(gold, Main.percentages.owner)
                    raidLeaderCut = getCut(gold, Main.percentages.raidLeaders)
                    adCut = getCut(gold, Main.percentages.advertisers)
                    boosterCut = getCut(gold, Main.percentages.boosters)
                } catch (e: NumberFormatException) {
                    UtilEmbed.result(event, "Invalid number format!", success = false)
                    e.printStackTrace()
                    return@queue
                }
                val type = UtilSheets.getChannelTypeByID(event.channel.idLong)
                when (type.first) {
                    "normal" -> {
                        NormalChannel().normalChannel(event, message, lines, peopleIDs, gold, ownerCut, raidLeaderCut, adCut, boosterCut, type)
                        return@queue
                    }
                    "heroic" -> {
                        val heroic = HeroicChannel()
                        if (type.third == "booster") heroic.heroicChannelBooster(event, message, lines, peopleIDs, gold, ownerCut, raidLeaderCut, adCut, boosterCut, type)
                        else heroic.heroicChannelAdvertiser(event, message, lines, peopleIDs, gold, ownerCut, raidLeaderCut, adCut, boosterCut, type)
                        return@queue
                    }
                    "mythic" -> {
                        MythicChannel().mythicChannel(event, message, lines, peopleIDs, gold, ownerCut, raidLeaderCut, adCut, boosterCut, type)
                        return@queue
                    }
                }
            }
        }
    }
}