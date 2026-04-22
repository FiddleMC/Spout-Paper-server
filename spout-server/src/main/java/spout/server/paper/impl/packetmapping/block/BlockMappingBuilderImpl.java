package spout.server.paper.impl.packetmapping.block;

import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import spout.server.paper.api.packetmapping.block.BlockMappingBuilder;
import spout.server.paper.api.packetmapping.block.BlockMappingHandle;
import java.util.Collection;

/**
 * The implementation of {@link BlockMappingBuilder}.
 */
public class BlockMappingBuilderImpl extends AbstractBlockMappingBuilderImpl<BlockData, BlockMappingHandle> implements BlockMappingBuilder {

    @Override
    protected Collection<BlockData> getStatesToRegisterFor() {
        return this.from;
    }

    @Override
    protected BlockMappingsStep createFunctionStep() {
        return new BukkitFunctionBlockMappingsStep(this.function, this.functionRequiresCoordinates);
    }

    @Override
    protected BlockState getSimpleTo() {
        return ((CraftBlockData) this.to).getState();
    }

}
