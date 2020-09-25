package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdHelp : Command("help") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        em.setTitle("Help Menu 📘")
        for (cmd in bot.commands) {
            if (cmd.description.isEmpty()) continue
            if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
                val totalCommand = cmd is CmdTotal
                val balanceCommands = cmd is CmdEarned || cmd is CmdPaid || cmd is CmdPenalty
                val generalCommands = cmd is CmdHelp || cmd is CmdBalance
                if (!totalCommand && !balanceCommands && !generalCommands) continue
                if (totalCommand && !permsValid(event, event.guild.getRolesByName(Main.config.raidLeaderRole, true)[0])) continue
                if (balanceCommands && !permsValid(event, event.guild.getRolesByName(Main.config.officerRole, true)[0])) continue
            }
            em.addField("${Main.config.prefix}${cmd.name} ${cmd.args}", cmd.description, true)
        }
        UtilEmbed.result(event)
    }

    private fun permsValid(event: MessageReceivedEvent, role: Role): Boolean {
        if (!event.member!!.roles.contains(role)) return false
        return true
    }
}