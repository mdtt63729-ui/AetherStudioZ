package dev.aetherstudioz.agent.impl

import dev.aetherstudioz.agent.LlmProvider
import dev.aetherstudioz.agent.LlmProviderRegistry
import dev.aetherstudioz.agent.SimpleLlmProviderRegistry

/** Assembles the built-in providers over a transport (OkHttp by default). Plugins may add more providers. */
object AgentProviders {
    fun defaults(transport: LlmTransport = OkHttpLlmTransport()): List<LlmProvider> =
        listOf(
            AnthropicProvider(transport),
            OpenAiProvider(transport),
            GeminiProvider(transport),
            OpenRouterProvider(transport),
        )

    fun registry(transport: LlmTransport = OkHttpLlmTransport()): LlmProviderRegistry =
        SimpleLlmProviderRegistry(defaults(transport))
}
