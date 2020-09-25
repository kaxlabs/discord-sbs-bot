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
import java.util.*
import kotlin.collections.ArrayList

class HeroicChannel {
    fun heroicChannelBooster(event: GuildMessageReactionAddEvent, message: Message, lines: List<String>, peopleIDs: HashMap<Long, String>, gold: Double, ownerCut: Double, raidLeaderCut: Double, adCut: Double, boosterCut: Double,
                             type: Triple<String, String, String>) {
        var boosterCutMutable = boosterCut
        val people = ArrayList(lines.subList(2, lines.size))
        val boosterNames = StringBuilder()
        val boosterCuts = StringBuilder()
        val map = linkedMapOf<String, Double>()
        var name: String
        var count = people.size
        var trimmedName = String()
        var nameOld: Long
        for (person in people) {
            if (!person.contains('@')) {
                count--
                continue
            }
        }
        boosterCutMutable /= count
        val boosters = LinkedList<String>()
        val modifiedBoosters = LinkedList<String>()
        var secondCut = 0.0
        for (person in people) {
            if (!person.contains('@')) continue
            var emote = UtilRegex.getBefore(person, listOf('>'))
            emote += ">"
            nameOld = UtilRegex.getBetween(person, listOf('@'), listOf('>')).replace("!", "").toLong()
            name = event.guild.retrieveMemberById(nameOld).submit().get().effectiveName
            trimmedName = name.trim().replace("\\s".toRegex(), "")
            peopleIDs[nameOld] = trimmedName
            boosters.add(trimmedName)
            boosterNames.append(emote + trimmedName).append("\n")
            map[trimmedName] = boosterCutMutable
            if (Regex(" (\\d+) ").containsMatchIn(lines[0]) && Regex("\\[(\\d+)]").containsMatchIn(person)) {
                val find1 = UtilRegex.getRegex(lines[0], " (\\d+) ", 1)
                val find2 = UtilRegex.getRegex(person, "\\[(\\d+)]", 1)
                if (find1.isNotBlank() && find2.isNotBlank() && find1 != find2) {
                    count--
                    val newCut = find2.toInt() * (boosterCutMutable / find1.toInt())
                    map[trimmedName] = newCut
                    modifiedBoosters.add(trimmedName)
                    secondCut += boosterCutMutable - newCut
                }
            }
        }
        secondCut /= count
        for (person in people) {
            if (!person.contains('@')) continue
            nameOld = UtilRegex.getBetween(person, listOf('@'), listOf('>')).replace("!", "").toLong()
            name = event.guild.retrieveMemberById(nameOld).submit().get().effectiveName
            trimmedName = name.trim().replace("\\s".toRegex(), "")
            if (modifiedBoosters.contains(trimmedName)) continue
            val add = map[trimmedName]!! + secondCut
            map[trimmedName] = add
        }
        val author = message.author.idLong
        var advertiser = event.guild.retrieveMemberById(author).submit().get().effectiveName
        val adPerson = (map[advertiser] ?: 0.0) + raidLeaderCut
        map[advertiser] = adPerson
        peopleIDs[author] = advertiser

        val sheetID = UtilSheets.getSheetIDByCheck(type)
        var color: Color? = null
        val newBoosters: String? = null
        when (sheetID.first) {
            Main.config.allianceSheetGID -> color = Color.decode("#034efc")
            Main.config.hordeSheetGID -> color = Color.decode("#b50012")
        }
        UtilEmbed.em.addField("**Server**", lines[0], true)
        UtilEmbed.em.addField("**POT**", lines[1], true)
        UtilEmbed.em.addField("**Raid Leader**", advertiser, false)
        UtilEmbed.em.addField("**Raid Leader Cut**", DecimalFormat("#.##").format(raidLeaderCut), false)
        UtilEmbed.em.addField("**Boosters**", newBoosters ?: boosterNames.toString(), true)
        for (item in map.values) {
            boosterCuts.append(DecimalFormat("#.##").format(item)).append("\n")
        }
        UtilEmbed.em.addField("**Cuts**", boosterCuts.toString(), true)
        val clientTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.now())
        UtilSheets.updateSheetByID(sheetID.first, map, "B3:E50000", 2, 5, 1)
        UtilEmbed.result(event, footer = clientTime, channel = event.jda.getTextChannelById(Main.logChannel.logChannel), color = color)
    }

    fun heroicChannelAdvertiser(event: GuildMessageReactionAddEvent, message: Message, lines: List<String>, peopleIDs: HashMap<Long, String>, gold: Double, ownerCut: Double, raidLeaderCut: Double, adCut: Double,
                                boosterCut: Double,
                                type: Triple<String, String, String>) {
        val map = hashMapOf<String, Double>()

        val author = message.author.idLong
        var advertiser = event.guild.retrieveMemberById(author).submit().get().effectiveName
        val adPerson = map[advertiser] ?: 0.0
        map[advertiser] = adPerson
        map[Main.config.owner] = (map[Main.config.owner] ?: 0.0) + ownerCut
        peopleIDs[Main.config.ownerID] = Main.config.owner
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
        UtilEmbed.em.addField("**Advertiser**", advertiser, false)
        UtilEmbed.em.addField("**Advertiser Cut**", Main.decimalFormatter.format(adCut), true)
        val clientTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.now())
        val adMap = hashMapOf<String, Double>()
        adMap[advertiser] = gold
        UtilSheets.updateSheetByID(sheetID.first, map, "B3:E50000", 2, 5, 1)
        UtilSheets.updateSheetByID(sheetID.second, adMap, "B3:E50000", 2, 5, 1)
        UtilEmbed.result(event, footer = clientTime, channel = event.jda.getTextChannelById(Main.logChannel.logChannel), color = color)
    }
}