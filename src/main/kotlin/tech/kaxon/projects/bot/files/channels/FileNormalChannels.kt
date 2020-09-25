package tech.kaxon.projects.bot.files.channels

import tech.kaxon.projects.bot.files.types.BasicGsonFile
import java.util.*

class FileNormalChannels : BasicGsonFile("channels/normal_channels.json") {
    var allianceChannels = LinkedList<Long>()
    var hordeChannels = LinkedList<Long>()

    fun clear() {
        allianceChannels.clear()
        hordeChannels.clear()
    }
}