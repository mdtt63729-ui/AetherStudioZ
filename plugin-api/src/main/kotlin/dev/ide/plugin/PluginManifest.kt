package dev.ide.plugin

/**
 * Plugin manifest containing SPI version information.
 * This constant is used by the build system to determine the published SPI version.
 */
object PluginManifest {
    /**
     * The current plugin SPI version.
     * This should be updated when making breaking changes to the plugin API.
     */
    const val PLUGIN_SPI_VERSION: String = "1.0.0"

    /**
     * The minimum compatible plugin SPI version.
     */
    const val MIN_PLUGIN_SPI_VERSION: String = "1.0.0"

    /**
     * The plugin API name.
     */
    const val PLUGIN_API_NAME: String = "AetherStudioZ Plugin API"

    /**
     * The plugin vendor information.
     */
    const val PLUGIN_VENDOR: String = "aetherstudioz"
}
