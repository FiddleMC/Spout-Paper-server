package spout.common.moredatadriven.minecraft;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import spout.common.moredatadriven.minecraft.blocktype.SpoutBlockType;
import spout.common.moredatadriven.minecraft.itemtype.SpoutItemType;

/**
 * A class similar to {@link Registries},
 * that holds the keys for some registries in {@link BuiltInSpoutMoreDataDrivenRegistries}.
 */
public final class SpoutMoreDataDrivenRegistries {

    private SpoutMoreDataDrivenRegistries() {
        throw new UnsupportedOperationException();
    }

    /**
     * Key for {@link BuiltInSpoutMoreDataDrivenRegistries#BLOCK_TYPE}.
     */
    public static final ResourceKey<Registry<SpoutBlockType>> BLOCK_TYPE = createRegistryKey(spout.common.branding.SpoutNamespace.SPOUT + ":block_type");

    /**
     * Key for {@link BuiltInSpoutMoreDataDrivenRegistries#ITEM_TYPE}.
     */
    public static final ResourceKey<Registry<SpoutItemType>> ITEM_TYPE = createRegistryKey(spout.common.branding.SpoutNamespace.SPOUT + ":item_type");

    /**
     * Analogous to {@link Registries#createRegistryKey}.
     */
    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(Identifier.parse(name));
    }

}
