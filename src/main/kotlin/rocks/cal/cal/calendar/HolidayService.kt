package rocks.cal.cal.calendar

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.util.GregorianCalendar


@Service
class HolidayService {

    private val yearMap = mutableMapOf<Int, Map<LocalDate, String>>()

    fun isHoliday(date: LocalDate): String? =
        yearMap.getOrPut(date.year) { generateHolidays(date.year) }[date]
}

private fun generateHolidays(year: Int): Map<LocalDate, String> {
    val easterSunday = easterDate(year)
    val maundyThursday = easterSunday.minusDays(3)
    val goodFriday = easterSunday.minusDays(2)
    val easterMonday = easterSunday.plusDays(1)
    val ascension = easterSunday.plusDays(39)
    val whitSunday = easterSunday.plusDays(49)
    val whitMonday = easterSunday.plusDays(50)

    return mapOf(
            // Set holidays
            Pair(LocalDate.of(year, 1, 1), "Første nyttårsdag"),
            Pair(LocalDate.of(year, 5, 1), "Arbeidernes dag"),
            Pair(LocalDate.of(year, 5, 17), "Grunnlovsdag"),
            Pair(LocalDate.of(year, 12, 25), "Første juledag"),
            Pair(LocalDate.of(year, 12, 26), "Andre juledag"),

            // Variable holidays
            Pair(maundyThursday, "Skjærtorsdag"),
            Pair(goodFriday, "Langfredag"),
            Pair(easterSunday, "Første påskedag"),
            Pair(easterMonday, "Andre påskedag"),
            Pair(ascension, "Kristi himmelfartsdag"),
            Pair(whitSunday, "Første pinsedag"),
            Pair(whitMonday, "Andre pinsedag")
    )
}

// Black magic: https://en.wikipedia.org/wiki/Computus
private fun easterDate(year: Int): LocalDate {
    val a = year % 19
    val b = year / 100
    val c = year % 100
    val d = b / 4
    val e = b % 4
    val f = (b + 8) / 25
    val g = (b - f + 1) / 3
    val h = (19 * a + b - d - g + 15) % 30
    val i = c / 4
    val k = c % 4
    val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = (a + 11 * h + 22 * l) / 451
    val n = (h + l - 7 * m + 114) / 31
    val p = (h + l - 7 * m + 114) % 31
    val calendar = GregorianCalendar.getInstance()
    calendar.clear()
    calendar.set(year, n - 1, p + 1)
    return calendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}