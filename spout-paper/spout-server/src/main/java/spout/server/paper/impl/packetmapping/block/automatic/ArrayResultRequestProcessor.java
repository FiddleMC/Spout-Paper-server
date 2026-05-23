package spout.server.paper.impl.packetmapping.block.automatic;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.jspecify.annotations.Nullable;
import spout.common.util.minecraft.blockstate.BlockStateUtil;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.api.resourcepack.construct.BlockstatesResourcePackPath;
import spout.server.paper.api.resourcepack.content.Blockstates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import spout.server.paper.impl.resourcepack.construct.ResourcePackConstructionImpl;
import spout.server.paper.impl.resourcepack.plugin.discover.PluginResourcePackDiscoveryImpl;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * An abstract {@link RequestProcessor}
 * that builds its (intermediate) result in a {@link Result}.
 */
public abstract class ArrayResultRequestProcessor<R extends ProxyStatesRequestBuilderImpl, Re extends ArrayResultRequestProcessor.Result> extends RequestProcessor<R> {

    protected final Re result;

    protected ArrayResultRequestProcessor(R request, BlockMappingsComposeEventImpl event) {
        super(request, event);
        this.result = this.createResult();
    }

    protected Re createResult() {
        return (Re) new RequestBasedResult((FromToBlockStatesRequestBuilder) this.request);
    }

    protected void useResult() {
        applyMappingsForResult(
            this.event,
            this.result,
            this.request.createFromToUsedStatesMappings,
            this.request.createVanillaMappings,
            this.request.createResourcePackBlockstatesEntries,
            this.request.createItemMappings,
            this.request.getFromItemFunction(),
            this.request.getToItemFunction()
        );
        if (this.request.resultConsumers != null) {
            UsedStates usedStates = result.getUsedStates();
            this.request.resultConsumers.forEach(consumer -> consumer.accept(usedStates));
        }
    }

    /**
     * A utility class that can be used to store the (intermediate) result of processing a typical request.
     */
    public static abstract class Result {

        /**
         * The block states that will be mapped to for {@link ClientView.AwarenessLevel#RESOURCE_PACK} clients,
         * at the same index as the corresponding state in {@link #fromStates()}.
         *
         * <p>
         * The array may be null if not initialized yet,
         * and individual values may be null if not determined yet.
         * </p>
         */
        protected @Nullable BlockState @Nullable [] resourcePackToStates;

        /**
         * Whether the value in {@link #resourcePackToStates} at each index is a proxy
         * (i.e. it is a claimed block that needs to be visually overridden).
         */
        protected boolean @Nullable [] isResourcePackToStateProxy;

        /**
         * The block states that will be mapped to for {@link ClientView.AwarenessLevel#VANILLA} clients.
         *
         * <p>
         * The array may be null if not initialized yet,
         * and individual values may be null if not determined yet.
         * </p>
         */
        protected @Nullable BlockState @Nullable [] vanillaToStates;

        /**
         * A map of each value in {@link #fromStates()} to their index in that array,
         * or null if not computed yet.
         */
        protected @Nullable Object2IntMap<BlockState> fromStateIndices;

        /**
         * @return The block states that have to be mapped.
         */
        protected abstract BlockState[] fromStates();

        public int getFromIndex(BlockState fromState) {
            BlockState[] fromStates = this.fromStates();
            for (int i = 0; i < fromStates.length; i++) {
                if (fromStates[i] == fromState) {
                    return i;
                }
            }
            throw new IllegalArgumentException();
        }

        public @Nullable BlockState getResourcePackToState(int fromIndex) {
            return this.resourcePackToStates == null ? null : this.resourcePackToStates[fromIndex];
        }

        public @Nullable BlockState getResourcePackToState(BlockState fromState) {
            return this.getResourcePackToState(this.getFromIndex(fromState));
        }

        public @Nullable BlockState getVanillaToState(int fromIndex) {
            return this.vanillaToStates == null ? null : this.vanillaToStates[fromIndex];
        }

        public @Nullable BlockState getVanillaToState(BlockState fromState) {
            return this.getVanillaToState(this.getFromIndex(fromState));
        }

        protected void initializeArrays() {
            BlockState[] fromStates = this.fromStates();
            if (this.resourcePackToStates == null) {
                this.resourcePackToStates = new BlockState[fromStates.length];
            }
            if (this.isResourcePackToStateProxy == null) {
                this.isResourcePackToStateProxy = new boolean[fromStates.length];
            }
            if (this.vanillaToStates == null) {
                this.vanillaToStates = new BlockState[fromStates.length];
            }
        }

        public void setResourcePackToState(int index, BlockState toState, boolean isProxy) {
            this.initializeArrays();
            this.resourcePackToStates[index] = toState;
            this.isResourcePackToStateProxy[index] = isProxy;
        }

        public void setResourcePackToStateIfNotSet(int index, BlockState toState, boolean isProxy) {
            this.initializeArrays();
            if (this.resourcePackToStates[index] == null) {
                this.resourcePackToStates[index] = toState;
                this.isResourcePackToStateProxy[index] = isProxy;
            }
        }

        /**
         * Sets the given block state as the to state for all yet unmapped from states.
         *
         * @param fallbackState A fallback block state.
         */
        public void setAllUnmapped(BlockState fallbackState, boolean includeResourcePack) {
            this.initializeArrays();
            BlockState[] fromStates = this.fromStates();
            for (int i = 0; i < fromStates.length; i++) {
                if (includeResourcePack) {
                    if (this.resourcePackToStates[i] == null) {
                        this.resourcePackToStates[i] = fallbackState;
                    }
                }
                if (this.vanillaToStates[i] == null) {
                    this.vanillaToStates[i] = fallbackState;
                }
            }
        }

        /**
         * Sets the given block states as the to state for all yet unmapped from states.
         *
         * @param fallbackStates Array of fallback block states, with the same length as {@link #fromStates()},
         *                       and indexed the same as {@link #fromStates()}
         */
        public void setAllUnmapped(BlockState[] fallbackStates, boolean includeResourcePack) {
            this.initializeArrays();
            for (int i = 0; i < fallbackStates.length; i++) {
                if (includeResourcePack) {
                    if (this.resourcePackToStates[i] == null) {
                        this.resourcePackToStates[i] = fallbackStates[i];
                    }
                }
                if (this.vanillaToStates[i] == null) {
                    this.vanillaToStates[i] = fallbackStates[i];
                }
            }
        }

        /**
         * Sets the to state for all yet unmapped from states to the
         * {@linkplain BlockStateUtil#copyProperties(BlockState, BlockState) corresponding}
         * state of the given block.
         *
         * @param fallbackBlock A fallback block.
         */
        public void setAllUnmapped(Block fallbackBlock, boolean includeResourcePack) {
            // Skip if all mapped already
            if (this.resourcePackToStates != null && this.vanillaToStates != null) {
                boolean allMapped = true;
                for (int i = 0; i < this.resourcePackToStates.length; i++) {
                    if ((includeResourcePack && this.resourcePackToStates[i] == null) || this.vanillaToStates[i] == null) {
                        allMapped = false;
                        break;
                    }
                }
                if (allMapped) {
                    return;
                }
            }
            this.setAllUnmapped(BlockStateUtil.copyProperties(this.fromStates(), fallbackBlock), includeResourcePack);
        }

        public int getFromStateIndex(BlockState fromState) {
            if (this.fromStateIndices == null) {
                this.fromStateIndices = new Object2IntOpenHashMap<>();
                BlockState[] fromStates = this.fromStates();
                for (int i = 0; i < fromStates.length; i++) {
                    this.fromStateIndices.put(fromStates[i], i);
                }
            }
            return this.fromStateIndices.getInt(fromState);
        }

        /**
         * @return A {@link UsedStates} instance for this result.
         */
        public UsedStates getUsedStates() {
            return from -> {
                int index = Result.this.getFromStateIndex(((CraftBlockData) from).getState());
                return ObjectBooleanPair.of(this.resourcePackToStates[index].asBlockData(), this.isResourcePackToStateProxy[index]);
            };
        }

    }

    /**
     * A {@link Result} for {@link FromToBlockStatesRequestBuilder} requests.
     */
    public static class RequestBasedResult extends Result {

        /**
         * The request.
         */
        protected final FromToBlockStatesRequestBuilder request;

        public RequestBasedResult(FromToBlockStatesRequestBuilder request) {
            this.request = request;
        }

        @Override
        protected BlockState[] fromStates() {
            return this.request.fromStates();
        }

    }

    public void applyMappingsForResult() {
        applyMappingsForResult(
            this.event,
            this.result,
            this.request.createFromToUsedStatesMappings,
            this.request.createVanillaMappings,
            this.request.createResourcePackBlockstatesEntries,
            this.request.createItemMappings,
            this.request.getFromItemFunction(),
            this.request.getToItemFunction()
        );
    }

    public static void applyMappingsForResult(
        BlockMappingsComposeEventImpl event,
        Result result,
        boolean createResourcePackMappings,
        boolean createVanillaMappings,
        boolean createResourcePackBlockstatesEntries,
        boolean createItemMappings,
        @Nullable Function<BlockState, @Nullable Item> fromItemFunction,
        @Nullable Function<BlockState, @Nullable Item> toItemFunction
    ) {
        Set<BlockState> createdResourcePackEntryForProxyStates = createResourcePackBlockstatesEntries ? new HashSet<>(result.fromStates().length) : null; // Don't create resource pack entries for the same block state multiple times
        BlockState[] fromStates = result.fromStates();
        for (int fromStateIndex = 0; fromStateIndex < fromStates.length; fromStateIndex++) {
            BlockState fromState = fromStates[fromStateIndex];
            // Resource pack
            if (createResourcePackMappings) {
                BlockState resourcePackToState = result.resourcePackToStates[fromStateIndex];
                // Block
                event.manualMappings().registerNMS(builder -> {
                    builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                    builder.from(fromState);
                    builder.to(resourcePackToState);
                });
                // Resource pack entry
                if (createResourcePackBlockstatesEntries && result.isResourcePackToStateProxy[fromStateIndex] && createdResourcePackEntryForProxyStates.add(resourcePackToState)) {
                    ResourcePackConstructionImpl.get().addEventInitializer(resourcePackEvent -> {
                        BlockstatesResourcePackPath path = resourcePackEvent.asset(ClientView.AwarenessLevel.RESOURCE_PACK, "blockstates", resourcePackToState.getBlock().keyInBlockRegistry, "json").asBlockstates();
                        path.update(blockstates -> {
                            if (blockstates == null) {
                                blockstates = Blockstates.create();
                            }
                            Blockstates fromBlockBlockstates = PluginResourcePackDiscoveryImpl.get().getResourcePackBlockstates(fromState.getBlock().keyInBlockRegistry);
                            if (fromBlockBlockstates.hasVariants()) {
                                blockstates.setVariant(resourcePackToState.asBlockData(), Objects.requireNonNull(fromBlockBlockstates.getVariant(fromState.asBlockData()), "Missing blockstates variants entry for " + fromState));
                            } else if (fromBlockBlockstates.hasMultipart()) {
                                BlockData resourcePackToStateBukkit = resourcePackToState.asBlockData();
                                for (JsonObject multipart : fromBlockBlockstates.getMultipartApplies(fromState.asBlockData())) {
                                    blockstates.addMultipartApply(multipart, resourcePackToStateBukkit);
                                }
                            } else {
                                throw new IllegalStateException("Missing blockstates entry for " + fromState);
                            }
                            return blockstates;
                        });
                    });
                }
                // Item
                if (createItemMappings) {
                    createItemMappingForBlockStateMapping(
                        fromState,
                        resourcePackToState,
                        fromItemFunction,
                        toItemFunction,
                        ClientView.AwarenessLevel.RESOURCE_PACK
                    );
                }
            }
            // Vanilla
            if (createVanillaMappings) {
                BlockState vanillaToState = result.vanillaToStates[fromStateIndex];
                // Block
                event.manualMappings().registerNMS(builder -> {
                    builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                    builder.from(fromState);
                    builder.to(vanillaToState);
                });
                // Item
                if (createItemMappings) {
                    createItemMappingForBlockStateMapping(
                        fromState,
                        vanillaToState,
                        fromItemFunction,
                        toItemFunction,
                        ClientView.AwarenessLevel.VANILLA
                    );
                }
            }
        }
    }

}
