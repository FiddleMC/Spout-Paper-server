package spout.server.paper.impl.moredatadriven.datapack;

import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows registries populated from data packs to have a directory name
 * that is different from their resource key.
 */
public final class DifferentDataPackRegistryDirectoryNames {

    private DifferentDataPackRegistryDirectoryNames() {
        throw new UnsupportedOperationException();
    }

    private static final Map<Identifier, String> differentNames = new HashMap<>();

    public static void register(ResourceKey<? extends Registry<?>> key, String name) {
        differentNames.put(key.identifier(), name);
    }

    public static @Nullable String get(ResourceKey<? extends Registry<?>> key) {
        return differentNames.get(key.identifier());
    }

    public static FileToIdConverter getFileToIdConverter(WritableRegistry<?> registry) {
        ResourceKey<? extends Registry<?>> key = registry.key();
        @Nullable String differentName = get(key);
        return differentName != null ? FileToIdConverter.json(differentName) : FileToIdConverter.registry(key);
    }



}
