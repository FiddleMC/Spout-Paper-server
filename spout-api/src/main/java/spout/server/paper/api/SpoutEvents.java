package spout.server.paper.api;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.PrioritizedLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemType;
import spout.server.paper.api.bukkit.enuminjection.BukkitEnumNamesComposeEvent;
import spout.server.paper.api.bukkit.enuminjection.material.MaterialEnumNames;
import spout.server.paper.api.moredatadriven.paper.registry.BlockTypeRegistryEntry;
import spout.server.paper.api.moredatadriven.paper.registry.ItemTypeRegistryEntry;
import spout.server.paper.api.moredatadriven.paper.registry.type.BlockTypeTypeRegistryEntry;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeTypeRegistryEntry;
import spout.server.paper.api.packetmapping.block.BlockMappings;
import spout.server.paper.api.packetmapping.block.BlockMappingsComposeEvent;
import spout.server.paper.api.packetmapping.component.ComponentMappings;
import spout.server.paper.api.packetmapping.component.ComponentMappingsComposeEvent;
import spout.server.paper.api.packetmapping.component.translatable.ServerSideTranslations;
import spout.server.paper.api.packetmapping.component.translatable.ServerSideTranslationsComposeEvent;
import spout.server.paper.api.packetmapping.item.ItemMappings;
import spout.server.paper.api.packetmapping.item.ItemMappingsComposeEvent;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstructEvent;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstructFinishEvent;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstruction;
import spout.server.paper.api.resourcepack.plugin.discover.PluginResourcePackDiscoverEvent;
import spout.server.paper.api.resourcepack.plugin.discover.PluginResourcePackDiscovery;
import spout.server.paper.api.util.composable.ComposableEventType;
import org.jspecify.annotations.Nullable;

/**
 * A convenience class providing links to the different Spout {@link LifecycleEventType}s.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class SpoutEvents {

    private SpoutEvents() {
        throw new UnsupportedOperationException();
    }

    public static final LifecycleEventType.Prioritizable<BootstrapContext, RegistryComposeEvent<?, BlockTypeTypeRegistryEntry.Builder>> BLOCK_TYPE = (LifecycleEventType.Prioritizable) RegistryEvents.BLOCK_TYPE_TYPE.compose();
    public static final LifecycleEventType.Prioritizable<BootstrapContext, RegistryComposeEvent<?, ItemTypeTypeRegistryEntry.Builder>> ITEM_TYPE = (LifecycleEventType.Prioritizable) RegistryEvents.ITEM_TYPE_TYPE.compose();
    public static final LifecycleEventType.Prioritizable<BootstrapContext, RegistryComposeEvent<BlockType, BlockTypeRegistryEntry.Builder>> BLOCK = RegistryEvents.BLOCK_TYPE.compose();
    public static final LifecycleEventType.Prioritizable<BootstrapContext, RegistryComposeEvent<ItemType, ItemTypeRegistryEntry.Builder>> ITEM = RegistryEvents.ITEM_TYPE.compose();
    public static final ComposableEventType<PluginResourcePackDiscoverEvent> PLUGIN_RESOURCE_PACK_DISCOVERY = PluginResourcePackDiscovery.get().compose();
    public static final ComposableEventType<ResourcePackConstructEvent> RESOURCE_PACK_CONSTRUCT = ResourcePackConstruction.get().compose();
    public static final LifecycleEventType<BootstrapContext, ResourcePackConstructFinishEvent, PrioritizedLifecycleEventHandlerConfiguration<BootstrapContext>> RESOURCE_PACK_CONSTRUCT_FINISH = ResourcePackConstruction.get().finish();
    public static final ComposableEventType<BlockMappingsComposeEvent> BLOCK_MAPPING = ((BlockMappings) BlockMappings.get()).compose();
    public static final ComposableEventType<ItemMappingsComposeEvent<?>> ITEM_MAPPING = ((ItemMappings) ItemMappings.get()).compose();
    public static final ComposableEventType<ComponentMappingsComposeEvent<?>> COMPONENT_MAPPING = ((ComponentMappings) ComponentMappings.get()).compose();
    public static final ComposableEventType<ServerSideTranslationsComposeEvent> SERVER_SIDE_TRANSLATION = ServerSideTranslations.get().compose();
    public static final ComposableEventType<BukkitEnumNamesComposeEvent<Triple<NamespacedKey, @Nullable BlockType, @Nullable ItemType>>> MATERIAL_ENUM_NAME = MaterialEnumNames.get().compose();

}
