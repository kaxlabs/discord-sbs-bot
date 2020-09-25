package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.utils.UtilAcronymConverter
import tech.kaxon.projects.bot.utils.UtilEmbed
import tech.kaxon.projects.bot.utils.UtilRegex
import java.math.BigDecimal

class CmdTotal : Command("total", "<none>", "View the total gold pot.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        event.textChannel.history.retrievePast(100).queue { messages ->
            var total = BigDecimal.ZERO
            for (message in messages) {
                if (message.contentRaw.contains(bot.config.prefix + this.name) || message.contentRaw.isBlank()) continue
                if (message.contentRaw.contains("---") && !message.contentRaw.contains(this.name)) break
                val find = UtilRegex.getRegex(message.contentRaw, ".+gold.+\\s+(\\d+.?(?:\\.\\d+[KM])?(?=\\s|\$|[^0-9]))", 1)
                if (find.isNotBlank()) total = total.add(BigDecimal(UtilAcronymConverter.convertBigDecimal(find)))
            }
            if (total == BigDecimal.ZERO) {
                UtilEmbed.result(event, "Nothing to add found!", success = false)
                return@queue
            }
            UtilEmbed.result(event, "Total: **${UtilAcronymConverter.reverseBigDecimal(total)}**", delay = 0)
        }
    }
}