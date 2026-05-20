package spout.server.paper.impl.moredatadriven.datapack.delayedfrozenregistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.ListCodec;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryValidator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BlockTypes;
import spout.common.moredatadriven.minecraft.common.dependent.SortDependentDataDrivenResources;
import spout.common.moredatadriven.minecraft.item.SpoutDataDrivenItem;
import spout.server.paper.impl.moredatadriven.datapack.CopyResourcesFromDataPackRegistryToInternalRegistry;
import spout.server.paper.impl.moredatadriven.datapack.SpoutDataPackRegistries;
import spout.server.paper.impl.packetmapping.item.datadriven.UnappliedDataDrivenItemMapping;
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
                    SortDependentDataDrivenResources.sortedRegistry(registry).map(pair -> {
                        pair.right().initializeItemFromInput(false);
                        Item item = pair.right().getItem();
                        Object mappingsInput = pair.right().getInput().input().get("mappings");
                        if (mappingsInput != null) {
                            DataResult<com.mojang.datafixers.util.Pair<List<UnappliedDataDrivenItemMapping>, ?>> mappings = UnappliedDataDrivenItemMapping.LIST_CODEC.decode((DynamicOps) pair.right().getInput().ops(), mappingsInput);
                            item.unappliedDataPackMappings = mappings.getOrThrow().getFirst();
                        }
                        return Pair.of(pair.left().identifier(), item);
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
