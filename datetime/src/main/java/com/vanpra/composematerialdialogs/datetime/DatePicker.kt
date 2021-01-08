package com.vanpra.composematerialdialogs.datetime

import android.graphics.Paint
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.primarySurface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vanpra.composematerialdialogs.MaterialDialog
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.util.*

internal class DatePickerData(val current: LocalDate) {
    var selected by mutableStateOf(current)
}

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
    val datePickerData = remember { DatePickerData(initialDate) }

    DatePickerLayout(datePickerData)

    buttons {
        positiveButton("Ok") {
            onComplete(datePickerData.selected)
        }
        negativeButton("Cancel") {
            onCancel()
        }
    }
}

@Composable
internal fun DatePickerLayout(datePickerData: DatePickerData) {
    /* Height doesn't include datePickerData height */
    Column(
        Modifier
            .height(460.dp)
            .width(328.dp)
    ) {
        CalendarHeader(datePickerData)

        val yearPickerShowing = mutableStateOf(false)
        ViewPager {
            val viewDate = remember(index) { datePickerData.current.plusMonths(index.toLong()) }
            CalendarViewHeader(viewDate, yearPickerShowing)

            Box {
                androidx.compose.animation.AnimatedVisibility(
                    yearPickerShowing.value,
                    Modifier.fillMaxSize().zIndex(0.7f),
                    enter = slideInVertically({ -it }),
                    exit = slideOutVertically({ -it })
                ) {
                    YearPicker()
                }

                CalendarView(viewDate)
            }
        }
    }
}

@Composable
private fun YearPicker() {
    Box(Modifier.zIndex(0.7f).background(MaterialTheme.colors.surface)) {

    }
}

@Composable
private fun ViewPagerScope.CalendarViewHeader(
    viewDate: LocalDate,
    yearPickerShowing: MutableState<Boolean>
) {
    val month = viewDate.month.getDisplayName(FULL, Locale.getDefault())
    val year = viewDate.year

    val yearDropdownIcon = remember(yearPickerShowing.value) {
        if (yearPickerShowing.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
    }

    Box(
        Modifier
            .background(MaterialTheme.colors.primarySurface)
            .padding(top = 16.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
            .height(24.dp)
            .fillMaxWidth()
            .zIndex(1f)
    ) {
        Row(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .clickable(onClick = { yearPickerShowing.value = !yearPickerShowing.value })
        ) {
            Text(
                "$month $year",
                modifier = Modifier
                    .paddingFromBaseline(top = 16.dp)
                    .wrapContentSize(Alignment.Center),
                style = TextStyle(fontSize = 14.sp, fontWeight = W600),
                color = MaterialTheme.colors.onBackground
            )

            Spacer(Modifier.width(4.dp))
            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                Image(
                    yearDropdownIcon,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
                )
            }
        }

        Row(
            Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        ) {
            Image(
                Icons.Default.KeyboardArrowLeft,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = { previous() }),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )

            Spacer(modifier = Modifier.width(24.dp))

            Image(
                Icons.Default.KeyboardArrowRight,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = { next() }),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        }
    }
}

@Composable
private fun CalendarView(viewDate: LocalDate) {
    Column(Modifier.padding(start = 12.dp, end = 12.dp)) {
        DayOfWeekHeader()
        val month = getDates(viewDate)
        for (y in 0..5) {
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
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
        Modifier
            .size(40.dp)
            .clickable(
                onClick = {
//                selected.value = LocalDate.of(yearMonth.year, yearMonth.month, it)
                },
                indication = null
            ), contentAlignment = Alignment.Center
    ) {
        Text(
            date.toString(),
            modifier = Modifier
                .size(36.dp)
                .clickable(
                    onClick = {
//                        selected.value = LocalDate.of(yearMonth.year, yearMonth.month, it)
                    },
                    indication = null
                )
//                .then(selectedModifier)
                .wrapContentSize(Alignment.Center),
            color = MaterialTheme.colors.onBackground,
            style = TextStyle(fontSize = 12.sp)
        )
    }
}

@Composable
private fun DayOfWeekHeader() {
    Row(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEachIndexed { index, it ->
            Box(Modifier.size(40.dp)) {
                Text(
                    it,
                    modifier = Modifier
                        .alpha(0.8f)
                        .fillMaxSize()
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
private fun CalendarHeader(datePickerData: DatePickerData) {
    val month = datePickerData.selected.month.shortLocalName
    val day = datePickerData.selected.dayOfWeek.shortLocalName

    Box(
        Modifier
            .background(MaterialTheme.colors.primaryVariant)
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp)) {
            Text(
                text = "SELECT DATE",
                modifier = Modifier.paddingFromBaseline(top = 32.dp),
                color = MaterialTheme.colors.onPrimary,
                style = TextStyle(fontSize = 12.sp)
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(top = 64.dp)
            ) {
                Text(
                    text = "$day, $month ${datePickerData.selected.dayOfMonth}",
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = MaterialTheme.colors.onPrimary,
                    style = TextStyle(fontSize = 30.sp, fontWeight = W400)
                )

                Image(
                    Icons.Default.Edit,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterEnd),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
                )
            }

        }
    }
}

@Preview
@Composable
fun DateDialogPreview() {
    DatePickerLayout(DatePickerData(LocalDate.now()))
}

private fun getDates(date: LocalDate): List<Int> {
    val dates = mutableListOf<Int>()

    val firstDate = LocalDate.of(date.year, date.monthValue, 1)
    val firstDay = firstDate.dayOfWeek.value % 7
    val numDays = date.month.length(firstDate.isLeapYear)

    var counter = 1
    for (y in 0..5) {
        for (x in 0..6) {
            if ((y == 0 && x < firstDay && firstDay != 0) || counter > numDays) {
                dates.add(-1)
            } else {
                dates.add(counter)
                counter += 1
            }
        }
    }

    return dates
}
