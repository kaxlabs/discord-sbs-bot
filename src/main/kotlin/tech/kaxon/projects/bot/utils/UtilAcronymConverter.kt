package tech.kaxon.projects.bot.utils

import tech.kaxon.projects.bot.main.Main
import java.math.BigDecimal
import java.text.DecimalFormat

object UtilAcronymConverter {
    fun convertBigDecimal(number: String): String {
        if (number.length <= 1) return "0"
        val last = number[number.length - 1]
        var value = if (number.length > 1) {
            BigDecimal(number.substring(0, number.length - 1))
        } else {
            BigDecimal(number)
        }
        when (last.toLowerCase()) {
            'k' -> value = value.multiply(BigDecimal("1000"))
            'm' -> value = value.multiply(BigDecimal("1000000"))
        }
        return value.toString()
    }

    fun reverseBigDecimal(number: BigDecimal): String {
        val value: BigDecimal
        val result: String
        when {
            number.divide(BigDecimal("1000000")) < BigDecimal.ONE -> {
                value = number.divide(BigDecimal("1000"))
                result = Main.decimalFormatter.format(value) + "K"
            }
            number.divide(BigDecimal("1000000")) >= BigDecimal.ONE -> {
                value = number.divide(BigDecimal("1000000"))
                result = DecimalFormat("0.#####").format(value) + "M"
            }
            else -> throw NullPointerException("Couldn't reverse value!")
        }
        return result
    }

    fun formatInt(number: String): Int {
        if (number.length <= 1) return 0
        val last = number[number.length - 1]
        var value = if (number.length > 1) {
            number.substring(0, number.length - 1).toInt()
        } else {
            number.toInt()
        }
        when (last.toLowerCase()) {
            'k' -> value *= 1000
            'm' -> value *= 1000000
        }
        return value
    }
}