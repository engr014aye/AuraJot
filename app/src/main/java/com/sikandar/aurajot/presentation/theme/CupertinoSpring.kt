package com.sikandar.aurajot.presentation.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

object CupertinoSpring {
    /**
     * Standard iOS/Cupertino fluid responsive spring.
     * Slightly damped, fast settlement, zero harsh jarring.
     */
    fun <T> responsive(): AnimationSpec<T> = spring(
        dampingRatio = 0.82f,
        stiffness = 380f
    )

    /**
     * Playful bouncy spring for checkbox toggle, fab click, and card insertion.
     */
    fun <T> bouncy(): AnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    /**
     * Snappy spring for quick tab transitions, sheet slides, and chip filtering.
     */
    fun <T> snappy(): AnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /**
     * Smooth subtle spring for background scrims and alpha fades.
     */
    fun <T> gentle(): AnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
}
