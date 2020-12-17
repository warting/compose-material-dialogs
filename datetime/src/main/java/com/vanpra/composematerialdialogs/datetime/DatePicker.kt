package com.vanpra.composematerialdialogs.datetime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.MaterialDialog
import java.time.LocalDate
import java.time.YearMonth

val dateBoxDp = 35.dp

/**
 * @brief A date picker body layout
 *
 * @param initialDate The time to be shown to the user when the dialog is first shown.
 * Defaults to the current date if this is not set
 * @param onComplete callback with a LocalDateTime object when the user completes their input
 * @param onCancel callback when the user cancels the dialog
 */
@Composable
fun MaterialDialog.datepicker(
    initialDate: LocalDate = LocalDate.now(),
    onCancel: () -> Unit = {},
    onComplete: (LocalDate) -> Unit = {}
) {
    val currentDate = remember { initialDate }
    val selectedDate = remember { mutableStateOf(currentDate) }

    DatePickerLayout()

    buttons {
        positiveButton("Ok") {
            onComplete(selectedDate.value)
        }
        negativeButton("Cancel") {
            onCancel()
        }
    }
}

@Composable
internal fun DatePickerLayout() {
    /* Height doesn't include button height */
    Column(
        Modifier.height(460.dp).width(328.dp)
    ) {
        CalendarHeader()
        CalendarViewHeader()
        CalendarView()
    }
}

@Composable
fun CalendarViewHeader() {
    Box(Modifier.padding(top = 16.dp, bottom = 16.dp).height(24.dp).fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp)) {
            Row(Modifier.fillMaxHeight().align(Alignment.CenterStart)) {
                Text(
                    "December 2020",
                    modifier = Modifier.paddingFromBaseline(top = 16.dp)
                        .wrapContentSize(Alignment.Center),
                    style = TextStyle(fontSize = 14.sp, fontWeight = W600),
                    color = MaterialTheme.colors.onBackground
                )

                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    Image(
                        Icons.Default.ArrowDropDown,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
                    )
                }
            }

            Row(Modifier.fillMaxHeight().align(Alignment.CenterEnd)) {
                Image(
                    Icons.Default.KeyboardArrowLeft,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
                )
                
                Spacer(modifier = Modifier.width(24.dp))
                
                Image(
                    Icons.Default.KeyboardArrowRight,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
                )
            }
        }
    }
}

@Composable
fun CalendarView() {
    Column(
        Modifier.padding(start = 12.dp, end = 12.dp)
    ) {
        DayOfWeekHeader()
        val month = getDates(LocalDate.now())
        for (y in 0..5) {
            Row(
                modifier = Modifier.height(40.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (x in 0 until 7) {
                    val day = month[y * 7 + x]
                    if (day != -1) {
                        DateSelectionBox(day)
                    } else {
                        Box(Modifier.size(40.dp))
                    }

                    if (x != 6) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DateSelectionBox(date: Int) {
    Box(
        Modifier.size(40.dp).clickable(
            onClick = {
//                selected.value = LocalDate.of(yearMonth.year, yearMonth.month, it)
            },
            indication = null
        ), contentAlignment = Alignment.Center
    ) {
        Text(
            date.toString(),
            modifier = Modifier.size(36.dp)
                .clickable(
                    onClick = {
//                        selected.value = LocalDate.of(yearMonth.year, yearMonth.month, it)
                    },
                    indication = null
                )
//                .then(selectedModifier)
                .wrapContentSize(Alignment.Center),
            color = MaterialTheme.colors.onBackground,
//            style = textStyle
        )
    }
}

@Composable
fun DayOfWeekHeader() {
    Row(
        modifier = Modifier.height(40.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEachIndexed { index, it ->
            Box(Modifier.size(40.dp)) {
                Text(
                    it,
                    modifier = Modifier.alpha(0.8f).fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    style = TextStyle(fontSize = 14.sp, fontWeight = W600),
                    color = MaterialTheme.colors.onBackground
                )
            }
            if (index != 6) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

// Input: Selected Date
@Composable
fun CalendarHeader() {
    Box(Modifier.background(MaterialTheme.colors.primaryVariant).fillMaxWidth().height(120.dp)) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp)) {
            Text(
                text = "SELECT DATE",
                modifier = Modifier.paddingFromBaseline(top = 32.dp),
                color = MaterialTheme.colors.onPrimary,
                style = TextStyle(fontSize = 12.sp)
            )
            Box(Modifier.fillMaxWidth().paddingFromBaseline(top = 64.dp)) {
                Text(
                    text = "Mon, Nov 17",
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = MaterialTheme.colors.onPrimary,
                    style = TextStyle(fontSize = 30.sp, fontWeight = W400)
                )

                Image(
                    Icons.Default.Edit,
                    modifier = Modifier.size(24.dp).align(Alignment.CenterEnd),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
                )
            }

        }
    }
}

@Preview
@Composable
fun DateDialogPreview() {
    DatePickerLayout()
}

private fun getDates(date: LocalDate): List<Int> {
    val dates = mutableListOf<Int>()

    val firstDate = LocalDate.of(date.year, date.monthValue, 1)
    val firstDay = firstDate.dayOfWeek.value - 1
    val numDays = date.month.length(firstDate.isLeapYear)

    var counter = 1
    for (y in 0..5) {
        for (x in 0..6) {
            if ((y == 0 && x < firstDay) || counter > numDays) {
                dates.add(-1)
            } else {
                dates.add(counter)
                counter += 1
            }
        }
    }

    return dates
}
