package dev.walk.gs.layla.utils

enum class ParseType {
    LONG, DOUBLE
}

fun Array<String>.join(at: Int, separator: String = " ") = toList().subList(at, size).joinToString(separator)

fun parse(type: ParseType, number: String): MultiValue<Long, Double> {
    if (type == ParseType.LONG) {
        try {
            val value = java.lang.Long.parseLong(number)
            return MultiValue(value, value.toDouble())
        } catch (e: Exception) {
        }

    } else if (type == ParseType.DOUBLE) {
        try {
            val value = java.lang.Double.parseDouble(number)
            return MultiValue(value.toLong(), value)
        } catch (e1: Exception) {
        }

    }
    return MultiValue(-1L, -1.0)
}