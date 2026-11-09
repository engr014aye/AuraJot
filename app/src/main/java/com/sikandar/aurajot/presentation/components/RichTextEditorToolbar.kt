package com.sikandar.aurajot.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sikandar.aurajot.presentation.theme.AuraAmber
import com.sikandar.aurajot.presentation.theme.BentoGlassBg
import com.sikandar.aurajot.presentation.theme.BentoGlassBorder
import com.sikandar.aurajot.presentation.theme.EditorialWhite
import com.sikandar.aurajot.presentation.theme.NoteTints
import com.sikandar.aurajot.presentation.theme.SlateLight
import com.sikandar.aurajot.presentation.theme.SlateMedium

enum class RichFormatAction(
    val icon: ImageVector,
    val label: String,
    val prefix: String,
    val suffix: String = "",
    val wrapSelection: Boolean = false
) {
    H1(Icons.Default.Title, "Heading 1", "# ", ""),
    H2(Icons.Default.FormatSize, "Heading 2", "## ", ""),
    BOLD(Icons.Default.FormatBold, "Bold", "**", "**", true),
    ITALIC(Icons.Default.FormatItalic, "Italic", "*", "*", true),
    STRIKE(Icons.Default.FormatStrikethrough, "Strikethrough", "~~", "~~", true),
    BULLET(Icons.Default.FormatListBulleted, "Bullet", "• ", ""),
    NUMBERED(Icons.Default.FormatListNumbered, "Numbered", "1. ", ""),
    CHECKBOX(Icons.Default.CheckCircleOutline, "Checkbox", "[ ] ", ""),
    QUOTE(Icons.Default.FormatQuote, "Quote", "> ", ""),
    CODE(Icons.Default.Code, "Code", "```\n", "\n```", true)
}

@Composable
fun RichTextEditorToolbar(
    onApplyFormat: (RichFormatAction) -> Unit,
    selectedColorHex: String,
    onColorSelected: (String) -> Unit,
    wordCount: Int,
    charCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(BentoGlassBg.copy(alpha = 0.98f))
            .border(
                1.dp,
                BentoGlassBorder.copy(alpha = 0.3f),
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        // Upper row: Word/Char stats & Palette selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$wordCount words",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateLight
                )
                Text(
                    text = "  •  ",
                    fontSize = 12.sp,
                    color = SlateMedium
                )
                Text(
                    text = "$charCount chars",
                    fontSize = 12.sp,
                    color = SlateMedium
                )
            }

            // Note surface tint selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NoteTints.forEach { (hex, _) ->
                    val color = Color(android.graphics.Color.parseColor(hex))
                    val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AuraAmber else BentoGlassBorder,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(hex) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Lower row: Scrollable formatting buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RichFormatAction.entries.forEach { action ->
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(1.dp, BentoGlassBorder, RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onApplyFormat(action) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.label,
                        tint = EditorialWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
