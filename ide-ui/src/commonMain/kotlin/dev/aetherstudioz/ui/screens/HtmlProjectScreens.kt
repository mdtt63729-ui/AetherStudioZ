package dev.aetherstudioz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aetherstudioz.ui.icons.CaIcons

/* ============================================================
   HTML PROJECT TEMPLATE
   Shown as a new section at the TOP of CreateProjectScreen gallery
   ============================================================ */

/** HTML template entry — appears above Java/Kotlin sections. */
data class HtmlTemplateInfo(
    val id: String = "html-blank",
    val displayName: String = "HTML App",
    val description: String = "Create an HTML/CSS/JS web app wrapped as an APK",
    val category: String = "HTML",
)

val HTML_TEMPLATES = listOf(
    HtmlTemplateInfo("html-blank", "HTML App", "Blank HTML/CSS/JS project — build a web app as an APK", "HTML"),
    HtmlTemplateInfo("html-bootstrap", "Bootstrap App", "HTML app with Bootstrap framework pre-configured", "HTML"),
)

/* ============================================================
   HTML PROJECT DETAILS SCREEN
   Matches HTML file's screen-details:
   - App icon picker
   - App name, package name, version, version code
   - Screen rotation (portrait/landscape/auto)
   - Full screen, hide title bar, allow long press, loading UI,
     allow zoom, PC mode, media autoplay, pull to refresh,
     allow microphone, allow camera
   - Continue button
   ============================================================ */

@Composable
fun HtmlProjectDetailsScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    var appName by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("com.example.app") }
    var versionName by remember { mutableStateOf("1.0.0") }
    var versionCode by remember { mutableStateOf("1") }
    var rotation by remember { mutableStateOf("Auto") }
    var fullScreen by remember { mutableStateOf(false) }
    var hideTitleBar by remember { mutableStateOf(false) }
    var allowLongPress by remember { mutableStateOf(true) }
    var showLoadingUi by remember { mutableStateOf(true) }
    var allowZoom by remember { mutableStateOf(true) }
    var pcMode by remember { mutableStateOf(false) }
    var mediaAutoplay by remember { mutableStateOf(false) }
    var pullToRefresh by remember { mutableStateOf(true) }
    var allowMicrophone by remember { mutableStateOf(false) }
    var allowCamera by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState()),
    ) {
        // Top app bar
        Row(
            Modifier.fillMaxWidth().padding(14.dp, 14.dp, 12.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val backInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier.size(44.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable(backInteraction, indication = null, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(CaIcons.chevronLeft, "Back", Modifier.size(22.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Step 2 of 2", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Project Details", style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp, fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
            }
        }

        // Content card
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // App icon picker
            Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val iconInteraction = remember { MutableInteractionSource() }
                Box(
                    Modifier.size(72.dp).clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                        .clickable(iconInteraction, indication = null) { },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(CaIcons.image, "Pick icon", Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column {
                    Text("Select App Icon", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("PNG, JPG or WEBP", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // App Name
            FieldEntry("Application Name", appName, "My HTML App") { appName = it }
            // Package Name
            FieldEntry("Package Name", packageName, "com.example.app") { packageName = it }
            // Version Name
            FieldEntry("Version Name", versionName, "1.0.0") { versionName = it }
            // Version Code
            FieldEntry("Version Code", versionCode, "1") { versionCode = it }

            // Screen Rotation
            Text("Screen Rotation", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("Auto", "Portrait", "Landscape").forEach { option ->
                    val selected = rotation == option
                    val chipInteraction = remember { MutableInteractionSource() }
                    Surface(
                        shape = CircleShape,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(40.dp).clickable(chipInteraction, indication = null) { rotation = option },
                    ) {
                        Row(Modifier.padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(option, style = MaterialTheme.typography.labelMedium, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
                        }
                    }
                }
            }

            // Toggle rows
            ToggleRow("Full Screen Mode", "Hide status bar and navigation bar", fullScreen) { fullScreen = it }
            ToggleRow("Hide Title Bar", "Remove the app title bar", hideTitleBar) { hideTitleBar = it }
            ToggleRow("Allow Long Press", "Enable long-press context menus", allowLongPress) { allowLongPress = it }
            ToggleRow("Show Loading UI", "Display a loading screen on startup", showLoadingUi) { showLoadingUi = it }
            ToggleRow("Allow Zoom", "Enable pinch-to-zoom on web content", allowZoom) { allowZoom = it }
            ToggleRow("PC Mode", "Request desktop site layout", pcMode) { pcMode = it }
            ToggleRow("Media Autoplay", "Auto-play video/audio without tap", mediaAutoplay) { mediaAutoplay = it }
            ToggleRow("Pull to Refresh", "Enable pull-to-refresh gesture", pullToRefresh) { pullToRefresh = it }
            ToggleRow("Allow Microphone", "Grant microphone access to web content", allowMicrophone) { allowMicrophone = it }
            ToggleRow("Allow Camera", "Grant camera access to web content", allowCamera) { allowCamera = it }

            // Continue button
            val continueInteraction = remember { MutableInteractionSource() }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth().height(52.dp).clickable(continueInteraction, indication = null, onClick = onContinue),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Continue", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
                }
            }
        }
    }
}

@Composable
private fun FieldEntry(label: String, value: String, placeholder: String, onValueChange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 6.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp).padding(vertical = 0.dp),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline)
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

/* ============================================================
   HTML FILE MANAGER SCREEN
   Matches HTML file's screen-workspace:
   - Top app bar with back button + title
   - Toolbar: Import button, Build APK button
   - File list with icons (colored per type)
   - FAB for creating new files
   ============================================================ */

data class HtmlFileEntry(
    val name: String,
    val size: String,
    val type: FileType,
)

enum class FileType { HTML, CSS, JS, JSON, IMAGE, VIDEO, AUDIO, FONT, FOLDER, FILE }

val SAMPLE_FILES = listOf(
    HtmlFileEntry("index.html", "2.4 KB", FileType.HTML),
    HtmlFileEntry("style.css", "1.1 KB", FileType.CSS),
    HtmlFileEntry("script.js", "3.2 KB", FileType.JS),
    HtmlFileEntry("assets", "—", FileType.FOLDER),
    HtmlFileEntry("manifest.json", "0.5 KB", FileType.JSON),
)

@Composable
fun HtmlFileManagerScreen(
    projectName: String,
    onBack: () -> Unit,
    onImportFile: () -> Unit,
    onOpenFile: (HtmlFileEntry) -> Unit,
    onAddFile: () -> Unit,
    files: List<HtmlFileEntry> = SAMPLE_FILES,
) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        // Top app bar
        Row(
            Modifier.fillMaxWidth().padding(14.dp, 14.dp, 12.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val backInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier.size(44.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable(backInteraction, indication = null, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(CaIcons.chevronLeft, "Back", Modifier.size(22.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(8.dp))
            Text(projectName, style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp, fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        }

        // Toolbar
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp).padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val importInteraction = remember { MutableInteractionSource() }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.weight(1f).height(44.dp).clickable(importInteraction, indication = null, onClick = onImportFile),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(CaIcons.cloudUpload, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(6.dp))
                    Text("Import", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            val buildInteraction = remember { MutableInteractionSource() }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.weight(1f).height(44.dp).clickable(buildInteraction, indication = null) { },
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(CaIcons.hammer, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(6.dp))
                    Text("Build APK", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        // File list
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 100.dp).verticalScroll(rememberScrollState()),
        ) {
            files.forEach { file ->
                val fileInteraction = remember { MutableInteractionSource() }
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 13.dp, horizontal = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(fileInteraction, indication = null) { onOpenFile(file) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(11.dp))
                            .background(fileTypeColor(file.type)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(fileTypeIcon(file.type), null, Modifier.size(20.dp), tint = androidx.compose.ui.graphics.Color.White)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(file.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                        Text(file.size, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

private fun fileTypeColor(type: FileType) = when (type) {
    FileType.HTML -> androidx.compose.ui.graphics.Color(0xFFE44D26)
    FileType.CSS -> androidx.compose.ui.graphics.Color(0xFF2965F1)
    FileType.JS -> androidx.compose.ui.graphics.Color(0xFFF0DB4F)
    FileType.JSON -> androidx.compose.ui.graphics.Color(0xFF8BC34A)
    FileType.IMAGE -> androidx.compose.ui.graphics.Color(0xFFAB47BC)
    FileType.VIDEO -> androidx.compose.ui.graphics.Color(0xFFEF5350)
    FileType.AUDIO -> androidx.compose.ui.graphics.Color(0xFF26A69A)
    FileType.FONT -> androidx.compose.ui.graphics.Color(0xFF795548)
    FileType.FOLDER -> androidx.compose.ui.graphics.Color(0xFFFFC107)
    FileType.FILE -> androidx.compose.ui.graphics.Color(0xFF78909C)
}

private fun fileTypeIcon(type: FileType) = when (type) {
    FileType.FOLDER -> CaIcons.folder
    else -> CaIcons.file
}

/* ============================================================
   HTML CODE EDITOR WITH LINE NUMBERS
   Matches HTML file's editor-screen:
   - Top app bar with back + filename + actions (search, undo, redo, preview, more)
   - Line numbered gutter (fast calculation for large pastes)
   - Code area with syntax highlighting support
   - Status bar at bottom
   ============================================================ */

@Composable
fun HtmlCodeEditorScreen(
    fileName: String,
    initialContent: String,
    onBack: () -> Unit,
    onContentChange: (String) -> Unit,
) {
    var content by remember { mutableStateOf(initialContent) }
    val scrollState = rememberScrollState()

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainerLowest)) {
        // Top app bar (64dp, matching HTML editor-screen .top-appbar)
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(8.dp, 8.dp, 8.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val backInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(20.dp))
                    .clickable(backInteraction, indication = null, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(CaIcons.chevronLeft, "Back", Modifier.size(22.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(6.dp))
            Text(
                fileName,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                EditorActionIcon(CaIcons.search, "Search") { }
                EditorActionIcon(CaIcons.undo, "Undo") { content = initialContent }
                EditorActionIcon(CaIcons.redo, "Redo") { }
                // Preview button - highlighted with primaryContainer
                val previewInteraction = remember { MutableInteractionSource() }
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable(previewInteraction, indication = null) { },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(CaIcons.play, "Preview", Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                EditorActionIcon(CaIcons.ellipsis, "More") { }
            }
        }

        // Editor body — gutter + code area
        Row(
            Modifier.fillMaxWidth().weight(1f)
                .padding(horizontal = 8.dp, vertical = 0.dp).padding(bottom = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
        ) {
            // Line number gutter
            val lineCount = remember(content) {
                // Fast line count — just count newlines, don't split into a list
                var count = 1
                for (c in content) if (c == '\n') count++
                count
            }
            
            Column(
                Modifier.width(46.dp).background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .verticalScroll(scrollState),
            ) {
                // Render line numbers 1..lineCount
                // Using a simple approach: each line number is 20sp tall
                for (i in 1..lineCount) {
                    Text(
                        "$i",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.5.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.outline,
                        ),
                        modifier = Modifier.fillMaxWidth().padding(end = 10.dp, top = 0.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    )
                }
            }

            // Code text area
            Box(
                Modifier.weight(1f).background(MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
                BasicTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        onContentChange(it)
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.5.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxSize().padding(16.dp, 14.dp, 16.dp, 200.dp),
                )
            }
        }

        // Status bar
        Row(
            Modifier.fillMaxWidth().height(34.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val lines = remember(content) { content.count { it == '\n' } + 1 }
            val chars = content.length
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outline))
                Text("Saved", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace)
            }
            Text("$lines lines · $chars chars", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun EditorActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        Modifier.size(40.dp).clip(RoundedCornerShape(20.dp))
            .clickable(interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, label, Modifier.size(22.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
