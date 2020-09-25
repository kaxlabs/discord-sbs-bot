package tech.kaxon.projects.bot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.commands.managers.Command
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.UtilEmbed

class CmdSaveTicket : Command("saveticket", "bmah|payment|tokens submit|create ", "Save the previous embed into a ticket template.") {
    override fun execute(event: MessageReceivedEvent, args: MutableList<String>) {
        if (checkArgsSize(event, args, 2, true)) return
        event.channel.history?.retrievePast(1)?.queue { messages ->
            val embed = messages[0].embeds[messages[0].embeds.size - 1]
            when (args[1].toLowerCase()) {
                "submit" -> {
                    when (args[0].toLowerCase()) {
                        "bmah" -> {
                            Main.bmahEmbeds.submitEmbed = embed
                            Main.bmahEmbeds.save()
                        }
                        "payment" -> {
                            Main.paymentEmbeds.submitEmbed = embed
                            Main.paymentEmbeds.save()
                        }
                        "tokens" -> {
                            Main.tokensEmbeds.submitEmbed = embed
                            Main.tokensEmbeds.save()
                        }
                        else -> {
                            UtilEmbed.result(event, "Invalid type! Valid types: `bmah, payment, token`", success = false)
                            return@queue
                        }
                    }
                }
                "create" -> {
                    when (args[0].toLowerCase()) {
                        "bmah" -> {
                            Main.bmahEmbeds.createEmbed = embed
                            Main.bmahEmbeds.save()
                        }
                        "payment" -> {
                            Main.paymentEmbeds.createEmbed = embed
                            Main.paymentEmbeds.save()
                        }
                        "tokens" -> {
                            Main.tokensEmbeds.createEmbed = embed
                            Main.tokensEmbeds.save()
                        }
                        else -> {
                            UtilEmbed.result(event, "Invalid type! Valid types: `bmah, payment, token`", success = false)
                            return@queue
                        }
                    }
                }
                else -> {
                    UtilEmbed.result(event, "Invalid argument! Valid arguments: `submit, create`", success = false)
                    return@queue
                }
            }
            UtilEmbed.result(event, "Saved ${args[1].capitalize()} embed!", success = true, delay = 5)
        }
    }
}