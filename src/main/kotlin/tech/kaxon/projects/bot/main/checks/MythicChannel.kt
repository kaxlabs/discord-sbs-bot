package tech.kaxon.projects.bot.main.checks

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.UtilEmbed
import tech.kaxon.projects.bot.utils.UtilRegex
import tech.kaxon.projects.bot.utils.sheets.UtilSheets
import java.awt.Color
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MythicChannel {
    fun mythicChannel(event: GuildMessageReactionAddEvent, message: Message, lines: List<String>, peopleIDs: HashMap<Long, String>, gold: Double, ownerCut: Double, raidLeaderCut: Double, adCut: Double, boosterCut: Double,
                      type: Triple<String, String, String>) {
        val map = hashMapOf<String, Double>()

        /*
        * Give cut to Guild Representative
        * */
        event.channel.getHistoryBefore(message.idLong, 100).queue { messages ->
            var find = String()
            for (msg in messages.retrievedHistory) {
                if (msg.contentRaw.isBlank()) continue
                if (Regex("\\[(.+)]").containsMatchIn(msg.contentRaw)) {
                    find = UtilRegex.getRegex(msg.contentRaw, "\\[(.+)]", 1)
                }
                if (find.isNotBlank()) break
            }
            if (find.isBlank()) {
                UtilEmbed.result(event, "Couldn't find Guild Representative!", success = false)
                return@queue
            }
            map[find] = boosterCut + raidLeaderCut
            val author = message.author.idLong
            map[Main.config.owner] = (map[Main.config.owner] ?: 0.0) + ownerCut
            peopleIDs[Main.config.ownerID] = Main.config.owner
            val advertiser = event.guild.retrieveMemberById(author).submit().get().effectiveName
            val adPerson = map[advertiser] ?: 0.0
            map[advertiser] = adPerson
            peopleIDs[author] = advertiser
            for (key in map.keys) {
                if (key.toLowerCase() == advertiser.toLowerCase()) {
                    map[advertiser] = adPerson + adCut
                }
            }

            val sheetID = UtilSheets.getSheetIDByCheck(type)
            var color: Color? = null
            when (sheetID.first) {
                Main.config.allianceSheetGID -> color = Color.decode("#034efc")
                Main.config.hordeSheetGID -> color = Color.decode("#b50012")
            }

            UtilEmbed.em.addField("**Server**", lines[1], true)
            UtilEmbed.em.addField("**POT**", lines[2], true)
            UtilEmbed.em.addField("**Advertiser**", advertiser, true)
            UtilEmbed.em.addField("**Guild Representative**", find, false)
            UtilEmbed.em.addField("**Advertiser Cut**", Main.decimalFormatter.format(adCut), true)
            UtilEmbed.em.addField("**Representative Cut**", Main.decimalFormatter.format(boosterCut + raidLeaderCut), true)
            val clientTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.now())
            val adMap = hashMapOf<String, Double>()
            adMap[advertiser] = gold
            UtilSheets.updateSheetByID(sheetID.first, map, "B3:E50000", 2, 5, 1)
            UtilSheets.updateSheetByID(sheetID.second, adMap, "B3:E50000", 2, 5, 1)
            UtilEmbed.result(event, footer = clientTime, channel = event.jda.getTextChannelById(Main.logChannel.logChannel), color = color)
        }
    }
}