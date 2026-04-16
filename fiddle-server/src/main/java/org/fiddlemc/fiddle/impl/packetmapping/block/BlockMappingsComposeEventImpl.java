package org.fiddlemc.fiddle.impl.packetmapping.block;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.BlockMappingsComposeEvent;
import org.fiddlemc.fiddle.api.packetmapping.block.ManualBlockMappings;
import org.fiddlemc.fiddle.api.packetmapping.block.claim.ClaimRequestPriorityComparator;
import org.fiddlemc.fiddle.api.packetmapping.block.claim.ResourcePackBlockStateClaims;
import org.fiddlemc.fiddle.api.packetmapping.block.nms.BlockMappingBuilderNMS;
import org.fiddlemc.fiddle.api.packetmapping.block.nms.ManualBlockMappingsNMS;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.AutomaticBlockMappings;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockStateRegistry;
import org.fiddlemc.fiddle.impl.packetmapping.block.claim.ResourcePackBlockStateClaimsImpl;
import org.fiddlemc.fiddle.impl.packetmapping.block.automatic.AutomaticBlockMappingsImpl;
import org.fiddlemc.fiddle.impl.util.composable.AwarenessLevelPairKeyedBuilderComposeEventImpl;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * The implementation of {@link BlockMappingsComposeEvent}.
 */
public final class BlockMappingsComposeEventImpl extends AwarenessLevelPairKeyedBuilderComposeEventImpl<BlockData, BlockMappingsStep, BlockMappingBuilder> implements BlockMappingsComposeEvent, ManualBlockMappingsNMS<BlockMappingsStep> {

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
    public AutomaticBlockMappingsImpl automaticMappings(PluginBootstrap bootstrap) {
        return new AutomaticBlockMappingsImpl(this, bootstrap);
    }

}
