import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@Composable
fun BengaliCalendarScreen() {
    val today = Calendar.getInstance()
    val currentMonth = today.get(Calendar.MONTH)
    val currentYear = today.get(Calendar.YEAR)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        MonthHeader(month = currentMonth, year = currentYear)
        Spacer(modifier = Modifier.height(16.dp))
        CalendarGrid(month = currentMonth, year = currentYear, today = today)
    }
}

@Composable
fun MonthHeader(month: Int, year: Int) {
    val monthNames = listOf(
        "Boishakh", "Joishtho", "Ashar", "Srabon", "Bhadro", "Ashwin",
        "Kartik", "Ogrohayon", "Poush", "Magh", "Falgun", "Chaitra"
    )
    // Bengali months start in mid-April, so adjust accordingly
    val bengaliMonthIndex = (month + 8) % 12
    val bengaliYear = year - 593 // Approximate conversion

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        BasicText(
            text = "${monthNames[bengaliMonthIndex]} $bengaliYear",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun CalendarGrid(month: Int, year: Int, today: Calendar) {
    val daysInMonth = getDaysInMonth(month, year)
    val firstDayOfWeek = getFirstDayOfWeek(month, year)
    val weeks = mutableListOf<List<Int?>>()
    var day = 1
    for (week in 0 until 6) {
        val weekDays = mutableListOf<Int?>()
        for (d in 0 until 7) {
            if (week == 0 && d < firstDayOfWeek || day > daysInMonth) {
                weekDays.add(null)
            } else {
                weekDays.add(day)
                day++
            }
        }
        weeks.add(weekDays)
    }

    val weekDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        weekDays.forEach {
            BasicText(
                text = it,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    weeks.forEach { week ->
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            week.forEach { dayNum ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (dayNum != null) {
                        val isToday = dayNum == today.get(Calendar.DAY_OF_MONTH) &&
                                month == today.get(Calendar.MONTH) &&
                                year == today.get(Calendar.YEAR)
                        DayCell(
                            day = dayNum,
                            month = month,
                            year = year,
                            isToday = isToday
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(day: Int, month: Int, year: Int, isToday: Boolean) {
    val (bengaliDay, bengaliMonth, bengaliYear) = convertToBengaliDate(day, month, year)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (isToday) Modifier.background(Color(0xFFB3E5FC), CircleShape) else Modifier
    ) {
        BasicText(
            text = "$day",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (isToday) Color.Blue else Color.Black,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
        )
        BasicText(
            text = "${bengaliDay},
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isToday) Color.Red else Color.DarkGray
            )
        )
    }
}

// Helper functions

fun getDaysInMonth(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun getFirstDayOfWeek(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    return calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
}

// Simple Bengali date conversion (approximate)
fun convertToBengaliDate(day: Int, month: Int, year: Int): Triple<Int, String, Int> {
    // Bengali months start mid-April, so adjust accordingly
    val bengaliMonths = listOf(
        "Boishakh", "Joishtho", "Ashar", "Srabon", "Bhadro", "Ashwin",
        "Kartik", "Ogrohayon", "Poush", "Magh", "Falgun", "Chaitra"
    )
    val startMonth = 3 // April (0-based)
    val startDay = 14

    var bengaliYear = year - 593
    var bengaliMonth = (month - startMonth + 12) % 12
    var bengaliDay = day

    if (month == startMonth && day < startDay) {
        bengaliYear -= 1
        bengaliMonth = 11 // Chaitra
        bengaliDay = day + 30 - startDay + 1
    } else if (month < startMonth || (month == startMonth && day < startDay)) {
        bengaliYear -= 1
    } else if (day >= startDay) {
        bengaliDay = day - startDay + 1
    }

    return Triple(bengaliDay, bengaliMonths[bengaliMonth], bengaliYear)
}