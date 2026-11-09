package com.sikandar.aurajot.presentation.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sikandar.aurajot.domain.model.ChecklistItem
import com.sikandar.aurajot.domain.model.PriorityLevel
import com.sikandar.aurajot.domain.model.TaskItem
import com.sikandar.aurajot.presentation.theme.AuraAmber
import com.sikandar.aurajot.presentation.theme.AuraAmberGlow
import com.sikandar.aurajot.presentation.theme.BentoGlassBg
import com.sikandar.aurajot.presentation.theme.BentoGlassBorder
import com.sikandar.aurajot.presentation.theme.CelestialAzure
import com.sikandar.aurajot.presentation.theme.CrimsonAlert
import com.sikandar.aurajot.presentation.theme.EditorialWhite
import com.sikandar.aurajot.presentation.theme.ObsidianBlack
import com.sikandar.aurajot.presentation.theme.SlateLight
import com.sikandar.aurajot.presentation.theme.SlateMedium
import com.sikandar.aurajot.presentation.theme.SlateMuted
import java.util.Calendar
import java.util.UUID

@Composable
fun TaskEditDialog(
    initialTask: TaskItem? = null,
    onSave: (TaskItem) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var notes by remember { mutableStateOf(initialTask?.notes ?: "") }
    var priority by remember { mutableStateOf(initialTask?.priority ?: PriorityLevel.MEDIUM) }
    var category by remember { mutableStateOf(initialTask?.category ?: "Work") }
    var dueDateMillis by remember { mutableStateOf(initialTask?.dueDateMillis) }
    var checklist by remember { mutableStateOf(initialTask?.checklist ?: emptyList()) }
    var newSubtaskText by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    val categories = listOf("Work", "Personal", "Studio", "Urgent", "Ideas")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp))
                .background(BentoGlassBg)
                .border(1.dp, BentoGlassBorder.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialTask == null) "New To-Do Task" else "Edit Task",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialWhite
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                Text("TITLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.05f))
                        .border(
                            1.dp,
                            if (hasError) CrimsonAlert else BentoGlassBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    if (title.isEmpty()) {
                        Text("e.g. Finalize studio specs", color = SlateMuted, fontSize = 14.sp)
                    }
                    BasicTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (it.isNotBlank()) hasError = false
                        },
                        textStyle = TextStyle(color = EditorialWhite, fontSize = 14.sp),
                        cursorBrush = SolidColor(AuraAmber),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes / Description Input
                Text("NOTES (OPTIONAL)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.05f))
                        .border(1.dp, BentoGlassBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    if (notes.isEmpty()) {
                        Text("Add context or checklist info...", color = SlateMuted, fontSize = 13.sp)
                    }
                    BasicTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        textStyle = TextStyle(color = SlateLight, fontSize = 13.sp),
                        cursorBrush = SolidColor(AuraAmber),
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Priority Selector
                Text("PRIORITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriorityLevel.entries.forEach { p ->
                        val isSel = priority == p
                        val pColor = when (p) {
                            PriorityLevel.HIGH -> CrimsonAlert
                            PriorityLevel.MEDIUM -> AuraAmber
                            PriorityLevel.LOW -> CelestialAzure
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) pColor.copy(alpha = 0.2f) else androidx.compose.ui.graphics.Color.White.copy(alpha = 0.04f))
                                .border(1.dp, if (isSel) pColor else BentoGlassBorder, RoundedCornerShape(12.dp))
                                .clickable { priority = p }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) pColor else SlateMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Due Date Presets
                Text("DUE DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(
                        "None" to null,
                        "Today" to getDueDate(0),
                        "Tomorrow" to getDueDate(1),
                        "In 3 Days" to getDueDate(3),
                        "Next Week" to getDueDate(7)
                    )
                    presets.forEach { (label, millis) ->
                        val isSel = (dueDateMillis == null && millis == null) ||
                                (dueDateMillis != null && millis != null && isSameDay(dueDateMillis!!, millis))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) AuraAmberGlow else androidx.compose.ui.graphics.Color.White.copy(alpha = 0.04f))
                                .border(1.dp, if (isSel) AuraAmber else BentoGlassBorder, RoundedCornerShape(12.dp))
                                .clickable { dueDateMillis = millis }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) AuraAmber else SlateMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subtasks / Checklist Items
                Text("SUBTASKS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                Spacer(modifier = Modifier.height(6.dp))
                checklist.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AuraAmber)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.text,
                            fontSize = 13.sp,
                            color = SlateLight,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = SlateMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    checklist = checklist.filter { it.id != item.id }
                                }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.04f))
                            .border(1.dp, BentoGlassBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        if (newSubtaskText.isEmpty()) {
                            Text("Add a subtask step...", color = SlateMuted, fontSize = 12.sp)
                        }
                        BasicTextField(
                            value = newSubtaskText,
                            onValueChange = { newSubtaskText = it },
                            textStyle = TextStyle(color = EditorialWhite, fontSize = 12.sp),
                            cursorBrush = SolidColor(AuraAmber),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AuraAmberGlow)
                            .border(1.dp, AuraAmber, CircleShape)
                            .clickable {
                                if (newSubtaskText.isNotBlank()) {
                                    checklist = checklist + ChecklistItem(
                                        id = UUID.randomUUID().toString(),
                                        text = newSubtaskText.trim()
                                    )
                                    newSubtaskText = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add subtask",
                            tint = AuraAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoGlassBorder)
                    ) {
                        Text("Cancel", fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                hasError = true
                            } else {
                                val task = (initialTask ?: TaskItem(title = "")).copy(
                                    title = title.trim(),
                                    notes = notes.trim(),
                                    priority = priority,
                                    category = category,
                                    dueDateMillis = dueDateMillis,
                                    checklist = checklist,
                                    updatedAt = System.currentTimeMillis()
                                )
                                onSave(task)
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuraAmber,
                            contentColor = ObsidianBlack
                        )
                    ) {
                        Text("Save Task", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

private fun getDueDate(daysFromNow: Int): Long {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, daysFromNow)
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    return cal.timeInMillis
}

private fun isSameDay(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}
