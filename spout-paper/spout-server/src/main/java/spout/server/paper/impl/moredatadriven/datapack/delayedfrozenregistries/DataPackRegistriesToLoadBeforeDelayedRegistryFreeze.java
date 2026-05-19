package spout.server.paper.impl.moredatadriven.datapack.delayedfrozenregistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryValidator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.BlockTypes;
import spout.common.moredatadriven.minecraft.common.dependent.SortDependentDataDrivenResources;
import spout.common.moredatadriven.minecraft.item.SpoutDataDrivenItem;
import spout.server.paper.impl.moredatadriven.datapack.CopyResourcesFromDataPackRegistryToInternalRegistry;
import spout.server.paper.impl.moredatadriven.datapack.SpoutDataPackRegistries;
import java.util.List;
import java.util.function.Consumer;

/**
 * A holder, similar to {@link RegistryDataLoader#WORLDGEN_REGISTRIES},
 * for the registries that are loaded from data packs before the delayed registries are frozen.
 */
public final class DataPackRegistriesToLoadBeforeDelayedRegistryFreeze {

    private DataPackRegistriesToLoadBeforeDelayedRegistryFreeze() {
        throw new UnsupportedOperationException();
    }

    public static final List<Instance<?>> REGISTRIES = List.of(
        new Instance<>(
            SpoutDataPackRegistries.BLOCK_FROM_DATA_PACK,
            BlockTypes.CODEC,
            registry -> {
                CopyResourcesFromDataPackRegistryToInternalRegistry.copy(
                    registry,
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK
                );
            }
        ),
        new Instance<>(
            SpoutDataPackRegistries.ITEM_FROM_DATA_PACK,
            SpoutDataDrivenItem.CODEC,
            registry -> {
                CopyResourcesFromDataPackRegistryToInternalRegistry.copy(
                    SortDependentDataDrivenResources.sorted(registry).map(pair -> {
                        pair.right().initializeItemFromInput();
                        return Pair.of(pair.left().identifier(), pair.right().getItem());
                    }),
                    net.minecraft.core.registries.BuiltInRegistries.ITEM
                );
            }
        )
    );

    public record Instance<T>(
        RegistryDataLoader.RegistryData<T> registryData,
        Consumer<Registry<T>> postLoadAction
    ) {

        public Instance(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Consumer<Registry<T>> postLoadAction) {
            this(new RegistryDataLoader.RegistryData<>(registryKey, codec, RegistryValidator.none()), postLoadAction);
        }

        public Instance(ResourceKey<Registry<T>> registryKey, MapCodec<T> codec, Consumer<Registry<T>> postLoadAction) {
            this(registryKey, codec.codec(), postLoadAction);
        }

    }

}
