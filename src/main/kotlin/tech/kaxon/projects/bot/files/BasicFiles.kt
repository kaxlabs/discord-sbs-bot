package tech.kaxon.projects.bot.files

import tech.kaxon.projects.bot.files.types.BasicTextFile

class BasicFiles {
    class TextFileWords : BasicTextFile("settings/chat_filter/word-whitelist.txt")
    class TextFileRoles : BasicTextFile("settings/chat_filter/role-whitelist.txt")
    class TextFileGiveaway : BasicTextFile("files/giveaway.txt")
}