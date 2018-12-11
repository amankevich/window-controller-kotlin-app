package by.squareroot.windowcontroller.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WindowTimeUtil @Inject constructor(private val locale: Locale) {
    val calendar = GregorianCalendar()
    val format = SimpleDateFormat("kk:mm", locale)

    fun currentHour24() : Int {
        return getHours24(System.currentTimeMillis())
    }

    fun currentMinute() : Int {
        return getMinute(System.currentTimeMillis())
    }

    fun getHours24(time: Long) : Int {
        calendar.timeInMillis = time
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getMinute(time: Long) : Int {
        calendar.timeInMillis = time
        return calendar.get(Calendar.MINUTE)
    }

    fun getTimeFromHour24Minute(hours: Int, minutes: Int): Long {
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val time = calendar.timeInMillis
        if (time > System.currentTimeMillis()) {
            return time / 1000
        } else {
            return (time + 24 * 60 * 60 * 1000) / 1000
        }
    }

    fun toHourMinutesString(time: Long) : String {
        return format.format(Date(time))
    }
}