package com.vanpra.composematerialdialogs.datetime

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * @brief A date time picker dialog
 *
 * @param initialDateTime The date and time to be shown to the user when the dialog is first shown.
 * Defaults to the current date and time if this is not set
 * @param onComplete callback with a LocalDateTime object when the user completes their input
 * @param onCancel callback when the user cancels the dialog
 */
@Composable
fun MaterialDialog.datetimepicker(
    title: String,
    initialDateTime: LocalDateTime = LocalDateTime.now(),
    onCancel: () -> Unit = {},
    onComplete: (LocalDateTime) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val currentDate = initialDateTime.toLocalDate()
    val selectedDate = remember { mutableStateOf(currentDate) }

    val currentTime = remember { initialDateTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES) }
    val selectedTime = remember { mutableStateOf(currentTime) }
    val scrollState = rememberScrollState()
    val columnScrollState = rememberScrollState()

    val scrollTo = remember { mutableStateOf(0) }
    val currentScreen = remember { mutableStateOf(0) }

    BoxWithConstraints {
        Column(
            Modifier
                .heightIn(max = maxHeight * 0.8f)
                .verticalScroll(columnScrollState),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp)
            ) {
                val ratio = scrollState.value / this@BoxWithConstraints.constraints.maxWidth
                Image(
                    Icons.Default.ArrowBack,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clip(CircleShape)
                        .clickable(
                            onClick = {
                                scope.launch { scrollState.animateScrollTo(0) }
                                currentScreen.value = 0
                            }
                        )
                        .alpha(1f * ratio)
                        .wrapContentHeight(Alignment.CenterVertically),
                    contentDescription = null,
                )
                DialogTitle(title)
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .height(10.dp)
            ) {
                val ratio = scrollState.value / this@BoxWithConstraints.constraints.maxWidth
                val color = MaterialTheme.colors.onBackground
                Canvas(modifier = Modifier) {
                    val offset = Offset(30f, 0f)
                    drawCircle(
                        color.copy(0.7f + 0.3f * (1 - ratio)),
                        radius = 8f + 7f * (1 - ratio),
                        center = center - offset
                    )
                    drawCircle(
                        color.copy(0.7f + 0.3f * ratio),
                        radius = 8f + 7f * ratio,
                        center = center + offset
                    )
                }
            }

            scrollTo.value = this@BoxWithConstraints.constraints.maxWidth

            val dpScrollState = rememberScrollState()
            Row(modifier = Modifier.verticalScroll(dpScrollState),
                content = {
                    DatePickerLayout(
                        Modifier
                            .padding(top = 16.dp)
                            .sizeIn(maxWidth = this@BoxWithConstraints.maxWidth, maxHeight = this@BoxWithConstraints.maxHeight),
                        selectedDate,
                        currentDate
                    )
                    TimePickerLayout(
                        Modifier
                            .padding(top = 16.dp)
                            .sizeIn(maxWidth = this@BoxWithConstraints.maxWidth, maxHeight = this@BoxWithConstraints.maxHeight),
                        selectedTime
                    )
                }
            )
        }
    }

    buttons {
        positiveButton(
            text = if (currentScreen.value == 0) {
                "Next"
            } else {
                "Ok"
            },
            disableDismiss = currentScreen.value == 0
        ) {
            if (currentScreen.value == 0) {
                scope.launch {
                    scrollState.animateScrollTo(scrollTo.value)
                    columnScrollState.animateScrollTo(0)
                }
                currentScreen.value = 1
            } else {
                onComplete(LocalDateTime.of(selectedDate.value, selectedTime.value))
            }
        }

        negativeButton("Cancel") {
            onCancel()
        }
    }
}
