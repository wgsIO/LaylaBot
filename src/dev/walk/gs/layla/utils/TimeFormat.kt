package carbon.walk.zking.core.util

import carbon.walk.zking.core.util.DateType.*
import java.text.SimpleDateFormat
import java.util.*

val days = listOf("domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado")
val months = listOf("janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro")

enum class DateType(val value: Int, val startString: String, val simple: String, vararg val partial: String) {
    SECONDS(1, " e ", "s", "segundo", "segundos"),
    MINUTES(60, ", ", "m", "minuto", "minutos"),
    HOURS(3600, ", ", "h", "hora", "horas"),
    DAYS(86400, ", ", "d", "dia", "dias"),
    MONTHS(2592000, ", ", "mm", "mês", "meses"),
    YEARS(31104000, ",  e ", "a", "ano", "anos")
}

object TimeFormat {

    private fun Long.aloc() = if (this > 1) 1 else 0
    fun Long.format() = if (this > 9) "$this" else "0$this"
    private fun date() = Date()

    @JvmStatic
    fun getTimeSmall(timer: Long): String? {
        val _time = getTiming(timer)
        val time = DateType.values().reversed().firstOrNull { _time[it]!! > 0 }
        val value = _time[time]!!
        return "$value ${time!!.partial[value.aloc()]}"
    }

    @JvmStatic
    fun getTimeSimplified(timer: Long): String? {
        val _time = getTiming(timer)
        val time = DateType.values().reversed().firstOrNull { _time[it]!! > 0 }
        val value = _time[time]!!
        return "$value${time!!.simple}"
    }

    @JvmStatic
    fun getTiming(valueTime: Long): Map<DateType, Long> {
        var time = valueTime
        val times = hashMapOf<DateType, Long>()
        DateType.values().reversed().forEach {
            val _time = time / it.value; time -= _time * it.value
            times.putIfAbsent(it, _time)
        }
        return times
    }

    @JvmStatic
    fun getBrDate(time: Long): String {
        val timing = getTiming(time)
        if (time == 0L || listOf(YEARS, MONTHS, DAYS).any { timing[it]!!.toInt() < 1 }) return "Without timing exception"
        val _month = timing[MONTHS]!!.toInt()
        val time = GregorianCalendar().apply { set(timing[YEARS]!!.toInt(), _month, timing[DAYS]!!.toInt()) }
        val month = months[_month - 1]
        val day_of_week = days[time.get(Calendar.DAY_OF_WEEK) - 1]
        return "$day_of_week, ${timing[DAYS]} de $month de ${timing[YEARS]}"
    }

    @JvmStatic
    fun getDate(time: Long): String {
        if (time == 0L) return "Without timing exception"
        val result = getTiming(time)
        return "${listOf(result[DAYS]?.format(), result[MONTHS]?.format(), result[YEARS]?.format()).joinToString("/")} ás ${listOf(result[HOURS]?.format(), result[MINUTES]?.format(), result[SECONDS]?.format()).joinToString(":")}"
    }

    @JvmStatic
    fun getCurrentTime(): Long {
        var seconds = 0L
        val form = SimpleDateFormat("ss:mm:HH:dd:MM:yyyy").format(date()).split(":")
        DateType.values().forEach {
            val a = form[it.ordinal].toLong()
            if (a > 0) seconds += it.value * a
        }
        return seconds
    }

    @JvmStatic
    fun getTimeString(timer: Long): String {
        if (timer == 0L) return "Without timing exception"
        var result = ""
        val time = getTiming(timer)
        DateType.values().reversed().forEach {
            val _a = time.get(it)
            if (_a!! > 0L) result += "${it.startString}$_a ${it.partial[_a.aloc()]}"
        }
        result = result.replaceFirst(", ", "")
        if (result.startsWith(" e ", true)) result = result.replaceFirst(" e ", "")
        return result
    }

    @JvmStatic
    fun getTimeStringSimplified(timer: Long): String {
        if (timer == 0L) return "Without timing exception"
        var result = ""
        val time = getTiming(timer)
        DateType.values().reversed().forEach {
            val _a = time.get(it)
            if (_a!! > 0L) result += " $_a${it.simple}"
        }
        result = result.replaceFirst(" ", "")
        //if(result.startsWith(" e ", true)) result = result.replaceFirst(" e ", "")
        return result
    }
}