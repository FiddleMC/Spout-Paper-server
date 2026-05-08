package spout.server.paper.api.packetmapping.block.automatic;

import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import org.bukkit.block.data.BlockData;

/**
 * The states that are chosen as mapping targets for a
 * requested proxy mapping.
 */
public interface UsedStates {

    /**
     * @return The used block state for the given 'from' block state (i.e. the block state
     * for which the mapping was requested), and whether the used block state is a proxy
     * (if false, it is fallback).
     */
    ObjectBooleanPair<BlockData> get(BlockData from);

}
