package tech.kaxon.projects.bot.commands

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.sheets.v4.Sheets
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.UtilEmbed
import tech.kaxon.projects.bot.utils.sheets.UtilSheets
import java.awt.Color

class CmdBalance : Command("balance", "<none>", "Check your gold balances on the spreadsheet.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        val service = Sheets.Builder(HTTP_TRANSPORT, UtilSheets.JSON_FACTORY, UtilSheets.getCredentials(HTTP_TRANSPORT)).setApplicationName(UtilSheets.APPLICATION_NAME).build()
        val valueRangeAlliance = UtilSheets.getValueRange(service, Main.config.allianceSheetGID, "B3:E50000")
        val valueRangeHorde = UtilSheets.getValueRange(service, Main.config.hordeSheetGID, "B3:E50000")
        val alliance: Double? = UtilSheets.getUserDataByName(valueRangeAlliance, event.member!!.effectiveName)?.get()?.earned
        val horde: Double? = UtilSheets.getUserDataByName(valueRangeHorde, event.member!!.effectiveName)?.get()?.earned
        UtilEmbed.em.setDescription(event.author.asMention)
        if (alliance == null && horde == null) UtilEmbed.em.setDescription("No balances found!")
        if (alliance != null) UtilEmbed.em.addField("${event.member!!.effectiveName}'s Alliance Gold", alliance.toString(), true)
        if (horde != null) UtilEmbed.em.addField("${event.member!!.effectiveName}'s Horde Gold", horde.toString(), true)
        UtilEmbed.result(event, color = Color.YELLOW, delay = 10)
    }
}