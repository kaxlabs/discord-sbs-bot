package tech.kaxon.projects.bot.utils.sheets

class UserData(val name: String?, val earned: Double? = null, val paid: Double? = null, val penalty: Double? = null) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Name: ").append(name).append(", ")
        sb.append("Earned: ").append(earned)
        return sb.toString()
    }
}