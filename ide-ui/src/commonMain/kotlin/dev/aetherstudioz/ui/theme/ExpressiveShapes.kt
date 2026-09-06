package dev.aetherstudioz.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * The Material 3 Expressive shape scale — rounder and more generous than baseline M3 (whose steps are
 * 4/8/12/16/28 dp). Fed to [androidx.compose.material3.MaterialTheme]; components pick their tier
 * (Buttons → full/large, Cards → medium/large, Sheets/Dialogs → extraLarge) automatically.
 *
 * `small` is deliberately *tighter* than the tier above it rather than following a smooth ramp: the
 * design pairs a large radius against a small one on the same element (see [cardShape]), and that
 * contrast needs the small end to stay small.
 */
val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // --md-sys-shape-xs: 4px
    small = RoundedCornerShape(8.dp),        // --md-sys-shape-sm: 8px
    medium = RoundedCornerShape(12.dp),      // --md-sys-shape-md: 12px
    large = RoundedCornerShape(16.dp),       // --md-sys-shape-lg: 16px
    extraLarge = RoundedCornerShape(28.dp),  // --md-sys-shape-xl: 28px
)
