package com.sikandar.aurajot.presentation.screens.trash

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sikandar.aurajot.domain.model.TrashItem
import com.sikandar.aurajot.presentation.components.BentoCard
import com.sikandar.aurajot.presentation.components.ConfirmDialog
import com.sikandar.aurajot.presentation.components.EmptyStateView
import com.sikandar.aurajot.presentation.components.GlassIconButton
import com.sikandar.aurajot.presentation.components.GlassTopBar
import com.sikandar.aurajot.presentation.theme.AuraAmber
import com.sikandar.aurajot.presentation.theme.AuraAmberGlow
import com.sikandar.aurajot.presentation.theme.BentoGlassBg
import com.sikandar.aurajot.presentation.theme.BentoGlassBorder
import com.sikandar.aurajot.presentation.theme.BentoGlassCard
import com.sikandar.aurajot.presentation.theme.CelestialAzure
import com.sikandar.aurajot.presentation.theme.CrimsonAlert
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
fun TrashScreen(
    viewModel: TrashViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showEmptyTrashConfirm by remember { mutableStateOf(false) }
    var itemToDeletePermanently by remember { mutableStateOf<TrashItem?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(
                title = "Trash Bin",
                subtitle = "Recovery & Permanent Purge",
                actions = {
                    if (uiState.totalCount > 0) {
                        GlassIconButton(
                            icon = Icons.Default.DeleteForever,
                            tint = CrimsonAlert,
                            onClick = { showEmptyTrashConfirm = true },
                            contentDescription = "Empty Trash"
                        )
                    }
                }
            )

            // Info Bento Banner
            BentoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                backgroundColor = BentoGlassCard.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CelestialAzure,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Items in the Trash can be restored anytime or permanently purged.",
                        fontSize = 12.sp,
                        color = SlateLight,
                        lineHeight = 17.sp
                    )
                }
            }

            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                TrashFilter.entries.forEach { f ->
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

            // Trash Items List or Empty State
            if (uiState.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        icon = Icons.Default.DeleteOutline,
                        title = "Trash is Empty",
                        description = "Items deleted from Notes or To-Dos will appear here for 1-tap recovery."
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
                    items(uiState.items, key = { "${it.itemType}_${it.id}" }) { item ->
                        TrashItemCard(
                            item = item,
                            onRestore = { viewModel.restoreItem(item) },
                            onDeletePermanently = { itemToDeletePermanently = item }
                        )
                    }
                }
            }
        }

        // Empty All Trash Confirmation Dialog
        if (showEmptyTrashConfirm) {
            ConfirmDialog(
                title = "Empty Trash?",
                message = "Are you sure you want to permanently delete all ${uiState.totalCount} item(s)? This action cannot be undone.",
                confirmText = "Empty All",
                isDestructive = true,
                onConfirm = {
                    viewModel.emptyTrash()
                    showEmptyTrashConfirm = false
                },
                onDismiss = { showEmptyTrashConfirm = false }
            )
        }

        // Single Item Permanent Delete Dialog
        if (itemToDeletePermanently != null) {
            val it = itemToDeletePermanently!!
            ConfirmDialog(
                title = "Permanently Delete?",
                message = "Are you sure you want to permanently delete '${it.title}'? This cannot be recovered.",
                confirmText = "Delete Forever",
                isDestructive = true,
                onConfirm = {
                    viewModel.permanentlyDeleteItem(it)
                    itemToDeletePermanently = null
                },
                onDismiss = { itemToDeletePermanently = null }
            )
        }
    }
}

@Composable
fun TrashItemCard(
    item: TrashItem,
    onRestore: () -> Unit,
    onDeletePermanently: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()) }
    val deletedDateText = remember(item.deletedAt) {
        "Deleted " + dateFormatter.format(Date(item.deletedAt))
    }

    val typeBadgeColor = when (item) {
        is TrashItem.NoteTrash -> AuraAmber
        is TrashItem.TaskTrash -> CelestialAzure
    }

    BentoCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = BentoGlassCard
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Type badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(typeBadgeColor.copy(alpha = 0.15f))
                        .border(0.8.dp, typeBadgeColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.itemType.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeBadgeColor,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = deletedDateText,
                    fontSize = 11.sp,
                    color = SlateMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = EditorialWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (item is TrashItem.NoteTrash && item.note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.note.content,
                    fontSize = 13.sp,
                    color = SlateMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            } else if (item is TrashItem.TaskTrash && item.task.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.task.notes,
                    fontSize = 13.sp,
                    color = SlateMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons: Restore & Delete Permanently
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDeletePermanently,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CrimsonAlert
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onRestore,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldSuccess.copy(alpha = 0.2f),
                        contentColor = EmeraldSuccess
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restore", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
