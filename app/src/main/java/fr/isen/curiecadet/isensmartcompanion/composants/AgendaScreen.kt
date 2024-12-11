package fr.isen.curiecadet.isensmartcompanion.composants

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import kotlin.random.Random

data class Course(
    val title: String,
    val date: String,
    val location: String
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaScreen() {
    val yearCourses = remember { generateYearCourses() }
    var selectedDateCourses by remember { mutableStateOf<List<Course>>(emptyList()) }
    var showCourseDetails by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Calendrier Annuel",
            style = MaterialTheme
                .typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(yearCourses) { monthCourses ->
                MonthView(monthCourses) { _, courses ->
                    selectedDateCourses = courses
                    showCourseDetails = true
                }
            }
        }

        if (showCourseDetails) {
            CourseDetailsDialog(courses = selectedDateCourses, onDismiss = { showCourseDetails = false })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateYearCourses(): List<List<Course>> {
    val currentYear = LocalDate.now().year
    val months = Month.entries.toTypedArray()
    val courseTitles = mapOf(
        DayOfWeek.MONDAY to "Cours de Mathématiques",
        DayOfWeek.TUESDAY to "Cours de Physique",
        DayOfWeek.WEDNESDAY to "Cours de Chimie",
        DayOfWeek.THURSDAY to "Cours d'Informatique",
        DayOfWeek.FRIDAY to "Cours de Biologie"
    )

    return months.map { month ->
        val daysInMonth = month.length(isLeapYear(currentYear))

        (1..daysInMonth).mapNotNull { day ->
            val date = LocalDate.of(currentYear, month, day)
            if (date.dayOfWeek in listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
                val title = courseTitles[date.dayOfWeek] ?: "Cours ${Random.nextInt(100)}"
                Course(
                    title = title,
                    date = date.toString(),
                    location = "Salle ${Random.nextInt(1, 5)}"
                )
            } else null
        }
    }
}

fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthView(courses: List<Course>, onDayClick: (LocalDate, List<Course>) -> Unit) {
    val monthName = courses.firstOrNull()?.date?.let { date ->
        LocalDate.parse(date).month.name
    } ?: "Mois"

    val eventDays = remember { listOf(LocalDate.of(2024, Month.JANUARY, 10), LocalDate.of(2024, Month.JANUARY, 15)) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = monthName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        val daysInMonth = LocalDate.parse(courses.firstOrNull()?.date).month.length(true)
        val startDayOfWeek = LocalDate.of(LocalDate.now().year, Month.valueOf(monthName), 1).dayOfWeek

        val emptyCellsBeforeMonthStart = startDayOfWeek.ordinal

        Column {
            var dayCounter = 1
            for (i in 0..(emptyCellsBeforeMonthStart + daysInMonth + 6) / 7) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (j in 0 until 7) {
                        if ((i == 0 && j >= emptyCellsBeforeMonthStart) || (i > 0 && dayCounter <= daysInMonth)) {
                            val date = LocalDate.of(LocalDate.now().year, Month.valueOf(monthName), dayCounter)
                            val courseForDay = courses.filter { it.date == date.toString() }
                            val backgroundColor = if (date in eventDays) Color(0xFFE0F7FA) else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(6.dp)
                                    .background(
                                        if (courseForDay.isNotEmpty()) Color(0xFFEFEFEF) else backgroundColor,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onDayClick(date, courseForDay)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$dayCounter",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                )
                            }
                            dayCounter++
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseDetailsDialog(courses: List<Course>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Détails des cours")
        },
        text = {
            if (courses.isEmpty()) {
                Text("Aucun cours pour cette date.")
            } else {
                LazyColumn {
                    items(courses) { course ->
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = course.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "Lieu : ${course.location}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}
