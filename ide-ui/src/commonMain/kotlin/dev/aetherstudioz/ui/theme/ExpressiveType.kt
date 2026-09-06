package dev.aetherstudioz.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The Material 3 Expressive type scale, bound to the app's UI face.
 *
 * Two departures from stock M3 do most of the work of making the UI not read as a default Material app:
 *
 * - **Negative tracking on the large sizes.** Display and headline roles tighten to between -0.4 and
 *   -1 sp. At 34 sp, default tracking looks slack; pulling it in is what makes a screen title read as a
 *   set piece rather than as large body text.
 * - **A light display weight.** `displaySmall` is [FontWeight.Light], not bold. Screen titles ("Your
 *   projects", "Explore", "Learn") get their presence from size and tracking, and the weight contrast
 *   against the medium-weight titles below them is the point.
 *
 * `labelSmall` is deliberately **bold with wide tracking**: it is the all-caps eyebrow label ("ABOUT",
 * "SORT BY", "CONTINUE"), which needs the extra letter spacing to stay legible in caps at 12 sp.
 *
 * Fed to [androidx.compose.material3.MaterialTheme], so every native M3 component picks this up. Sizes
 * are in sp and therefore scale with the user's font scale; nothing in the redesign fixes a text
 * container's height, so a 2.0 scale wraps rather than clips.
 */
fun expressiveTypography(ui: FontFamily): Typography = Typography(
    // Matching AetherStudioZ HTML design system typography
    displayLarge = TextStyle(fontFamily = ui, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = (-0.2).sp),
    displayMedium = TextStyle(fontFamily = ui, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.2).sp),
    displaySmall = TextStyle(fontFamily = ui, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp, letterSpacing = (-0.2).sp),
    // headline-lg: 32px/700, headline-md: 26px/700
    headlineLarge = TextStyle(fontFamily = ui, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = (-0.2).sp),
    headlineMedium = TextStyle(fontFamily = ui, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp, letterSpacing = (-0.2).sp),
    headlineSmall = TextStyle(fontFamily = ui, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.2).sp),
    // title-lg: 20px/600, title-md: 16px/500
    titleLarge = TextStyle(fontFamily = ui, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = (-0.1).sp),
    titleMedium = TextStyle(fontFamily = ui, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = ui, fontWeight = FontWeight.Medium, fontSize = 14.5.sp, lineHeight = 20.sp),
    // body-lg: 15px/400, body-md: 13.5px/400
    bodyLarge = TextStyle(fontFamily = ui, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = ui, fontWeight = FontWeight.Normal, fontSize = 13.5.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = ui, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp),
    // label-lg: 14px/600
    labelLarge = TextStyle(fontFamily = ui, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontFamily = ui, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.1.sp),
    // Section title: 13px/700, uppercase, 0.5px tracking
    labelSmall = TextStyle(fontFamily = ui, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
)
