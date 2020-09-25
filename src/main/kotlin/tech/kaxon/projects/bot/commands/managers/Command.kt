package tech.kaxon.projects.bot.commands.managers

import com.jagrosh.jagtag.Parser
import com.jagrosh.jagtag.ParserBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tech.kaxon.projects.bot.main.Main
import tech.kaxon.projects.bot.utils.UtilEmbed
import tech.kaxon.projects.bot.utils.UtilJagTag

open class Command(val name: String, val args: String = "", val description: String = "", private val error: Pair<String, String> = Pair("", "")) {
    val bot = Main
    val em = UtilEmbed.em
    val parser: Parser = ParserBuilder().addMethods(UtilJagTag.methods).setMaxOutput(2048).setMaxIterations(1000).build()

    open fun execute(event: MessageReceivedEvent, args: MutableList<String>) {}

    fun getChannelID(channel: String): String {
        return channel.replace("<#(\\d+)>".toRegex(), "$1")
    }

    fun isChannel(id: String): Boolean {
        return id.matches("\\d{17,22}".toRegex())
    }

    fun showError(event: MessageReceivedEvent) {
        if (this.error.second.isEmpty()) UtilEmbed.result(event, this.error.first, success = false)
        else UtilEmbed.result(event, null, this.error.first, this.error.second, success = false)
    }

    fun argsToString(list: MutableList<String>): String {
        val sbName = StringBuilder()
        for (word in list) {
            sbName.append(word).append(" ")
        }
        return sbName.toString().trim()
    }

    fun checkArgsSize(event: MessageReceivedEvent, args: MutableList<String>, amount: Int, invalid: Boolean = false): Boolean {
        if (args.size < amount) {
            if (invalid) UtilEmbed.result(event, null, "Not enough arguments!", "**${Main.config.prefix}$name** ${this.args}", false, delay = 10)
            return true
        }
        return false
    }
}