package tech.kaxon.projects.bot.utils

import java.util.*

object UtilNull {
    fun notNull(vararg args: Any?, action: (LinkedList<String>) -> Unit) {
        when (args.filterNotNull().size) {
            args.size -> {
                val list = LinkedList<String>()
                args.iterator().forEach {
                    list.add(it.toString())
                }
                action(list)
            }
        }
    }
}