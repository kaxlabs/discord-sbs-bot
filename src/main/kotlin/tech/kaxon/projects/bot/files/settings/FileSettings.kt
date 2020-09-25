package tech.kaxon.projects.bot.files.settings

import tech.kaxon.projects.bot.files.types.BasicGsonFile

class FileSettings : BasicGsonFile("settings/settings.json") {
    var chatFilter = true
}