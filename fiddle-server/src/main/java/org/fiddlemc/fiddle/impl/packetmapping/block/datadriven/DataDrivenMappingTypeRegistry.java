package org.fiddlemc.fiddle.impl.packetmapping.block.datadriven;

import net.minecraft.resources.Identifier;
import org.fiddlemc.fiddle.impl.branding.FiddleNamespace;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple ad-hoc registry of existing types of mapping types.
 */
public final class DataDrivenMappingTypeRegistry {

    private DataDrivenMappingTypeRegistry() {
        throw new UnsupportedOperationException();
    }

    private static final Map<Identifier, DataDrivenMappingType> registry = new HashMap<>();

    private static void register(String key, DataDrivenMappingType type) {
        if (key.indexOf(Identifier.NAMESPACE_SEPARATOR) == -1) {
            key = FiddleNamespace.FIDDLE + Identifier.NAMESPACE_SEPARATOR + key;
        }
        register(Identifier.parse(key), type);
    }

    public static void register(Identifier key, DataDrivenMappingType type) {
        if (registry.containsKey(key)) {
            throw new IllegalArgumentException("Data-driven mapping type " + key + " is already registered!");
        }
        registry.put(key, type);
    }

    public static @Nullable DataDrivenMappingType get(String key) {
        if (key.indexOf(Identifier.NAMESPACE_SEPARATOR) == -1) {
            key = FiddleNamespace.FIDDLE + Identifier.NAMESPACE_SEPARATOR + key;
        }
        return get(Identifier.parse(key));
    }

    public static @Nullable DataDrivenMappingType get(Identifier key) {
        return registry.get(key);
    }

    static {
        // Manual
        register("manual", BuiltInDataDrivenMappingTypes.MANUAL);
        // Automatic
        register("full_block", BuiltInDataDrivenMappingTypes.FULL_BLOCK);
        register("leaves", BuiltInDataDrivenMappingTypes.LEAVES);
        register("slab", BuiltInDataDrivenMappingTypes.SLAB);
        register("stairs", BuiltInDataDrivenMappingTypes.STAIRS);
    }

}
