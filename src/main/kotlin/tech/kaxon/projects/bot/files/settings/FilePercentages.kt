package tech.kaxon.projects.bot.files.settings

import tech.kaxon.projects.bot.files.types.BasicGsonFile

class FilePercentages : BasicGsonFile("settings/percentages.json") {
    var owner = 10.0
    var advertisers = 18.0
    var boosters = 67.0
    var raidLeaders = 5.0
}