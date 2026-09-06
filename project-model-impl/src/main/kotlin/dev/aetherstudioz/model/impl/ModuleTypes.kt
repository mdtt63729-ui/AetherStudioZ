package dev.aetherstudioz.model.impl

/** Compatibility aliases; both moved to `project-model-api` (see [dev.aetherstudioz.model.ModuleTypeRegistry]). */
@Deprecated(
    "Moved to project-model-api",
    ReplaceWith("ModuleTypeRegistry", "dev.aetherstudioz.model.ModuleTypeRegistry"),
)
typealias ModuleTypeRegistry = dev.aetherstudioz.model.ModuleTypeRegistry

@Deprecated("Moved to project-model-api", ReplaceWith("UnknownModuleType", "dev.aetherstudioz.model.UnknownModuleType"))
typealias UnknownModuleType = dev.aetherstudioz.model.UnknownModuleType
