package spout.server.paper.api.packetmapping.block.automatic;

import org.bukkit.block.BlockType;

/**
 * A {@link ProxyStatesRequestBuilder} for {@link AutomaticBlockMappings#pressurePlate}.
 *
 * <p>
 * By default:
 * <ul>
 *     <li>{@link #fallback()} is {@link BlockType#STONE_PRESSURE_PLATE}.</li>
 * </ul>
 */
public interface PressurePlateRequestBuilder extends FromBlockTypeRequestBuilder<UsedStates.PressurePlate>, ToBlockTypeRequestBuilder<UsedStates.PressurePlate> {
}
