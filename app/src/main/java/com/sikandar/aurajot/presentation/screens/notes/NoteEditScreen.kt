package com.sikandar.aurajot.presentation.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sikandar.aurajot.domain.model.Note
import com.sikandar.aurajot.presentation.components.GlassIconButton
import com.sikandar.aurajot.presentation.components.RichFormatAction
import com.sikandar.aurajot.presentation.components.RichTextEditorToolbar
import com.sikandar.aurajot.presentation.theme.AuraAmber
import com.sikandar.aurajot.presentation.theme.BentoGlassBg
import com.sikandar.aurajot.presentation.theme.BentoGlassBorder
import com.sikandar.aurajot.presentation.theme.EditorialWhite
import com.sikandar.aurajot.presentation.theme.ObsidianBlack
import com.sikandar.aurajot.presentation.theme.SlateLight
import com.sikandar.aurajot.presentation.theme.SlateMedium
import com.sikandar.aurajot.presentation.theme.SlateMuted
import kotlinx.coroutines.launch

@Composable
fun NoteEditScreen(
    noteId: Long,
    viewModel: NotesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var titleValue by remember { mutableStateOf(TextFieldValue("")) }
    var contentValue by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCategory by remember { mutableStateOf("Personal") }
    var selectedColorHex by remember { mutableStateOf("#1C202C") }
    var isPinned by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    // Load existing note or initialize new note
    LaunchedEffect(noteId) {
        if (noteId > 0) {
            val existingNote = viewModel.getNote(noteId)
            if (existingNote != null) {
                titleValue = TextFieldValue(existingNote.title)
                contentValue = TextFieldValue(existingNote.content)
                selectedCategory = existingNote.category
                selectedColorHex = existingNote.colorHex
                isPinned = existingNote.isPinned
            }
        }
        isLoaded = true
    }

    // Function to save note
    val saveCurrentNote = {
        val t = titleValue.text.trim()
        val c = contentValue.text.trim()
        if (t.isNotEmpty() || c.isNotEmpty()) {
            coroutineScope.launch {
                val noteToSave = Note(
                    id = if (noteId > 0) noteId else 0,
                    title = t,
                    content = contentValue.text,
                    category = selectedCategory,
                    colorHex = selectedColorHex,
                    isPinned = isPinned,
                    updatedAt = System.currentTimeMillis()
                )
                viewModel.saveNote(noteToSave)
            }
        }
    }

    // Apply rich formatting
    val applyFormat: (RichFormatAction) -> Unit = { action ->
        val text = contentValue.text
        val selection = contentValue.selection

        val newText: String
        val newSelection: TextRange

        if (selection.collapsed) {
            // No selection: Insert at cursor or prefix line
            val cursor = selection.start
            val before = text.substring(0, cursor)
            val after = text.substring(cursor)
            
            val insertion = "${action.prefix}${action.suffix}"
            newText = "$before$insertion$after"
            val targetPos = cursor + action.prefix.length
            newSelection = TextRange(targetPos, targetPos)
        } else {
            // Wrap selected text
            val start = selection.min
            val end = selection.max
            val selectedText = text.substring(start, end)
            val before = text.substring(0, start)
            val after = text.substring(end)

            val wrapped = "${action.prefix}$selectedText${action.suffix}"
            newText = "$before$wrapped$after"
            newSelection = TextRange(start + action.prefix.length, end + action.prefix.length)
        }

        contentValue = TextFieldValue(text = newText, selection = newSelection)
    }

    // Word and Char counts
    val wordCount = remember(contentValue.text) {
        val trimmed = contentValue.text.trim()
        if (trimmed.isEmpty()) 0 else trimmed.split("\\s+".toRegex()).size
    }
    val charCount = remember(contentValue.text) { contentValue.text.length }

    val bgColor = remember(selectedColorHex) {
        try {
            Color(android.graphics.Color.parseColor(selectedColorHex))
        } catch (_: Exception) {
            ObsidianBlack
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GlassIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = {
                        saveCurrentNote()
                        onNavigateBack()
                    }
                )

                // Category selector chip
                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BentoGlassBg)
                            .border(1.dp, BentoGlassBorder, RoundedCornerShape(12.dp))
                            .clickable { showCategoryMenu = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = selectedCategory.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuraAmber,
                            letterSpacing = 0.5.sp
                        )
                    }

                    DropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false },
                        modifier = Modifier
                            .background(BentoGlassBg)
                            .border(1.dp, BentoGlassBorder, RoundedCornerShape(12.dp))
                    ) {
                        viewModel.categories.filter { it != "All" }.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = cat,
                                        color = if (selectedCategory == cat) AuraAmber else EditorialWhite,
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedCategory = cat
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }

                // Actions: Pin, Delete, Save
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlassIconButton(
                        icon = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                        onClick = { isPinned = !isPinned },
                        tint = if (isPinned) AuraAmber else EditorialWhite
                    )

                    if (noteId > 0) {
                        GlassIconButton(
                            icon = Icons.Default.DeleteOutline,
                            onClick = {
                                coroutineScope.launch {
                                    val note = viewModel.getNote(noteId)
                                    if (note != null) {
                                        viewModel.softDelete(note)
                                    }
                                    onNavigateBack()
                                }
                            }
                        )
                    }

                    GlassIconButton(
                        icon = Icons.Default.Check,
                        tint = AuraAmber,
                        onClick = {
                            saveCurrentNote()
                            onNavigateBack()
                        }
                    )
                }
            }

            // Editable Note Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                // Title Field
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (titleValue.text.isEmpty()) {
                        Text(
                            text = "Title",
                            color = SlateMuted,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    BasicTextField(
                        value = titleValue,
                        onValueChange = { titleValue = it },
                        textStyle = TextStyle(
                            color = EditorialWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        ),
                        cursorBrush = SolidColor(AuraAmber),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content Body Field
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    if (contentValue.text.isEmpty()) {
                        Text(
                            text = "Start jotting down your thoughts, markdown, checklists...",
                            color = SlateMuted,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                    BasicTextField(
                        value = contentValue,
                        onValueChange = { contentValue = it },
                        textStyle = TextStyle(
                            color = SlateLight,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        cursorBrush = SolidColor(AuraAmber),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Rich Text Toolbar
            RichTextEditorToolbar(
                onApplyFormat = applyFormat,
                selectedColorHex = selectedColorHex,
                onColorSelected = { selectedColorHex = it },
                wordCount = wordCount,
                charCount = charCount
            )
        }
    }
}
