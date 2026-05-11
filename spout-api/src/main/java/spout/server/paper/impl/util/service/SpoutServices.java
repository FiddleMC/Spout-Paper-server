package spout.server.paper.impl.util.service;

import spout.server.paper.api.bukkit.enuminjection.material.MaterialEnumNames;
import spout.server.paper.api.packetmapping.block.BlockMappings;
import spout.server.paper.api.packetmapping.block.claim.ResourcePackBlockStateClaims;
import spout.server.paper.api.packetmapping.block.claim.VisualDuplicates;
import spout.server.paper.api.packetmapping.component.ComponentMappings;
import spout.server.paper.api.packetmapping.component.translatable.ServerSideTranslations;
import spout.server.paper.api.packetmapping.item.ItemMappingUtilities;
import spout.server.paper.api.packetmapping.item.ItemMappings;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstruction;
import spout.server.paper.api.resourcepack.plugin.discover.PluginResourcePackDiscovery;
import spout.server.paper.impl.bukkit.enuminjection.material.match.MaterialByKeyLookup;
import java.util.ServiceLoader;

/**
 * A class that provides the instances for all Spout service classes
 * (which will typically have a static {@code get()} method that defers to this class).
 */
public final class SpoutServices {

    private SpoutServices() {
        throw new UnsupportedOperationException();
    }

    private static <T> T getOrInitialize(T[] reference, Class<T> clazz) {
        T value = reference[0];
        if (value != null) {
            return value;
        }
        synchronized (reference) {
            value = reference[0];
            if (value != null) {
                return value;
            }
            value = ServiceLoader.load(clazz, clazz.getClassLoader()).findFirst().get();
            reference[0] = value;
            return value;
        }
    }

    private static final BlockMappings[] blockMappings = new BlockMappings[1];
    private static final ComponentMappings[] componentMappings = new ComponentMappings[1];
    private static final ItemMappings[] itemMappings = new ItemMappings[1];
    private static final ItemMappingUtilities[] itemMappingUtilities = new ItemMappingUtilities[1];
    private static final MaterialByKeyLookup[] materialByKeyLookup = new MaterialByKeyLookup[1];
    private static final MaterialEnumNames[] materialEnumNames = new MaterialEnumNames[1];
    private static final PluginResourcePackDiscovery[] pluginResourcePackDiscovery = new PluginResourcePackDiscovery[1];
    private static final ResourcePackBlockStateClaims[] resourcePackBlockStateClaims = new ResourcePackBlockStateClaims[1];
    private static final ResourcePackConstruction[] resourcePackConstruction = new ResourcePackConstruction[1];
    private static final ServerSideTranslations[] serverSideTranslations = new ServerSideTranslations[1];
    private static final VisualDuplicates[] visualDuplicates = new VisualDuplicates[1];

    public static BlockMappings getBlockMappings() {
        return getOrInitialize(blockMappings, BlockMappings.class);
    }

    public static ComponentMappings<?> getComponentMappings() {
        return getOrInitialize(componentMappings, ComponentMappings.class);
    }

    public static ItemMappings<?> getItemMappings() {
        return getOrInitialize(itemMappings, ItemMappings.class);
    }

    public static ItemMappingUtilities getItemMappingUtilities() {
        return getOrInitialize(itemMappingUtilities, ItemMappingUtilities.class);
    }

    public static MaterialByKeyLookup getMaterialByKeyLookup() {
        return getOrInitialize(materialByKeyLookup, MaterialByKeyLookup.class);
    }

    public static MaterialEnumNames getMaterialEnumNames() {
        return getOrInitialize(materialEnumNames, MaterialEnumNames.class);
    }

    public static PluginResourcePackDiscovery getPluginResourcePackDiscovery() {
        return getOrInitialize(pluginResourcePackDiscovery, PluginResourcePackDiscovery.class);
    }

    public static ResourcePackBlockStateClaims getResourcePackBlockStateClaims() {
        return getOrInitialize(resourcePackBlockStateClaims, ResourcePackBlockStateClaims.class);
    }

    public static ResourcePackConstruction getResourcePackConstruction() {
        return getOrInitialize(resourcePackConstruction, ResourcePackConstruction.class);
    }

    public static ServerSideTranslations getServerSideTranslations() {
        return getOrInitialize(serverSideTranslations, ServerSideTranslations.class);
    }

    public static VisualDuplicates getVisualDuplicates() {
        return getOrInitialize(visualDuplicates, VisualDuplicates.class);
    }

}
