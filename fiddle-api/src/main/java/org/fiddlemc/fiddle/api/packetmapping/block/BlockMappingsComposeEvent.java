package org.fiddlemc.fiddle.api.packetmapping.block;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.util.composable.BuilderComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.ChangeRegisteredComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.GetRegisteredComposeEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Provides functionality to register mappings to the {@link BlockMappings}.
 *
 * <p>
 * Casting this instance to {@code BlockMappingsComposeEventNMS} and using its methods
 * with Minecraft internals gives slightly better performance.
 * </p>
 */
public interface BlockMappingsComposeEvent<M> extends BuilderComposeEvent<BlockMappingBuilder>, GetRegisteredComposeEvent<Pair<ClientView.AwarenessLevel, BlockData>, M>, ChangeRegisteredComposeEvent<Pair<ClientView.AwarenessLevel, BlockData>, M> {

    /**
     * @see #getRegistered(Object)
     */
    default List<M> getRegistered(ClientView.AwarenessLevel awarenessLevel, BlockData from) {
        return this.getRegistered(Pair.of(awarenessLevel, from));
    }

    /**
     * @see #changeRegistered(Object, Consumer)
     */
    default void changeRegistered(ClientView.AwarenessLevel awarenessLevel, BlockData from, Consumer<List<M>> listConsumer) {
        this.changeRegistered(Pair.of(awarenessLevel, from), listConsumer);
    }

    /**
     * A convenience function that calls {@link #register}
     * for each matching {@link BlockData} of the {@code from} and {@code to}.
     */
    default void registerStateToState(ClientView.AwarenessLevel awarenessLevel, BlockType from, BlockType to) {
        this.registerStateToState(List.of(awarenessLevel), from, to);
    }

    /**
     * @see #registerStateToState(ClientView.AwarenessLevel, BlockType, BlockType)
     */
    default void registerStateToState(ClientView.AwarenessLevel[] awarenessLevel, BlockType from, BlockType to) {
        this.registerStateToState(Arrays.asList(awarenessLevel), from, to);
    }

    /**
     * @see #registerStateToState(ClientView.AwarenessLevel, BlockType, BlockType)
     */
    default void registerStateToState(Collection<ClientView.AwarenessLevel> awarenessLevel, BlockType from, BlockType to) {
        this.registerStateToState(awarenessLevel, from.createBlockDataStates(), to);
    }

    /**
     * Calls {@link #registerStateToState} with
     * {@link ClientView.AwarenessLevel#getThatDoNotAlwaysUnderstandsAllServerSideBlocks()}.
     */
    default void registerStateToState(BlockType from, BlockType to) {
        this.registerStateToState(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(), from, to);
    }

    /**
     * A convenience function that calls {@link #register}
     * for each matching {@link BlockData} of the {@code from} and {@code to}.
     */
    default void registerStateToState(ClientView.AwarenessLevel awarenessLevel, Iterable<? extends BlockData> from, BlockType to) {
        this.registerStateToState(List.of(awarenessLevel), from, to);
    }

    /**
     * @see #registerStateToState(ClientView.AwarenessLevel, BlockType, BlockType)
     */
    default void registerStateToState(ClientView.AwarenessLevel[] awarenessLevel, Iterable<? extends BlockData> from, BlockType to) {
        this.registerStateToState(Arrays.asList(awarenessLevel), from, to);
    }

    /**
     * @see #registerStateToState(ClientView.AwarenessLevel, BlockType, BlockType)
     */
    default void registerStateToState(Collection<ClientView.AwarenessLevel> awarenessLevel, Iterable<? extends BlockData> from, BlockType to) {
        for (BlockData fromState : from) {
            BlockData toState = to.createBlockData();
            fromState.copyTo(toState);
            this.register(builder -> {
                builder.awarenessLevel(awarenessLevel);
                builder.from(fromState);
                builder.to(toState);
            });
        }
    }

    /**
     * Calls {@link #registerStateToState} with
     * {@link ClientView.AwarenessLevel#getThatDoNotAlwaysUnderstandsAllServerSideBlocks()}.
     */
    default void registerStateToState(Iterable<? extends BlockData> from, BlockType to) {
        this.registerStateToState(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(), from, to);
    }

}
