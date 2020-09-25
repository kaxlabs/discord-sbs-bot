package tech.kaxon.projects.bot.main.checks

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.*
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.UtilEmbed

class Command {
    fun checkCommand(event: MessageReceivedEvent, msgRaw: String) {
        if (msgRaw.toLowerCase().startsWith(Main.config.prefix.toLowerCase())) {
            val args = msgRaw.substring(Main.config.prefix.length).trim { it <= ' ' }.split("\\s+".toRegex()).toMutableList()
            for (cmd in Main.commands) {
                if (args.isNotEmpty() && args[0].equals(cmd.name, true)) {
                    if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
                        val totalCommand = cmd is CmdTotal
                        val balanceCommands = cmd is CmdEarned || cmd is CmdPaid || cmd is CmdPenalty
                        val generalCommands = cmd is CmdHelp || cmd is CmdBalance
                        if (!totalCommand && !balanceCommands && !generalCommands) {
                            UtilEmbed.result(event, "Insufficient permissions to run this command!", success = false)
                            return
                        }
                        if (totalCommand && !permsValid(event, event.guild.getRolesByName(Main.config.raidLeaderRole, true)[0])) continue
                        if (balanceCommands && !permsValid(event, event.guild.getRolesByName(Main.config.officerRole, true)[0])) continue
                    }
                    args.remove(args[0])
                    event.message.delete().queue({ cmd.execute(event, args) }, { cmd.execute(event, args) })
                }
            }
        }
    }

    private fun permsValid(event: MessageReceivedEvent, role: Role): Boolean {
        if (!event.member!!.roles.contains(role)) {
            UtilEmbed.result(event, "Insufficient permissions to run this command!", success = false)
            return false
        }
        return true
    }
}