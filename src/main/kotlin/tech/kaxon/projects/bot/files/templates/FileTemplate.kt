package tech.kaxon.projects.bot.files.templates

import net.dv8tion.jda.api.entities.MessageEmbed
import tech.kaxon.projects.bot.files.types.BasicGsonFile

class FileTemplate(@Transient val file: String) : BasicGsonFile("templates/$file", true) {
    var submitEmbed = MessageEmbed(null, null, null, null, null, 0, null, null, null, null, null, null, null)
    var createEmbed = MessageEmbed(null, null, null, null, null, 0, null, null, null, null, null, null, null)
}