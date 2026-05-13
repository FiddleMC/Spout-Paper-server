package spout.server.paper.api.packetmapping.block.automatic;

import org.bukkit.block.BlockType;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.packetmapping.block.BlockMappingsComposeEvent;
import java.util.function.Consumer;

/**
 * Provides proxy mapping functionality to
 * {@link BlockMappingsComposeEvent}.
 *
 * <p>
 * Proxy mappings are mappings where server-side blocks are mapped
 * (for {@linkplain ClientView.AwarenessLevel#RESOURCE_PACK players who have the resource pack})
 * to vanilla block states that have a visual duplicate. The look of that target block state is then overridden
 * in its corresponding {@code blockstates} resource pack file.
 * <br>
 * Actual occurrences of the target block state are mapped to its visual duplicate.
 * <br>
 * Examples of usable vanilla block states are note block states, leaves states
 * and cut copper stairs (which have a visually identical waxed variant).
 * <br>
 * Because these target block states can only have one visual look each, they are claimed on a
 * first-come first-serve basis. When no more block states are available, the server-side blocks
 * are instead mapped to existing vanilla fallback states that look and behave similar, but not the same.
 * <br>
 * By default, the fallback states are assumed to also be the best target block states for
 * {@linkplain ClientView.AwarenessLevel#VANILLA players without the resource pack}.
 * </p>
 *
 * <p>
 * Note that for all methods of this class,
 * the requests should be assumed to be for blocks that are
 * <ul>
 *     <li>a full cube visually,</li>
 *     <li>solid with respect to collisions, and</li>
 *     <li>non-light-emitting</li>
 * </ul>
 * unless otherwise mentioned.
 * Using a method for a different purpose can lead to visual and gameplay glitches.
 * </p>
 */
public interface AutomaticBlockMappings {

    /**
     * Attempts to find a proxy state for every possible state of a barrel block.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#BARREL}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void barrel(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a brushable block.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#SUSPICIOUS_SAND}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void brushable(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a button.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#STONE_BUTTON}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void button(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a chain.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#IRON_CHAIN}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void chain(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a chiseled bookshelf.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#CHISELED_BOOKSHELF}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void chiseledBookshelf(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a door.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#OAK_DOOR}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void door(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a fence.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#OAK_FENCE}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void fence(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a fence gate.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#OAK_FENCE_GATE}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void fenceGate(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of an empty or filled flower pot.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#FLOWER_POT}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void flowerPot(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for a single full block state.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockStateRequestBuilder#fallback()} is the default block state of {@link BlockType#STONE}.</li>
     * </ul>
     */
    <B extends FromBlockStateRequestBuilder & ToBlockStateRequestBuilder> void fullBlock(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a furnace block.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#FURNACE}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void furnace(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a glazed terracotta block.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#WHITE_GLAZED_TERRACOTTA}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void glazedTerracotta(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a ladder.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#LADDER}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void ladder(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find proxy states for 2 leaves block states:
     * 1 non-waterlogged and 1 waterlogged.
     *
     * <p>
     * This is not suitable for blocks that are "less" instabreak than leaves.
     * The vanilla client generally believes leaves are instabreak when broken with the right tool.
     * In practice, this means you should only use this for server-side leaves.
     * </p>
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#OAK_LEAVES}.</li>
     * </ul>
     */
    <B extends LeavesRequestBuilder> void leaves(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a loom.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#LOOM}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void loom(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a nether portal.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#NETHER_PORTAL}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void netherPortal(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a pressure plate.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#STONE_PRESSURE_PLATE}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void pressurePlate(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a pumpkin block.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#PUMPKIN}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void pumpkin(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a redstone ore block.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#REDSTONE_ORE}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void redstoneOre(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a respawn anchor.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#RESPAWN_ANCHOR}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void respawnAnchor(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a rotated pillar block.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#QUARTZ_PILLAR}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void rotatedPillar(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a sapling.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#OAK_SAPLING}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void sapling(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a slab.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#STONE_SLAB}.</li>
     * </ul>
     */
    <B extends SlabRequestBuilder> void slab(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of stairs.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#STONE_STAIRS}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void stairs(Consumer<? extends B> builderConsumer);

    /**
     * Attempts to find a proxy state for every possible state of a trap door.
     *
     * <p>
     * By default:
     * <ul>
     *     <li>{@link ToBlockTypeRequestBuilder#fallback()} is {@link BlockType#OAK_TRAPDOOR}.</li>
     * </ul>
     */
    <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void trapdoor(Consumer<? extends B> builderConsumer);

}
