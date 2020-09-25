package tech.kaxon.projects.bot.files.channels

import tech.kaxon.projects.bot.files.types.BasicGsonFile
import java.util.*

class FileMythicChannels : BasicGsonFile("channels/mythic_channels.json") {
    var allianceChannels = LinkedList<Long>()
    var hordeChannels = LinkedList<Long>()

    fun clear() {
        allianceChannels.clear()
        hordeChannels.clear()
    }
}