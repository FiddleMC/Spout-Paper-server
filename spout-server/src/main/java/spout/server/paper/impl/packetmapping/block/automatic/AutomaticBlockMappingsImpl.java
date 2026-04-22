package spout.server.paper.impl.packetmapping.block.automatic;

import spout.server.paper.api.packetmapping.block.automatic.FullBlockRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.LeavesRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.AutomaticBlockMappings;
import spout.server.paper.api.packetmapping.block.automatic.SlabRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.StairsRequestBuilder;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.function.Consumer;

/**
 * The implementation of {@link AutomaticBlockMappings}.
 */
public final class AutomaticBlockMappingsImpl implements AutomaticBlockMappings {

    private final BlockMappingsComposeEventImpl event;

    public AutomaticBlockMappingsImpl(BlockMappingsComposeEventImpl event) {
        this.event = event;
    }

    @Override
    public void fullBlock(Consumer<FullBlockRequestBuilder> builderConsumer) {
        FullBlockRequestBuilderImpl builder = new FullBlockRequestBuilderImpl();
        builderConsumer.accept(builder);
        new FullBlockRequestProcessor(builder, this.event).process();
    }

    @Override
    public void slab(Consumer<SlabRequestBuilder> builderConsumer) {
        SlabRequestBuilderImpl builder = new SlabRequestBuilderImpl();
        builderConsumer.accept(builder);
        new SlabRequestProcessor(builder, this.event).process();
    }

    @Override
    public void stairs(Consumer<StairsRequestBuilder> builderConsumer) {
        StairsRequestBuilderImpl builder = new StairsRequestBuilderImpl();
        builderConsumer.accept(builder);
        new StairsRequestProcessor(builder, this.event).process();
    }

    @Override
    public void leaves(Consumer<LeavesRequestBuilder> builderConsumer) {
        LeavesRequestBuilderImpl builder = new LeavesRequestBuilderImpl();
        builderConsumer.accept(builder);
        new LeavesRequestProcessor(builder, this.event).process();
    }

}
