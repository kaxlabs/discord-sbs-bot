package tech.kaxon.projects.bot.utils

import java.util.*
import java.util.regex.Pattern

object UtilRegex {
    fun getBefore(string: String, after: List<Char>): String {
        val copy = java.lang.StringBuilder()
        for (element in string) {
            if (after.contains(element)) break
            copy.append(element)
        }
        return copy.toString()
    }

    fun getBetween(string: String, before: List<Char>, after: List<Char>): String {
        val copy = java.lang.StringBuilder()
        var found = false
        for (element in string) {
            if (!found && before.contains(element)) {
                found = true
            } else if (found) {
                if (after.contains(element)) break
                copy.append(element)
            }
        }
        return copy.toString()
    }

    fun extractInfo(text: String, regex1: String, regex2: String): List<String> {
        var text1 = text
        val pattern = Pattern.compile(regex1)
        val matcher = pattern.matcher(text1)
        if (matcher.find()) {
            text1 = matcher.group(1)
        }
        return extractRegex(text1, regex2).toList()
    }

    fun extractRegex(text: String, regex: String): List<String> {
        val found = ArrayList<String>()
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            found.add(text.substring(matcher.start(0), matcher.end(0)))
        }
        return found
    }

    fun getRegex(text: String, regex: String, option: Int): String {
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(text)
        return if (matcher.find()) {
            matcher.group(option)
        } else ""
    }
}