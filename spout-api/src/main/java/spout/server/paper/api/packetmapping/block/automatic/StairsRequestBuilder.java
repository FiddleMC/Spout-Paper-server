package spout.server.paper.api.packetmapping.block.automatic;

import org.bukkit.block.BlockType;

/**
 * A {@link ProxyStatesRequestBuilder} for {@link AutomaticBlockMappings#stairs}.
 *
 * <p>
 * By default:
 * <ul>
 *     <li>{@link #fallback()} is {@link BlockType#STONE_STAIRS}.</li>
 * </ul>
 */
public interface StairsRequestBuilder extends FromBlockTypeRequestBuilder<UsedStates.Stairs>, ToBlockTypeRequestBuilder<UsedStates.Stairs> {
}
