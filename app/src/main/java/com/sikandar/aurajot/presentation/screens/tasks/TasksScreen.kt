package com.sikandar.aurajot.presentation.screens.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sikandar.aurajot.domain.model.TaskItem
import com.sikandar.aurajot.presentation.components.BentoCard
import com.sikandar.aurajot.presentation.components.EmptyStateView
import com.sikandar.aurajot.presentation.components.GlassTopBar
import com.sikandar.aurajot.presentation.components.PriorityBadge
import com.sikandar.aurajot.presentation.theme.AuraAmber
import com.sikandar.aurajot.presentation.theme.AuraAmberGlow
import com.sikandar.aurajot.presentation.theme.BentoGlassBg
import com.sikandar.aurajot.presentation.theme.BentoGlassBorder
import com.sikandar.aurajot.presentation.theme.BentoGlassCard
import com.sikandar.aurajot.presentation.theme.CupertinoSpring
import com.sikandar.aurajot.presentation.theme.EditorialWhite
import com.sikandar.aurajot.presentation.theme.EmeraldSuccess
import com.sikandar.aurajot.presentation.theme.ObsidianBlack
import com.sikandar.aurajot.presentation.theme.SlateLight
import com.sikandar.aurajot.presentation.theme.SlateMedium
import com.sikandar.aurajot.presentation.theme.SlateMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var editingTask by remember { mutableStateOf<TaskItem?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(
                title = "To-Dos",
                subtitle = "Studio Productivity"
            )

            // Bento Progress Summary Card
            BentoProgressSummaryCard(
                completed = uiState.completedCount,
                total = uiState.totalCount,
                percentage = uiState.completionPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            )

            // Filter Tabs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                TaskFilter.entries.forEach { f ->
                    val isSelected = uiState.filter == f
                    val bg = if (isSelected) AuraAmberGlow else BentoGlassBg
                    val border = if (isSelected) AuraAmber else BentoGlassBorder
                    val txt = if (isSelected) AuraAmber else SlateMedium

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(18.dp))
                            .clickable { viewModel.onFilterSelected(f) }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = f.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = txt
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            // Task List or Empty State
            if (uiState.tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        icon = Icons.Default.CheckCircle,
                        title = if (uiState.filter == TaskFilter.COMPLETED) "No completed tasks yet" else "All caught up!",
                        description = if (uiState.filter == TaskFilter.COMPLETED)
                            "Tasks marked as completed will appear here."
                        else "No pending tasks in this category. Tap '+' to create your next milestone."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 6.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onToggle = { viewModel.toggleTask(task) },
                            onToggleSubtask = { subId -> viewModel.toggleSubtask(task, subId) },
                            onEdit = { editingTask = task },
                            onDelete = { viewModel.softDelete(task) }
                        )
                    }
                }
            }
        }

        // FAB to add task
        FloatingActionButton(
            onClick = { isCreatingNew = true },
            containerColor = AuraAmber,
            contentColor = ObsidianBlack,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 84.dp)
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Task",
                modifier = Modifier.size(28.dp)
            )
        }

        // Dialog for creating or editing tasks
        if (isCreatingNew || editingTask != null) {
            TaskEditDialog(
                initialTask = editingTask,
                onSave = { task ->
                    viewModel.saveTask(task)
                    isCreatingNew = false
                    editingTask = null
                },
                onDismiss = {
                    isCreatingNew = false
                    editingTask = null
                }
            )
        }
    }
}

@Composable
fun BentoProgressSummaryCard(
    completed: Int,
    total: Int,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage,
        animationSpec = CupertinoSpring.responsive(),
        label = "progress_anim"
    )

    val progressPercent = (percentage * 100).toInt()

    BentoCard(
        modifier = modifier,
        backgroundColor = BentoGlassCard
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "FOCUS & MOMENTUM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraAmber,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (total == 0) "No tasks yet" else "$completed of $total tasks completed",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EditorialWhite
                    )
                }

                Text(
                    text = "$progressPercent%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (progressPercent == 100 && total > 0) EmeraldSuccess else AuraAmber
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (progressPercent == 100 && total > 0) EmeraldSuccess else AuraAmber,
                trackColor = Color.White.copy(alpha = 0.08f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskItem,
    onToggle: () -> Unit,
    onToggleSubtask: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSubtasks by remember { mutableStateOf(false) }

    val completedSubtasks = remember(task.checklist) { task.checklist.count { it.isDone } }
    val totalSubtasks = remember(task.checklist) { task.checklist.size }

    val dueText = remember(task.dueDateMillis) {
        task.dueDateMillis?.let {
            val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
            formatter.format(Date(it))
        }
    }

    BentoCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = if (task.isCompleted) BentoGlassCard.copy(alpha = 0.5f) else BentoGlassCard
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Cupertino Bouncy Checkbox
                CupertinoCheckbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.padding(top = 2.dp, end = 12.dp)
                )

                // Task Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (task.isCompleted) SlateMuted else EditorialWhite,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )

                    if (task.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.notes,
                            fontSize = 13.sp,
                            color = SlateMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Badges row: Priority, Due Date, Subtask Count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PriorityBadge(priority = task.priority)

                        if (dueText != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = SlateMedium,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = dueText,
                                    fontSize = 10.sp,
                                    color = SlateMedium
                                )
                            }
                        }

                        if (totalSubtasks > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .clickable { expandedSubtasks = !expandedSubtasks }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "$completedSubtasks/$totalSubtasks steps",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AuraAmber
                                )
                                Icon(
                                    imageVector = if (expandedSubtasks) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = AuraAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // Edit and Delete Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val editSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = editSource,
                                indication = null,
                                onClick = onEdit
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = SlateMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    val delSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = delSource,
                                indication = null,
                                onClick = onDelete
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Move to Trash",
                            tint = SlateMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Expanded Subtasks checklist
            if (expandedSubtasks && totalSubtasks > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 34.dp)
                ) {
                    task.checklist.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleSubtask(item.id) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CupertinoCheckbox(
                                checked = item.isDone,
                                onCheckedChange = { onToggleSubtask(item.id) },
                                size = 18.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.text,
                                fontSize = 13.sp,
                                color = if (item.isDone) SlateMuted else SlateLight,
                                textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CupertinoCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 24.dp
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.08f else 1.0f,
        animationSpec = CupertinoSpring.bouncy(),
        label = "cb_scale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (checked) EmeraldSuccess else Color.Transparent,
        animationSpec = CupertinoSpring.responsive(),
        label = "cb_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (checked) EmeraldSuccess else SlateMedium.copy(alpha = 0.5f),
        animationSpec = CupertinoSpring.responsive(),
        label = "cb_border"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.5.dp, borderColor, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = ObsidianBlack,
                modifier = Modifier.size(size * 0.65f)
            )
        }
    }
}
