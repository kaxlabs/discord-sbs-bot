package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.utils.UtilAcronymConverter
import tech.kaxon.projects.bot.utils.UtilEmbed
import tech.kaxon.projects.bot.utils.UtilRegex
import tech.kaxon.projects.bot.utils.sheets.UtilSheets


class CmdPenalty : Command("penalty", "add|remove @tag amount alliance|horde note...", "Modify account penalties.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 4, true)) return
        val tagId = try {
            UtilRegex.getBetween(args[1], listOf('@'), listOf('>')).replace("!", "").toLong()
        } catch (nfe: NumberFormatException) {
            UtilEmbed.result(event, "Invalid user tag!", success = false)
            return
        }
        val amount = UtilAcronymConverter.convertBigDecimal(args[2]).toDouble()
        event.guild.retrieveMemberById(tagId).queue({ member ->
            val memberName = member.effectiveName
            val cellRange = "B3:E50000"
            val sRI = 2
            val eRI = 5
            val sCI = 1
            val type = "penalty"
            when (args[3].toLowerCase()) {
                "alliance" -> {
                    when (args[0].toLowerCase()) {
                        "add" -> UtilSheets.updateSheetByID(bot.config.allianceSheetGID, hashMapOf(memberName to amount), cellRange, sRI, eRI, sCI, false, type)
                        "remove" -> UtilSheets.updateSheetByID(bot.config.allianceSheetGID, hashMapOf(memberName to amount), cellRange, sRI, eRI, sCI, true, type)
                        else -> {
                            UtilEmbed.result(event, "Invalid add|remove argument specified.", success = false)
                            return@queue
                        }
                    }
                }
                "horde" -> {
                    when (args[0].toLowerCase()) {
                        "add" -> UtilSheets.updateSheetByID(bot.config.hordeSheetGID, hashMapOf(memberName to amount), cellRange, sRI, eRI, sCI, false, type)
                        "remove" -> UtilSheets.updateSheetByID(bot.config.hordeSheetGID, hashMapOf(memberName to amount), cellRange, sRI, eRI, sCI, true, type)
                        else -> {
                            UtilEmbed.result(event, "Invalid add|remove argument specified.", success = false)
                            return@queue
                        }
                    }
                }
                else -> {
                    UtilEmbed.result(event, "Invalid faction specified.", success = false)
                    return@queue
                }
            }
            val note = argsToString(args.subList(4, args.size))
            val formattedNote = if (note.isNotBlank()) "Reason: `$note`" else String()
            when (args[0].toLowerCase()) {
                "add" -> {
                    UtilEmbed.result(event.guild, bot.balanceChannels.adjustmentChannel, "Added `$amount` to `$memberName`'s ${type.capitalize()} in ${args[3].capitalize()}.\n$formattedNote", success = true, delay = 0)
                    UtilEmbed.result(event.guild, bot.balanceChannels.strikeChannel, "Added `$amount` to `$memberName`'s ${type.capitalize()} in ${args[3].capitalize()}.\n$formattedNote", success = true, delay = 0)
                }
                "remove" -> {
                    UtilEmbed.result(event.guild, bot.balanceChannels.adjustmentChannel, "Removed `$amount` from `$memberName`'s ${type.capitalize()} in ${args[3].capitalize()}.\n$formattedNote", success = true, delay = 0)
                    UtilEmbed.result(event.guild, bot.balanceChannels.strikeChannel, "Removed `$amount` from `$memberName`'s ${type.capitalize()} in ${args[3].capitalize()}.\n$formattedNote", success = true, delay = 0)
                }
                else -> return@queue
            }
        }, {
            UtilEmbed.result(event, "Member not found.", success = false)
        })
    }
}