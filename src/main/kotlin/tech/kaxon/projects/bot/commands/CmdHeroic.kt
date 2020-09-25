package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdHeroic : Command("heroic", "<add|remove|clear> [alliance|horde] [booster|advertiser] #channels...", "Manage the heroic faction channels.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 1, true)) return
        val msg: String
        if (args[0].toLowerCase() == "clear") {
            bot.heroicChannels.clear()
            msg = "All channels cleared!"
            UtilEmbed.result(event, msg, success = true)
            return
        }
        if (checkArgsSize(event, args, 3, true)) return
        val alliance: Boolean
        when (args[1].toLowerCase()) {
            "alliance" -> alliance = true
            "horde" -> alliance = false
            else -> {
                msg = "Invalid faction argument!"
                UtilEmbed.result(event, msg, success = false)
                return
            }
        }
        val booster: Boolean
        when (args[2].toLowerCase()) {
            "booster" -> booster = true
            "advertiser" -> booster = false
            else -> {
                msg = "Invalid type argument!"
                UtilEmbed.result(event, msg, success = false)
                return
            }
        }
        val idList = args.subList(3, args.size)
        when (args[0].toLowerCase()) {
            "add" -> {
                var fID: String
                val fMsg = if (alliance) {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (booster) {
                            if (!bot.heroicChannels.allianceBoosterChannels.contains(fIDLong) && isChannel(fID)) {
                                bot.heroicChannels.allianceBoosterChannels.add(fIDLong)
                            }
                        } else {
                            if (!bot.heroicChannels.allianceAdvertiserChannels.contains(fIDLong) && isChannel(fID)) {
                                bot.heroicChannels.allianceAdvertiserChannels.add(fIDLong)
                            }
                        }
                    }
                    if (booster) "Alliance-Booster"
                    else "Alliance-Advertiser"
                } else {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (booster) {
                            if (!bot.heroicChannels.hordeBoosterChannels.contains(fIDLong) && isChannel(fID)) {
                                bot.heroicChannels.hordeBoosterChannels.add(fIDLong)
                            }
                        } else {
                            if (!bot.heroicChannels.hordeAdvertiserChannels.contains(fIDLong) && isChannel(fID)) {
                                bot.heroicChannels.hordeAdvertiserChannels.add(fIDLong)
                            }
                        }
                    }
                    if (booster) "Horde-Booster"
                    else "Horde-Advertiser"
                }
                msg = "Channel(s) `${argsToString(idList)}` added to `$fMsg`."
                bot.heroicChannels.save()
            }
            "remove" -> {
                var fID: String
                val fMsg = if (alliance) {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (booster) {
                            if (!bot.heroicChannels.allianceBoosterChannels.contains(fIDLong) && isChannel(fID)) {
                                bot.heroicChannels.allianceBoosterChannels.remove(fIDLong)
                            }
                        } else {
                            if (!bot.heroicChannels.allianceAdvertiserChannels.contains(fIDLong) && isChannel(fID)) {
                                bot.heroicChannels.allianceAdvertiserChannels.remove(fIDLong)
                            }
                        }
                    }
                    "Alliance"
                } else {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (booster) {
                            if (!bot.heroicChannels.hordeBoosterChannels.contains(fIDLong) && isChannel(fID)) {
                                bot.heroicChannels.hordeBoosterChannels.remove(fIDLong)
                            }
                        } else {
                            if (!bot.heroicChannels.hordeAdvertiserChannels.contains(fIDLong) && isChannel(fID)) {
                                bot.heroicChannels.hordeAdvertiserChannels.remove(fIDLong)
                            }
                        }
                    }
                    "Horde"
                }
                msg = "Channel(s) `${argsToString(idList)}` removed from `$fMsg`."
                bot.heroicChannels.save()
            }
            else -> {
                msg = "Invalid modifier arguments!"
                UtilEmbed.result(event, msg, success = false)
                return
            }
        }
        UtilEmbed.result(event, null, "Heroic faction channels updated!", msg, success = true)
    }
}