package com.sikandar.aurajot.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sikandar.aurajot.domain.model.PriorityLevel
import com.sikandar.aurajot.presentation.theme.AuraAmber
import com.sikandar.aurajot.presentation.theme.CelestialAzure
import com.sikandar.aurajot.presentation.theme.CrimsonAlert

@Composable
fun PriorityBadge(
    priority: PriorityLevel,
    modifier: Modifier = Modifier
) {
    val (dotColor, bgColor, borderColor) = when (priority) {
        PriorityLevel.HIGH -> Triple(CrimsonAlert, CrimsonAlert.copy(alpha = 0.15f), CrimsonAlert.copy(alpha = 0.4f))
        PriorityLevel.MEDIUM -> Triple(AuraAmber, AuraAmber.copy(alpha = 0.15f), AuraAmber.copy(alpha = 0.4f))
        PriorityLevel.LOW -> Triple(CelestialAzure, CelestialAzure.copy(alpha = 0.15f), CelestialAzure.copy(alpha = 0.4f))
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(0.8.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = priority.label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            color = dotColor
        )
    }
}
