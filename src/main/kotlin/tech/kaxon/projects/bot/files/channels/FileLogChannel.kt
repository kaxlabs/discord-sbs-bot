package tech.kaxon.projects.bot.files.channels

import tech.kaxon.projects.bot.files.types.BasicGsonFile

class FileLogChannel : BasicGsonFile("channels/log_channel.json") {
    var logChannel = 0L
}