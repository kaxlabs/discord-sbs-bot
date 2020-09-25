package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdNormal : Command("normal", "<add|remove|clear> [alliance|horde] [#channels...]", "Manage the normal faction channels.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 1, true)) return
        val msg: String
        if (args[0].toLowerCase() == "clear") {
            bot.normalChannels.clear()
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
                        if (!bot.normalChannels.allianceChannels.contains(fIDLong) && isChannel(fID)) {
                            bot.normalChannels.allianceChannels.add(fIDLong)
                        }
                    }
                    "Alliance"
                } else {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (!bot.normalChannels.hordeChannels.contains(fIDLong) && isChannel(fID)) {
                            bot.normalChannels.hordeChannels.add(fIDLong)
                        }
                    }
                    "Horde"
                }
                msg = "Channel(s) `${argsToString(idList)}` added to `$fMsg`."
                bot.normalChannels.save()
            }
            "remove" -> {
                var fID: String
                val fMsg = if (alliance) {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (!bot.normalChannels.allianceChannels.contains(fIDLong) && isChannel(fID)) {
                            bot.normalChannels.allianceChannels.remove(fIDLong)
                        }
                    }
                    "Alliance"
                } else {
                    for (id in idList) {
                        fID = getChannelID(id)
                        val fIDLong = fID.toLong()
                        if (!bot.normalChannels.hordeChannels.contains(fIDLong) && isChannel(fID)) {
                            bot.normalChannels.hordeChannels.remove(fIDLong)
                        }
                    }
                    "Horde"
                }
                msg = "Channel(s) `${argsToString(idList)}` removed from `$fMsg`."
                bot.normalChannels.save()
            }
            else -> {
                msg = "Invalid modifier arguments!"
                UtilEmbed.result(event, msg, success = false)
                return
            }
        }
        UtilEmbed.result(event, null, "Normal faction channels updated!", msg, success = true)
    }
}