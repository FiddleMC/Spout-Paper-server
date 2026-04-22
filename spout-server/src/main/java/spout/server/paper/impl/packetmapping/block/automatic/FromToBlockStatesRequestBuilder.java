package spout.server.paper.impl.packetmapping.block.automatic;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.state.BlockState;
import spout.server.paper.api.resourcepack.content.Blockstates;
import spout.server.paper.impl.resourcepack.plugin.discover.PluginResourcePackDiscoveryImpl;

/**
 * Common interface for {@link FromToBlockStateRequestBuilderImpl}
 * and {@link FromToBlockTypeRequestBuilderImpl}.
 */
public interface FromToBlockStatesRequestBuilder {

    BlockState[] fromStates();

    BlockState[] fallbackStates();

    default JsonObject getBlockstatesVariant(int fromStateI) {
        BlockState fromState = this.fromStates()[fromStateI];
        Blockstates blockstates = PluginResourcePackDiscoveryImpl.get().getResourcePackBlockstates(fromState.getBlock().keyInBlockRegistry);
        return blockstates.getVariant(fromState.createCraftBlockData());
    }

}
