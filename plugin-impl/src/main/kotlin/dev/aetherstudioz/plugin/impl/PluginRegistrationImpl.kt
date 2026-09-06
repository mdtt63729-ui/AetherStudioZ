package dev.aetherstudioz.plugin.impl

import dev.aetherstudioz.platform.Disposable
import dev.aetherstudioz.platform.ExtensionPoint
import dev.aetherstudioz.platform.ExtensionRegistry
import dev.aetherstudioz.platform.MessageBus
import dev.aetherstudioz.platform.MessageBusConnection
import dev.aetherstudioz.platform.PluginId
import dev.aetherstudioz.platform.SERVICE_EP
import dev.aetherstudioz.platform.ServiceDescriptor
import dev.aetherstudioz.platform.ServiceFactory
import dev.aetherstudioz.platform.ServiceKey
import dev.aetherstudioz.platform.ServiceLookup
import dev.aetherstudioz.platform.ServiceScopeLevel
import dev.aetherstudioz.platform.impl.CompositeDisposable
import dev.aetherstudioz.platform.log.Log
import dev.aetherstudioz.platform.log.Logger
import dev.aetherstudioz.plugin.PluginRegistration

/**
 * The [PluginRegistration] a [PluginManager] hands to one plugin's `register`. It attributes every
 * contribution to [pluginId] and adds each returned [Disposable] to [teardown] (the plugin's
 * [CompositeDisposable]), so an unload disposes them LIFO. Facade contributions made through [contributeVia]
 * discard their handles by design; those are swept on unload by [ExtensionRegistry.unregisterAll].
 */
internal class PluginRegistrationImpl(
    override val pluginId: PluginId,
    private val registry: ExtensionRegistry,
    private val teardown: CompositeDisposable,
    private val bus: MessageBus,
    override val hostVersion: String? = null,
    override val appServices: ServiceLookup = ServiceLookup.Empty,
) : PluginRegistration {

    override val messageBus: MessageBus get() = bus

    override fun <T : Any> register(ep: ExtensionPoint<T>, impl: T): Disposable =
        teardown.add(registry.register(ep, impl, pluginId))

    override fun <T : Any> service(
        key: ServiceKey<T>,
        level: ServiceScopeLevel,
        factory: ServiceFactory<T>,
    ): Disposable =
        teardown.add(registry.register(SERVICE_EP, ServiceDescriptor(key, level, factory, pluginId), pluginId))

    override fun contributeVia(block: (ExtensionRegistry, PluginId) -> Unit) = block(registry, pluginId)

    override fun onDispose(d: Disposable) {
        teardown.add(d)
    }

    // A MessageBusConnection is a Disposable, so tracking it in teardown auto-unsubscribes on unload.
    override fun busConnection(): MessageBusConnection = bus.connect().also { teardown.add(it) }

    override fun logger(tag: String): Logger = Log.logger(tag, source = pluginId.value)
}
