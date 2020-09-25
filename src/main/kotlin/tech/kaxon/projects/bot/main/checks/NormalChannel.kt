package tech.kaxon.projects.bot.main.checks

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.UtilEmbed
import tech.kaxon.projects.bot.utils.UtilRegex
import tech.kaxon.projects.bot.utils.sheets.UtilSheets
import java.awt.Color
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NormalChannel {
    fun normalChannel(event: GuildMessageReactionAddEvent, message: Message, lines: List<String>, peopleIDs: HashMap<Long, String>, gold: Double, ownerCut: Double, raidLeaderCut: Double, adCut: Double, boosterCut: Double,
                      type: Triple<String, String, String>) {
        var boosterCutMutable = boosterCut + raidLeaderCut
        val people = ArrayList(lines.subList(3, lines.size))
        val sb = StringBuilder()
        val map = hashMapOf<String, Double>()
        var name: String
        var count = people.size
        for (person in people) {
            if (!person.contains('@')) {
                count--
                continue
            }
        }
        boosterCutMutable /= count
        var trimmedName: String? = null
        val boosters = mutableListOf<String>()
        for (person in people) {
            if (!person.contains('@')) continue
            val nameOld = UtilRegex.getBetween(person, listOf('@'), listOf('>')).replace("!", "").toLong()
            var emote = UtilRegex.getBefore(person, listOf('>'))
            emote += ">"
            name = event.guild.retrieveMemberById(nameOld).submit().get().effectiveName
            trimmedName = name.trim().replace("\\s".toRegex(), "")
            map[trimmedName] = boosterCutMutable
            peopleIDs[nameOld] = trimmedName
            boosters.add(trimmedName)
            sb.append(emote + trimmedName).append(" ")
        }

        map[Main.config.owner] = (map[Main.config.owner] ?: 0.0) + ownerCut
        peopleIDs[Main.config.ownerID] = Main.config.owner
        val author = message.author.idLong
        var advertiser = event.guild.retrieveMemberById(author).submit().get().effectiveName
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
        val newBoosters: String? = null
        when (sheetID.first) {
            Main.config.allianceSheetGID -> color = Color.decode("#034efc")
            Main.config.hordeSheetGID -> color = Color.decode("#b50012")
        }

        UtilEmbed.em.addField("**Server**", lines[1], true)
        UtilEmbed.em.addField("**POT**", lines[2], true)
        UtilEmbed.em.addField("**Advertiser**", advertiser, false)
        UtilEmbed.em.addField("**Advertiser Cut**", DecimalFormat("#.##").format(adCut), true)
        UtilEmbed.em.addField("**Boosters Cut**", DecimalFormat("#.##").format(boosterCutMutable), true)
        UtilEmbed.em.addField("**Boosters**", newBoosters ?: sb.toString(), false)
        val clientTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.now())
        val adMap = hashMapOf<String, Double>()
        adMap[advertiser] = gold
        UtilSheets.updateSheetByID(sheetID.first, map, "B3:E50000", 2, 5, 1)
        UtilSheets.updateSheetByID(sheetID.second, adMap, "B3:E50000", 2, 5, 1)
        UtilEmbed.result(event, footer = clientTime, channel = event.jda.getTextChannelById(Main.logChannel.logChannel), color = color)
    }
}