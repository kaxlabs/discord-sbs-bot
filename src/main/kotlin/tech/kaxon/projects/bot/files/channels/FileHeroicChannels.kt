package tech.kaxon.projects.bot.files.channels

import tech.kaxon.projects.bot.files.types.BasicGsonFile
import java.util.*

class FileHeroicChannels : BasicGsonFile("channels/heroic_channels.json") {
    var allianceBoosterChannels = LinkedList<Long>()
    var allianceAdvertiserChannels = LinkedList<Long>()
    var hordeBoosterChannels = LinkedList<Long>()
    var hordeAdvertiserChannels = LinkedList<Long>()

    fun clear() {
        allianceBoosterChannels.clear()
        allianceAdvertiserChannels.clear()
        hordeBoosterChannels.clear()
        hordeAdvertiserChannels.clear()
    }
}