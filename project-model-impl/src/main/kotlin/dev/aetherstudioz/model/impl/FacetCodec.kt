package dev.aetherstudioz.model.impl

/**
 * Compatibility aliases. [dev.aetherstudioz.model.FacetCodecRegistry] and [dev.aetherstudioz.model.FacetData] moved into
 * `project-model-api` so an externally-packaged plugin can reach them: `project-model-impl` is not a
 * published artifact, so a registry that lived here was host-only by construction.
 */
@Deprecated(
    "Moved to project-model-api",
    ReplaceWith("FacetCodecRegistry", "dev.aetherstudioz.model.FacetCodecRegistry"),
)
typealias FacetCodecRegistry = dev.aetherstudioz.model.FacetCodecRegistry

@Deprecated("Moved to project-model-api", ReplaceWith("FacetData", "dev.aetherstudioz.model.FacetData"))
typealias FacetData = dev.aetherstudioz.model.FacetData
