package tech.kaxon.projects.bot.files.channels

import tech.kaxon.projects.bot.files.types.BasicGsonFile
import java.util.*

class FileTicketChannels : BasicGsonFile("channels/ticket_channels.json") {
    var staffRole = ""
    var bmahRole = ""
    var paymentRole = ""
    var tokensRole = ""
    var categoryID = 0L
    var bmahChannelID = 0L
    var tokensChannelID = 0L
    var paymentChannelID = 0L
    var channels = LinkedList<Long>()
}