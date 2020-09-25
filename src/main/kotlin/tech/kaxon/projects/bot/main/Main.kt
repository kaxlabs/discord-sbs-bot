package tech.kaxon.projects.bot.main

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.slf4j.LoggerFactory
import tech.kaxon.projects.bot.commands.*
import tech.kaxon.projects.bot.files.BasicFiles
import tech.kaxon.projects.bot.files.FileConfig
import tech.kaxon.projects.bot.files.channels.*
import tech.kaxon.projects.bot.files.settings.FileGiveaways
import tech.kaxon.projects.bot.files.settings.FilePercentages
import tech.kaxon.projects.bot.files.settings.FileSettings
import tech.kaxon.projects.bot.files.templates.FileTemplate
import tech.kaxon.projects.bot.files.types.BasicGsonFile
import tech.kaxon.projects.bot.gson.InterfaceAdapter
import tech.kaxon.projects.bot.main.checks.*
import tech.kaxon.projects.bot.utils.UtilEmbed
import java.io.File
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.util.*

fun main() {
    Locale.setDefault(Locale.US)
    if (!Main.configDir.exists()) Main.configDir.mkdir()
    val intents = listOf(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MESSAGE_REACTIONS)
    JDABuilder.createLight(Main.config.token, intents).setMemberCachePolicy(MemberCachePolicy.NONE).addEventListeners(Main()).build()
}

class Main : ListenerAdapter() {
    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)

        const val botName = "SBS BOT"

        val configDir = File(File(Main::class.java.protectionDomain.codeSource.location.toURI()).parentFile.path + File.separatorChar + botName.toLowerCase().replace(" ", "_") + File.separatorChar)
        val tokensDir = File(configDir, "tokens")
        val credentialsFile = File(configDir, "credentials.json")

        val gson: Gson = GsonBuilder().registerTypeAdapter(BasicGsonFile::class.java, InterfaceAdapter<BasicGsonFile>()).setPrettyPrinting().create()
        val decimalFormatter = DecimalFormat("0.##")

        var config = BasicGsonFile.load<FileConfig>(FileConfig())
        var settings = BasicGsonFile.load<FileSettings>(FileSettings())

        var bmahEmbeds = BasicGsonFile.load<FileTemplate>(FileTemplate("bmah.json"))
        var paymentEmbeds = BasicGsonFile.load<FileTemplate>(FileTemplate("payment.json"))
        var tokensEmbeds = BasicGsonFile.load<FileTemplate>(FileTemplate("tokens.json"))

        var logChannel = BasicGsonFile.load<FileLogChannel>(FileLogChannel())
        var balanceChannels = BasicGsonFile.load<FileBalanceChannels>(FileBalanceChannels())

        var normalChannels = BasicGsonFile.load<FileNormalChannels>(FileNormalChannels())
        var heroicChannels = BasicGsonFile.load<FileHeroicChannels>(FileHeroicChannels())
        var mythicChannels = BasicGsonFile.load<FileMythicChannels>(FileMythicChannels())

        var ticketChannels = BasicGsonFile.load<FileTicketChannels>(FileTicketChannels())

        var giveaways = BasicGsonFile.load<FileGiveaways>(FileGiveaways())
        var percentages = BasicGsonFile.load<FilePercentages>(FilePercentages())

        var words = BasicFiles.TextFileWords()
        var roles = BasicFiles.TextFileRoles()
        var giveaway = BasicFiles.TextFileGiveaway()

        val commands =
                arrayOf(/*CmdDebug(), */CmdHelp(), CmdPrefix(), CmdLogChannel(), CmdBalanceChannels(), CmdEmbed(), CmdSaveTicket(), CmdTickets(), CmdChatFilter(), CmdGiveaway(), CmdNormal(), CmdHeroic(), CmdMythic(), CmdTotal(),
                        CmdBalance(), CmdEarned(), CmdPaid(), CmdPenalty())
    }

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        Channels().checkChannels(event)
        Tickets().checkTickets(event)
        Giveaways().checkGiveaway(event)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val msgRaw = event.message.contentRaw
        if (event.author.isBot) return
        Command().checkCommand(event, msgRaw)
        ChatFilter().checkFilter(event, msgRaw)
    }

    override fun onReady(event: ReadyEvent) {
        UtilEmbed.resetEmbed()
        LoggerFactory.getLogger("Main").info("Currently logged into ${event.jda.guilds.size} guilds. Ready.")
    }
}