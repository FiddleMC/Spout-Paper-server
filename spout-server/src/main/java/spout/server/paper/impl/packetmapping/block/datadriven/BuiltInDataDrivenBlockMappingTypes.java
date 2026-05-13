package spout.server.paper.impl.packetmapping.block.datadriven;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.packetmapping.block.automatic.FromBlockStateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.FromBlockTypeRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.ProxyStatesRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.ToBlockStateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.ToBlockTypeRequestBuilder;
import spout.common.branding.SpoutNamespace;
import spout.server.paper.impl.clientview.ClientViewImpl;
import spout.server.paper.impl.moredatadriven.minecraft.BlockRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;
import spout.server.paper.impl.packetmapping.block.automatic.AutomaticBlockMappingsImpl;
import spout.server.paper.impl.packetmapping.block.automatic.FromToBlockTypeRequestBuilderImpl;
import spout.server.paper.impl.packetmapping.block.automatic.LeavesRequestBuilderImpl;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Some built-in {@link DataDrivenBlockMappingType}s.
 */
public final class BuiltInDataDrivenBlockMappingTypes {

    private BuiltInDataDrivenBlockMappingTypes() {
        throw new UnsupportedOperationException();
    }

    public static abstract class BuiltInDataDrivenBlockMappingType implements DataDrivenBlockMappingType {

        private BuiltInDataDrivenBlockMappingType(String key) {
            DataDrivenBlockMappingTypeRegistry.register(Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, key), this);
        }

    }

    private static class SimpleBuiltInDataDrivenBlockMappingType<B extends ProxyStatesRequestBuilder> extends BuiltInDataDrivenBlockMappingType {

        private final BiConsumer<AutomaticBlockMappingsImpl, Consumer<? extends B>> mappingFunction;
        private final BuilderConsumer<B> builderConsumer;

        SimpleBuiltInDataDrivenBlockMappingType(String key, BiConsumer<AutomaticBlockMappingsImpl, Consumer<? extends B>> mappingFunction, BuilderConsumer<B> builderConsumer) {
            super(key);
            this.mappingFunction = mappingFunction;
            this.builderConsumer = builderConsumer;
        }

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
            this.mappingFunction.accept(event.automaticMappings(), builder -> this.builderConsumer.accept(builder, block, ops, mapLike));
        }

        public interface BuilderConsumer<B extends ProxyStatesRequestBuilder> {

            <T> void accept(B builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike);

        }

    }

    private static class MultiStateBuiltInDataDrivenBlockMappingType<B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> extends SimpleBuiltInDataDrivenBlockMappingType<B> {

        MultiStateBuiltInDataDrivenBlockMappingType(String key, BiConsumer<AutomaticBlockMappingsImpl, Consumer<? extends B>> mappingFunction, @Nullable BuilderConsumer<B> additionalBuilderConsumer) {
            super(key, mappingFunction, new BuilderConsumer<>() {

                @Override
                public <T> void accept(B builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
                    parseFromBlockType(builder, block, ops, mapLike, null);
                    parseToBlockType(builder, block, ops, mapLike, null);
                    if (additionalBuilderConsumer != null) {
                        additionalBuilderConsumer.accept(builder, block, ops, mapLike);
                    }
                }

            });
        }

        MultiStateBuiltInDataDrivenBlockMappingType(String key, BiConsumer<AutomaticBlockMappingsImpl, Consumer<? extends B>> mappingFunction) {
            this(key, mappingFunction, null);
        }

    }

    public static <T> Collection<? extends BlockData> parseFromBlockStates(@Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
        T fromInput = mapLike.get("from");
        if (fromInput != null) {
            List<String> fromStrings;
            DataResult<String> fromSingleString = ops.getStringValue(fromInput);
            if (fromSingleString.isSuccess()) {
                fromStrings = List.of(fromSingleString.getOrThrow());
            } else {
                fromStrings = Codec.list(Codec.STRING).decode(ops, fromInput).getOrThrow().getFirst();
            }
            return fromStrings.stream().map(Bukkit::createBlockData).toList();
        } else if (block != null) {
            return CraftBlockType.minecraftToBukkitNew(block).createBlockDataStates();
        } else {
            throw new IllegalArgumentException("Missing 'from' in included in data-driven block mapping");
        }
    }

    public static <T> @Nullable BlockData[] parseFromBlockState(FromBlockStateRequestBuilder builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike, @Nullable BlockData @Nullable [] cached) {
        @Nullable BlockData parsed;
        if (cached != null) {
            parsed = cached[0];
        } else {
            T input = mapLike.get("from");
            if (input != null) {
                parsed = Bukkit.createBlockData(ops.getStringValue(input).getOrThrow());
            } else if (block != null) {
                parsed = block.defaultBlockState().asBlockData();
            } else {
                parsed = null;
            }
            cached = new @Nullable BlockData[]{parsed};
        }
        if (parsed != null) {
            builder.from(parsed);
        }
        return cached;
    }

    public static <T> @Nullable BlockData[] parseToBlockState(ToBlockStateRequestBuilder builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike, @Nullable BlockData @Nullable [] cached) {
        @Nullable BlockData parsed;
        if (cached != null) {
            parsed = cached[0];
        } else {
            T input = mapLike.get("fallback");
            if (input != null) {
                parsed = Bukkit.createBlockData(ops.getStringValue(input).getOrThrow());
            } else {
                parsed = null;
            }
            cached = new @Nullable BlockData[]{parsed};
        }
        if (parsed != null) {
            builder.fallback(parsed);
        }
        return cached;
    }

    public static <T> @Nullable BlockType[] parseFromBlockType(FromBlockTypeRequestBuilder builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike, @Nullable BlockType @Nullable [] cached) {
        @Nullable BlockType parsed;
        if (cached != null) {
            parsed = cached[0];
        } else {
            T input = mapLike.get("from");
            if (input != null) {
                parsed = CraftBlockType.minecraftToBukkitNew(BlockRegistry.get().getValue(Identifier.CODEC.parse(ops, input).getOrThrow()));
            } else {
                parsed = CraftBlockType.minecraftToBukkitNew(block);
            }
            cached = new @Nullable BlockType[]{parsed};
        }
        if (parsed != null) {
            builder.from(parsed);
        }
        return cached;
    }

    public static <T> @Nullable BlockType[] parseToBlockType(ToBlockTypeRequestBuilder builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike, @Nullable BlockType @Nullable [] cached) {
        @Nullable BlockType parsed;
        if (cached != null) {
            parsed = cached[0];
        } else {
            T fallbackinput = mapLike.get("fallback");
            if (fallbackinput != null) {
                parsed = CraftBlockType.minecraftToBukkitNew(BlockRegistry.get().getValue(Identifier.CODEC.parse(ops, fallbackinput).getOrThrow()));
            } else {
                parsed = null;
            }
            cached = new @Nullable BlockType[]{parsed};
        }
        if (parsed != null) {
            builder.fallback(parsed);
        }
        return cached;
    }

    public static final DataDrivenBlockMappingType MANUAL = new BuiltInDataDrivenBlockMappingType("manual") {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
            Collection<? extends BlockData> fromStates = parseFromBlockStates(block, ops, mapLike);
            @Nullable List<ClientView.AwarenessLevel> awarenessLevels;
            T awarenessLevelsInput = mapLike.get("awareness_levels");
            if (awarenessLevelsInput != null) {
                awarenessLevels = ClientViewImpl.AWARENESS_LEVEL_LIST_CODEC.decode(ops, awarenessLevelsInput).getOrThrow().getFirst();
            } else {
                awarenessLevels = null;
            }
            @Nullable BlockState to;
            T toInput = mapLike.get("to");
            if (toInput != null) {
                to = ((CraftBlockData) Bukkit.createBlockData(ops.getStringValue(toInput).getOrThrow())).getState();
            } else {
                to = null;
            }
            for (BlockData fromState : fromStates) {
                event.manualMappings().registerNMS(builder -> {
                    if (awarenessLevels != null) {
                        builder.awarenessLevel(awarenessLevels);
                    }
                    builder.from(((CraftBlockData) fromState).getState());
                    if (to != null) {
                        builder.to(to);
                    }
                });
            }
        }

    };

    public static final DataDrivenBlockMappingType BARREL = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("barrel", AutomaticBlockMappingsImpl::barrel);

    public static final DataDrivenBlockMappingType BRUSHABLE = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("brushable", AutomaticBlockMappingsImpl::brushable);

    public static final DataDrivenBlockMappingType BUTTON = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("button", AutomaticBlockMappingsImpl::button);

    public static final DataDrivenBlockMappingType CHAIN = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("chain", AutomaticBlockMappingsImpl::chain);

    public static final DataDrivenBlockMappingType CHISELED_BOOKSHELF = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("chiseled_bookshelf", AutomaticBlockMappingsImpl::chiseledBookshelf);

    public static final DataDrivenBlockMappingType DOOR = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("door", AutomaticBlockMappingsImpl::door);

    public static final DataDrivenBlockMappingType FENCE = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("fence", AutomaticBlockMappingsImpl::fence);

    public static final DataDrivenBlockMappingType FENCE_GATE = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("fence_gate", AutomaticBlockMappingsImpl::fenceGate);

    public static final DataDrivenBlockMappingType FLOWER_POT = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("flower_pot", AutomaticBlockMappingsImpl::flowerPot);

    public static final DataDrivenBlockMappingType FULL_BLOCK = new BuiltInDataDrivenBlockMappingType("full_block") {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
            Collection<? extends BlockData> fromStates = parseFromBlockStates(block, ops, mapLike);
            @Nullable BlockData @Nullable [][] cachedTo = {null};
            for (BlockData fromState : fromStates) {
                event.automaticMappings().fullBlock(builder -> {
                    builder.from(fromState);
                    cachedTo[0] = parseToBlockState(builder, block, ops, mapLike, cachedTo[0]);
                });
            }
        }

    };

    public static final DataDrivenBlockMappingType FURNACE = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("furnace", AutomaticBlockMappingsImpl::furnace);

    public static final DataDrivenBlockMappingType GLAZED_TERRACOTTA = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("glazed_terracotta", AutomaticBlockMappingsImpl::glazedTerracotta);

    public static final DataDrivenBlockMappingType LADDER = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("ladder", AutomaticBlockMappingsImpl::ladder);

    public static final DataDrivenBlockMappingType LEAVES = new MultiStateBuiltInDataDrivenBlockMappingType<LeavesRequestBuilderImpl>("leaves", AutomaticBlockMappingsImpl::leaves, new SimpleBuiltInDataDrivenBlockMappingType.BuilderConsumer<>() {

        @Override
        public <T> void accept(LeavesRequestBuilderImpl builder, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
            T tintedInput = mapLike.get("tinted");
            if (tintedInput != null) {
                builder.tinted(ops.getBooleanValue(tintedInput).getOrThrow());
            }
        }

    });

    public static final DataDrivenBlockMappingType LOOM = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("loom", AutomaticBlockMappingsImpl::loom);

    public static final DataDrivenBlockMappingType NETHER_PORTAL = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("nether_portal", AutomaticBlockMappingsImpl::netherPortal);

    public static final DataDrivenBlockMappingType PRESSURE_PLATE = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("pressure_plate", AutomaticBlockMappingsImpl::pressurePlate);

    public static final DataDrivenBlockMappingType PUMPKIN = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("pumpkin", AutomaticBlockMappingsImpl::pumpkin);

    public static final DataDrivenBlockMappingType REDSTONE_ORE = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("redstone_ore", AutomaticBlockMappingsImpl::redstoneOre);

    public static final DataDrivenBlockMappingType RESPAWN_ANCHOR = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("respawn_anchor", AutomaticBlockMappingsImpl::respawnAnchor);

    public static final DataDrivenBlockMappingType ROTATED_PILLAR = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("rotated_pillar", AutomaticBlockMappingsImpl::rotatedPillar);

    public static final DataDrivenBlockMappingType SAPLING = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("sapling", AutomaticBlockMappingsImpl::sapling);

    public static final DataDrivenBlockMappingType SLAB = new BuiltInDataDrivenBlockMappingType("slab") {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {
            event.automaticMappings().slab(builder -> {
                parseFromBlockType(builder, block, ops, mapLike, null);
                parseToBlockType(builder, block, ops, mapLike, null);
                T fullBlockFallbackinput = mapLike.get("full_block_fallback");
                if (fullBlockFallbackinput != null) {
                    builder.fullBlockFallback(Bukkit.createBlockData(ops.getStringValue(fullBlockFallbackinput).getOrThrow()));
                }
            });
        }

    };

    public static final DataDrivenBlockMappingType STAIRS = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("stairs", AutomaticBlockMappingsImpl::stairs);

    public static final DataDrivenBlockMappingType TRAPDOOR = new MultiStateBuiltInDataDrivenBlockMappingType<FromToBlockTypeRequestBuilderImpl>("trapdoor", AutomaticBlockMappingsImpl::trapdoor);

    private static boolean bootstrapped = false;

    static void bootstrapIfNecessary() {
        if (!bootstrapped) {
            List.of(TRAPDOOR);
            bootstrapped = true;
        }
    }

}
