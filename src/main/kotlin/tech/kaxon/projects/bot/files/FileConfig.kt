package tech.kaxon.projects.bot.files

import tech.kaxon.projects.bot.files.types.BasicGsonFile

class FileConfig : BasicGsonFile("/config.json") {
    val token = ""
    var prefix = "--"
    var owner = "SBS"
    var ownerID = 0L
    var raidLeaderRole = "Raid Leader"
    var officerRole = "Officer"
    var spreadsheetURL = ""
    var allianceSheetGID = 0
    var allianceSheetAdGID = 0
    var hordeSheetGID = 0
    var hordeSheetAdGID = 0
}
