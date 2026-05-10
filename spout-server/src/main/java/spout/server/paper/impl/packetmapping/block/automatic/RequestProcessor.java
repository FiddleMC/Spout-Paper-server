package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.packetmapping.block.claim.ClaimRequestPriority;
import spout.server.paper.api.packetmapping.item.nms.ItemMappingUtilitiesNMS;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockStateRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import spout.server.paper.impl.packetmapping.block.claim.ResourcePackBlockStateClaimsImpl;
import spout.server.paper.impl.packetmapping.item.ItemMappingsImpl;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A processor for {@link ProxyStatesRequestBuilderImpl}s.
 *
 * <p>
 * Each processor instance is for one {@link ProxyStatesRequestBuilderImpl}.
 * </p>
 */
public abstract class RequestProcessor<R extends ProxyStatesRequestBuilderImpl> {

    protected final R request;
    protected final BlockMappingsComposeEventImpl event;

    protected RequestProcessor(R request, BlockMappingsComposeEventImpl event) {
        this.request = request;
        this.event = event;
    }

    public void process() {
        this.validateArguments();
        this.processAfterValidateArguments();
    }

    protected void validateArguments() {
        this.request.validateArguments();
    }

    protected abstract void processAfterValidateArguments();

    // /**
    //  * Attempts to {@linkplain ResourcePackBlockStateClaimsImpl#claim claim} block states for use as
    //  * proxy states for some given states.
    //  *
    //  * @param event                                The {@link BlockMappingsComposeEventImpl} context.
    //  * @param from                                 The states that the claim is intended for.
    //  * @param proxyCandidates                      The candidate proxy states, each at the same index as the
    //  *                                             corresponding {@code from} state (so it may contain duplicates).
    //  * @param priority                             The pre-computed {@link ClaimRequestPriority},
    //  *                                             or null to have it be automatically computed based on {@code from}.
    //  * @param createProxyToVisualDuplicateMappings If this is true and the claim is successful,
    //  *                                             the claimed proxy states will be mapped to visual duplicates
    //  *                                             for {@link ClientView.AwarenessLevel#RESOURCE_PACK} clients.
    //  * @param createItemMappings                   If this is true, and {@code createProxyToVisualDuplicateMapping}
    //  *                                             is also true, and the claim successful,
    //  *                                             item mappings may be created from items corresponding
    //  *                                             to the claimed proxy states to items corresponding to their
    //  *                                             visual duplicates.
    //  */
    // public static void a() {
    //
    //     if (priority == null) {
    //         priority = ClaimRequestPriority.forBlockStates(Arrays.stream(from).map(state -> (BlockData) state.asBlockData()).toArray(BlockData[]::new));
    //     }
    //
    // }

    public void attemptClaimOfProxyStates(
        BlockState[] proxyCandidateStates,
        ClaimRequestPriority priority,
        @Nullable Consumer<int @Nullable []> resultConsumer
    ) {
        attemptClaimOfProxyStates(
            this.event,
            proxyCandidateStates,
            priority,
            this.request.createProxyToVisualDuplicateMappings,
            this.request.createItemMappings,
            resultConsumer
        );
    }

    /**
     * Attempts to {@linkplain ResourcePackBlockStateClaimsImpl#claim claim} block states for use as
     * proxy states for some given states.
     *
     * @param event                                The {@link BlockMappingsComposeEventImpl} context.
     * @param proxyCandidateStates                 The candidate proxy states, which must be distinct.
     * @param priority                             A pre-computed {@link ClaimRequestPriority}.
     * @param createProxyToVisualDuplicateMappings If this is true and the claim is successful,
     *                                             the claimed proxy states will be mapped to visual duplicates
     *                                             for {@link ClientView.AwarenessLevel#RESOURCE_PACK} clients.
     * @param createItemMappings                   If this is true, and {@code createProxyToVisualDuplicateMapping}
     *                                             is also true, and the claim successful,
     *                                             item mappings may be created from items corresponding
     *                                             to the claimed proxy states to items corresponding to their
     *                                             visual duplicates.
     * @param resultConsumer                       The {@code resultConsumer} passed to
     *                                             {@link ResourcePackBlockStateClaimsImpl#claim}.
     */
    public static void attemptClaimOfProxyStates(
        BlockMappingsComposeEventImpl event,
        BlockState[] proxyCandidateStates,
        ClaimRequestPriority priority,
        boolean createProxyToVisualDuplicateMappings,
        boolean createItemMappings,
        @Nullable Consumer<int @Nullable []> resultConsumer
    ) {
        // Translate the proxy candidates to their registry indices
        int[] proxyCandidateStateIndicesInRegistry = new int[proxyCandidateStates.length];
        for (int proxyCandidateStateIndex = 0; proxyCandidateStateIndex < proxyCandidateStateIndicesInRegistry.length; proxyCandidateStateIndex++) {
            proxyCandidateStateIndicesInRegistry[proxyCandidateStateIndex] = proxyCandidateStates[proxyCandidateStateIndex].indexInVanillaOnlyBlockStateRegistry;
        }
        // Attempt the claim
        ResourcePackBlockStateClaimsImpl.get().claim(
            proxyCandidateStateIndicesInRegistry,
            priority,
            resultConsumer,
            createProxyToVisualDuplicateMappings ? visualDuplicateStateIndicesInRegistry -> {
                // For resource pack clients, map the claimed proxy states to their visual duplicate
                for (int i = 0; i < visualDuplicateStateIndicesInRegistry.length; i++) {
                    BlockState visualDuplicateState = VanillaOnlyBlockStateRegistry.get().byId(visualDuplicateStateIndicesInRegistry[i]);
                    BlockState proxyState = proxyCandidateStates[i];
                    // Block
                    event.registerNMS(
                        ClientView.AwarenessLevel.RESOURCE_PACK,
                        proxyState,
                        visualDuplicateState
                    );
                    // Item
                    if (createItemMappings) {
                        createItemMappingForBlockStateMapping(
                            proxyState,
                            visualDuplicateState,
                            null,
                            null,
                            ClientView.AwarenessLevel.RESOURCE_PACK
                        );
                    }
                }
            } : null,
            true,
            false
        );
    }

    public static void createItemMappingForBlockStateMapping(
        BlockState fromState,
        BlockState targetState,
        @Nullable Function<BlockState, @Nullable Item> fromItemFunction,
        @Nullable Function<BlockState, @Nullable Item> targetItemFunction,
        ClientView.AwarenessLevel awarenessLevel
    ) {
        // Determine the from item
        Item fromItem;
        if (fromItemFunction != null) {
            fromItem = fromItemFunction.apply(fromState);
        } else {
            if (fromState == fromState.getBlock().defaultBlockState()) {
                fromItem = FromToItemRequestBuilderImpl.inferItem(fromState.getBlock());
            } else {
                fromItem = null;
            }
        }
        if (fromItem == null) {
            return;
        }
        // Determine the target item
        Item targetItem;
        if (targetItemFunction != null) {
            targetItem = targetItemFunction.apply(targetState);
        } else {
            targetItem = FromToItemRequestBuilderImpl.inferItem(targetState.getBlock());
        }
        if (targetItem == null) {
            return;
        }
        // Register the mapping
        ItemMappingsImpl.get().addEventInitializer(itemMappingsEvent -> {
            @Nullable BlockItemStateProperties toBlockItemStateProperties;
            Item toItem;
            if (targetState == targetState.getBlock().defaultBlockState() && BlocksWithVisuallyDifferentBlockstates.check(fromState.getBlock())) {
                // Changing the block state component will not help this mapping, and only potentially mess other mappings up
                toBlockItemStateProperties = null;
                toItem = targetItem;
            } else if (!haveSameHoneyLevel(targetState, fromState)) {
                // Setting the block state component would show an undesired honey level meter, so we use barrier instead
                toBlockItemStateProperties = null;
                toItem = Items.BARRIER;
            } else {
                toBlockItemStateProperties = new BlockItemStateProperties(targetState.asBlockData().toStates(true));
                toItem = targetItem;
            }
            @Nullable Identifier toItemModel = (awarenessLevel == ClientView.AwarenessLevel.VANILLA || toItem == fromItem) ? null : fromItem.getDefaultInstance().getOrDefault(DataComponents.ITEM_MODEL, fromItem.keyInItemRegistry);
            if (fromItem != toItem || toItemModel != null || toBlockItemStateProperties != null) {
                itemMappingsEvent.registerNMS(builder -> {
                    builder.awarenessLevel(awarenessLevel);
                    builder.from(fromItem);
                    builder.to(handle -> {
                        if (fromItem != toItem) {
                            ItemMappingUtilitiesNMS.get().setItemWhilePreservingRest(handle, toItem);
                        }
                        ItemStack itemStack = handle.getMutable();
                        if (toItemModel != null) {
                            itemStack.set(DataComponents.ITEM_MODEL, toItemModel);
                        }
                        if (toBlockItemStateProperties != null) {
                            itemStack.set(DataComponents.BLOCK_STATE, toBlockItemStateProperties);
                        }
                    });
                });
            }
        });
    }

    public static boolean haveSameHoneyLevel(@Nullable BlockState state1, @Nullable BlockState state2) {
        @Nullable Integer level1 = (state1 != null && state1.hasProperty(BlockStateProperties.LEVEL_HONEY)) ? state1.getValue(BlockStateProperties.LEVEL_HONEY) : null;
        @Nullable Integer level2 = (state2 != null && state2.hasProperty(BlockStateProperties.LEVEL_HONEY)) ? state2.getValue(BlockStateProperties.LEVEL_HONEY) : null;
        return Objects.equals(level1, level2);
    }

}
