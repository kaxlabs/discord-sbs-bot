package tech.kaxon.projects.bot.files.settings

import tech.kaxon.projects.bot.files.types.BasicGsonFile

class FileGiveaways : BasicGsonFile("settings/giveaways.json") {
    var currentMessageID = 0L
}