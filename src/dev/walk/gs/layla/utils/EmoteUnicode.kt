package dev.walk.gs.layla.utils

enum class EmoteUnicode(var emote: String) {
    Number_1("U+31U+20E3"),
    Number_2("U+32U+20E3"),
    Number_3("U+33U+20E3"),
    Number_4("U+34U+20E3"),
    Number_5("U+35U+20E3"),
    Number_6("U+36U+20E3"),
    Number_7("U+37U+20E3"),
    Number_8("U+38U+20E3"),
    Number_9("U+39U+20E3"),
}

class EmoteUnicodes {

    companion object {
        fun unicodeNumber(value: Int): String? {
            var number = value
            if (number < 1) {
                number = 1
            } else if (number > 10) {
                number = 10
            }
            return try {
                EmoteUnicode.valueOf("Number_$number").emote
            } catch (e: Exception) {
                null
            }
        }
    }

}


