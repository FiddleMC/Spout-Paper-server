package spout.common.moredatadriven.minecraft;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import spout.common.moredatadriven.minecraft.blocktype.SpoutBlockTypeRegistry;
import spout.common.moredatadriven.minecraft.blocktype.SpoutBlockTypes;
import spout.common.moredatadriven.minecraft.itemtype.SpoutItemType;
import spout.common.moredatadriven.minecraft.itemtype.SpoutItemTypes;
import java.util.function.Function;

/**
 * A class similar to {@link BuiltInRegistries},
 * that holds the built-in registries that Spout uses to add more data-driven elements.
 */
public final class BuiltInSpoutMoreDataDrivenRegistries {

    private BuiltInSpoutMoreDataDrivenRegistries() {
        throw new UnsupportedOperationException();
    }

    /**
     * A registry for block types.
     *
     * <p>
     * This registry is synchronized with {@link BuiltInRegistries#BLOCK_TYPE}:
     * entries added to either are added to the other.
     * </p>
     */
    public static final SpoutBlockTypeRegistry BLOCK_TYPE = internalRegister(SpoutMoreDataDrivenRegistries.BLOCK_TYPE, new SpoutBlockTypeRegistry(), SpoutBlockTypes::bootstrap);

    /**
     * A registry for item types.
     */
    public static final Registry<SpoutItemType> ITEM_TYPE = registerSimple(SpoutMoreDataDrivenRegistries.ITEM_TYPE, SpoutItemTypes::bootstrap);

    /**
     * Analogous to {@link BuiltInRegistries#registerSimple}.
     */
    public static <T> Registry<T> registerSimple(
        ResourceKey<? extends Registry<T>> name, Function<Registry<T>, Object> loader
    ) {
        return internalRegister(name, new MappedRegistry<>(name, Lifecycle.stable(), false), loader);
    }
    /**
     * Analogous to {@link BuiltInRegistries#internalRegister}.
     */
    public static <T, R extends WritableRegistry<T>> R internalRegister(
        ResourceKey<? extends Registry<T>> name, R registry, Function<Registry<T>, Object> loader
    ) {
        Bootstrap.checkBootstrapCalled(() -> "registry " + name.identifier());
        io.papermc.paper.registry.PaperRegistryAccess.instance().registerRegistry(registry); // Paper - initialize API registry
        Identifier key = name.identifier();
        BuiltInRegistries.LOADERS.put(key, () -> loader.apply(registry));
        BuiltInRegistries.WRITABLE_REGISTRY.register((ResourceKey)name, registry, RegistrationInfo.BUILT_IN);
        return registry;
    }

    public static Registry<?> bootstrap() {
        return ITEM_TYPE;
    }

}
