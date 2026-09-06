package dev.aetherstudioz.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring

/** Motion tokens from tokens.css. Honor reduce-motion by gating entrance animations at call sites. */
object Motion {
    /** calm decel — --md-motion-standard */
    val quiet: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    /** liquid overshoot — --md-motion-emphasized */
    val spring: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val soft: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

    const val FAST = 150   // --dur-short: 150ms
    const val BASE = 300   // --dur-medium: 300ms
    const val SLOW = 450   // --dur-long: 450ms

    /** Press feedback scale. */
    const val PRESS_SCALE = 0.96f
}

/**
 * The Material 3 Expressive spring set, hand-declared.
 *
 * M3 Expressive ships these as `MaterialTheme.motionScheme`, but that property and the `MotionScheme`
 * interface itself are **internal** in the Compose Multiplatform material3 artifact this project
 * resolves (1.9.0) — the classes are in the jar, marked internal in their Kotlin metadata, so they
 * cannot be referenced. These are the published spring values, declared here so the app still animates
 * on the expressive curves. Swap the call sites over to `MaterialTheme.motionScheme` if the artifact
 * ever exposes it.
 *
 * **Spatial** springs move things — size, position, shape. They are underdamped (0.9), so a nav
 * indicator settling into place has a trace of overshoot. **Effects** springs change color and alpha and
 * are critically damped (1.0): a colour that overshoots reads as a flicker, not as liveliness.
 */
object CaMotion {
    fun <T> defaultSpatial(): SpringSpec<T> = spring(dampingRatio = 0.9f, stiffness = 700f)
    fun <T> fastSpatial(): SpringSpec<T> = spring(dampingRatio = 0.9f, stiffness = 1400f)
    fun <T> slowSpatial(): SpringSpec<T> = spring(dampingRatio = 0.9f, stiffness = 300f)

    fun <T> defaultEffects(): SpringSpec<T> = spring(dampingRatio = 1f, stiffness = 1600f)
    fun <T> fastEffects(): SpringSpec<T> = spring(dampingRatio = 1f, stiffness = 3800f)
    fun <T> slowEffects(): SpringSpec<T> = spring(dampingRatio = 1f, stiffness = 800f)
}
