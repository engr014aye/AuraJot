package com.sikandar.aurajot.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sikandar.aurajot.presentation.theme.AuraAmber
import com.sikandar.aurajot.presentation.theme.AuraAmberGlow
import com.sikandar.aurajot.presentation.theme.BentoGlassBg
import com.sikandar.aurajot.presentation.theme.BentoGlassBorder
import com.sikandar.aurajot.presentation.theme.CupertinoSpring
import com.sikandar.aurajot.presentation.theme.SlateMedium

enum class BottomNavTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    NOTES("notes", "Notes", Icons.Default.EditNote),
    TASKS("tasks", "To-Dos", Icons.Default.CheckCircle),
    TRASH("trash", "Trash Bin", Icons.Default.Delete)
}

@Composable
fun GlassBottomNav(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(BentoGlassBg.copy(alpha = 0.96f))
                .border(1.dp, BentoGlassBorder.copy(alpha = 0.35f), RoundedCornerShape(32.dp))
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                val interactionSource = remember { MutableInteractionSource() }

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1.0f,
                    animationSpec = CupertinoSpring.responsive(),
                    label = "tab_scale"
                )

                val tintColor by animateColorAsState(
                    targetValue = if (isSelected) AuraAmber else SlateMedium,
                    animationSpec = CupertinoSpring.snappy(),
                    label = "tab_tint"
                )

                val itemBgColor by animateColorAsState(
                    targetValue = if (isSelected) AuraAmberGlow else androidx.compose.ui.graphics.Color.Transparent,
                    animationSpec = CupertinoSpring.responsive(),
                    label = "tab_bg"
                )

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clip(RoundedCornerShape(20.dp))
                        .background(itemBgColor)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(tab) }
                        )
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = tintColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = tab.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = tintColor
                        )
                    }
                }
            }
        }
    }
}
