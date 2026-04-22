package spout.server.paper.impl.packetmapping.block;

import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import spout.server.paper.api.packetmapping.block.BlockMappingBuilder;
import spout.server.paper.api.packetmapping.block.BlockMappingsComposeEvent;
import spout.server.paper.api.packetmapping.block.nms.BlockMappingBuilderNMS;
import spout.server.paper.api.packetmapping.block.nms.ManualBlockMappingsNMS;
import spout.server.paper.impl.moredatadriven.minecraft.BlockStateRegistry;
import spout.server.paper.impl.packetmapping.block.automatic.AutomaticBlockMappingsImpl;
import spout.server.paper.impl.util.composable.AwarenessLevelPairKeyedBuilderComposeEventImpl;
import org.jspecify.annotations.Nullable;
import java.util.function.Consumer;

/**
 * The implementation of {@link BlockMappingsComposeEvent}.
 */
public final class BlockMappingsComposeEventImpl extends AwarenessLevelPairKeyedBuilderComposeEventImpl<BlockData, BlockMappingsStep, BlockMappingBuilder> implements BlockMappingsComposeEvent, ManualBlockMappingsNMS<BlockMappingsStep> {

    private @Nullable AutomaticBlockMappingsImpl automaticMappings;

    @Override
    public void register(Consumer<BlockMappingBuilder> builderConsumer) {
        BlockMappingBuilderImpl builder = new BlockMappingBuilderImpl();
        builderConsumer.accept(builder);
        builder.registerWith(this);
    }

    @Override
    public void registerNMS(Consumer<BlockMappingBuilderNMS> builderConsumer) {
        BlockMappingBuilderNMSImpl builder = new BlockMappingBuilderNMSImpl();
        builderConsumer.accept(builder);
        builder.registerWith(this);
    }

    @Override
    protected int keyPartToInt(final BlockData key) {
        return ((CraftBlockData) key).getState().indexInBlockStateRegistry;
    }

    @Override
    protected BlockData intToKeyPart(final int internalKey) {
        return BlockStateRegistry.get().byId(internalKey).createCraftBlockData();
    }

    @Override
    public BlockMappingsComposeEventImpl manualMappings() {
        return this;
    }

    @Override
    public AutomaticBlockMappingsImpl automaticMappings() {
        if (this.automaticMappings == null) {
            this.automaticMappings = new AutomaticBlockMappingsImpl(this);
        }
        return this.automaticMappings;
    }

}
