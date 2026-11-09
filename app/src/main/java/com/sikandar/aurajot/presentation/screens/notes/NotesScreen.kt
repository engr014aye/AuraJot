package com.sikandar.aurajot.presentation.screens.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sikandar.aurajot.domain.model.Note
import com.sikandar.aurajot.presentation.components.BentoCard
import com.sikandar.aurajot.presentation.components.EmptyStateView
import com.sikandar.aurajot.presentation.components.GlassTopBar
import com.sikandar.aurajot.presentation.theme.AuraAmber
import com.sikandar.aurajot.presentation.theme.AuraAmberGlow
import com.sikandar.aurajot.presentation.theme.BentoGlassBg
import com.sikandar.aurajot.presentation.theme.BentoGlassBorder
import com.sikandar.aurajot.presentation.theme.BentoGlassCard
import com.sikandar.aurajot.presentation.theme.CrimsonAlert
import com.sikandar.aurajot.presentation.theme.EditorialWhite
import com.sikandar.aurajot.presentation.theme.ObsidianBlack
import com.sikandar.aurajot.presentation.theme.SlateLight
import com.sikandar.aurajot.presentation.theme.SlateMedium
import com.sikandar.aurajot.presentation.theme.SlateMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onNoteClick: (Long) -> Unit,
    onCreateNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val pinnedNotes = remember(uiState.notes) { uiState.notes.filter { it.isPinned } }
    val otherNotes = remember(uiState.notes) { uiState.notes.filter { !it.isPinned } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(
                title = "AuraJot",
                subtitle = "Studio Clean Notes"
            )

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BentoGlassBg)
                    .border(1.dp, BentoGlassBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = SlateMedium,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.searchQuery.isEmpty()) {
                            Text(
                                text = "Search notes & thoughts...",
                                color = SlateMuted,
                                fontSize = 14.sp
                            )
                        }
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = EditorialWhite,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(AuraAmber),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (uiState.searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = SlateMedium,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { viewModel.onSearchQueryChanged("") }
                        )
                    }
                }
            }

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                viewModel.categories.forEach { cat ->
                    val isSelected = uiState.selectedCategory.equals(cat, ignoreCase = true)
                    val chipBg = if (isSelected) AuraAmberGlow else BentoGlassBg
                    val chipBorder = if (isSelected) AuraAmber else BentoGlassBorder
                    val chipText = if (isSelected) AuraAmber else SlateMedium

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(chipBg)
                            .border(1.dp, chipBorder, RoundedCornerShape(20.dp))
                            .clickable { viewModel.onCategorySelected(cat) }
                            .padding(horizontal = 16.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = chipText
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            // Notes List
            if (uiState.notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        icon = Icons.Default.NoteAlt,
                        title = if (uiState.searchQuery.isNotEmpty()) "No matching notes" else "No notes yet",
                        description = if (uiState.searchQuery.isNotEmpty())
                            "Try searching with different keywords."
                        else "Tap the amber '+' button below to jot down ideas, journal entries, or studio thoughts."
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = AuraAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PINNED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraAmber,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                        items(pinnedNotes, key = { it.id }) { note ->
                            NoteCardItem(
                                note = note,
                                onClick = { onNoteClick(note.id) },
                                onTogglePin = { viewModel.togglePin(note) },
                                onDelete = { viewModel.softDelete(note) }
                            )
                        }
                    }

                    if (otherNotes.isNotEmpty()) {
                        if (pinnedNotes.isNotEmpty()) {
                            item {
                                Text(
                                    text = "ALL NOTES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateMuted,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                )
                            }
                        }
                        items(otherNotes, key = { it.id }) { note ->
                            NoteCardItem(
                                note = note,
                                onClick = { onNoteClick(note.id) },
                                onTogglePin = { viewModel.togglePin(note) },
                                onDelete = { viewModel.softDelete(note) }
                            )
                        }
                    }
                }
            }
        }

        // FAB to add note
        FloatingActionButton(
            onClick = onCreateNote,
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
                contentDescription = "New Note",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun NoteCardItem(
    note: Note,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = remember(note.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(note.colorHex))
        } catch (_: Exception) {
            BentoGlassCard
        }
    }

    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()) }
    val formattedDate = remember(note.updatedAt) { dateFormatter.format(Date(note.updatedAt)) }

    BentoCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = cardColor.copy(alpha = 0.85f),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Category pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = note.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AuraAmber,
                        letterSpacing = 0.5.sp
                    )
                }

                // Actions: Pin and Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val pinInteraction = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = pinInteraction,
                                indication = null,
                                onClick = onTogglePin
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (note.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (note.isPinned) "Unpin" else "Pin",
                            tint = if (note.isPinned) AuraAmber else SlateMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    val deleteInteraction = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = deleteInteraction,
                                indication = null,
                                onClick = onDelete
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Move to Trash",
                            tint = SlateMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = note.title.ifBlank { "Untitled Note" },
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = EditorialWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note.content,
                    fontSize = 13.sp,
                    color = SlateLight,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formattedDate,
                fontSize = 11.sp,
                color = SlateMuted
            )
        }
    }
}
