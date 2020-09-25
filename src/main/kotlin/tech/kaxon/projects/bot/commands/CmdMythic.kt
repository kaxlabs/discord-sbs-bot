package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdMythic : Command("mythic", "<add|remove|clear> [alliance|horde] #channels...", "Manage the mythic faction channels.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 1, true)) return
        val msg: String
        if (args[0].toLowerCase() == "clear") {
            bot.mythicChannels.clear()
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
        val idList = args.subList(2, args.size)
        when (args[0].toLowerCase()) {
            "add" -> {
                var fID: String
                val fMsg = if (alliance) {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (!bot.mythicChannels.allianceChannels.contains(fIDLong) && isChannel(fID)) {
                            bot.mythicChannels.allianceChannels.add(fIDLong)
                        } else if (!isChannel(fID)) {
                            msg = "One or more channels are invalid!"
                            UtilEmbed.result(event, msg, success = false)
                            return
                        }
                    }
                    "Alliance"
                } else {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (!bot.mythicChannels.hordeChannels.contains(fIDLong) && isChannel(fID)) {
                            bot.mythicChannels.hordeChannels.add(fIDLong)
                        }
                    }
                    "Horde"
                }
                msg = "Channel(s) `${argsToString(idList)}` added to `$fMsg`."
                bot.mythicChannels.save()
            }
            "remove" -> {
                var fID: String
                val fMsg = if (alliance) {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (!bot.mythicChannels.allianceChannels.contains(fIDLong) && isChannel(fID)) {
                            bot.mythicChannels.allianceChannels.remove(fIDLong)
                        }
                    }
                    "Alliance"
                } else {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (!bot.mythicChannels.hordeChannels.contains(fIDLong) && isChannel(fID)) {
                            bot.mythicChannels.hordeChannels.remove(fIDLong)
                        }
                    }
                    "Horde"
                }
                msg = "Channel(s) `${argsToString(idList)}` removed from `$fMsg`."
                bot.mythicChannels.save()
            }
            else -> {
                msg = "Invalid modifier arguments!"
                UtilEmbed.result(event, msg, success = false)
                return
            }
        }
        UtilEmbed.result(event, null, "Mythic faction channels updated!", msg, success = true)
    }
}