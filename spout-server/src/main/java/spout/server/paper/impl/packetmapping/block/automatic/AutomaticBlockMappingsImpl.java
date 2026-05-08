package spout.server.paper.impl.packetmapping.block.automatic;

import org.bukkit.block.BlockType;
import spout.server.paper.api.packetmapping.block.automatic.FromBlockStateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.FromBlockTypeRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.LeavesRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.AutomaticBlockMappings;
import spout.server.paper.api.packetmapping.block.automatic.SlabRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.ToBlockStateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.ToBlockTypeRequestBuilder;
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
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void brushable(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl builder = new FromToBlockTypeRequestBuilderImpl();
        builder.fallback(BlockType.SUSPICIOUS_SAND);
        ((Consumer) builderConsumer).accept(builder);
        new BrushableRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void button(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl builder = new FromToBlockTypeRequestBuilderImpl();
        builder.fallback(BlockType.STONE_BUTTON);
        ((Consumer) builderConsumer).accept(builder);
        new ButtonRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void door(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl builder = new FromToBlockTypeRequestBuilderImpl();
        builder.fallback(BlockType.OAK_DOOR);
        ((Consumer) builderConsumer).accept(builder);
        new DoorRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void fenceGate(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl builder = new FromToBlockTypeRequestBuilderImpl();
        builder.fallback(BlockType.OAK_FENCE_GATE);
        ((Consumer) builderConsumer).accept(builder);
        new FenceGateRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockStateRequestBuilder & ToBlockStateRequestBuilder> void fullBlock(Consumer<? extends B> builderConsumer) {
        FromToBlockStateRequestBuilderImpl builder = new FromToBlockStateRequestBuilderImpl();
        builder.fallbackDefaultStateOf(BlockType.STONE);
        ((Consumer) builderConsumer).accept(builder);
        new FullBlockRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void ladder(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl builder = new FromToBlockTypeRequestBuilderImpl();
        builder.fallback(BlockType.LADDER);
        ((Consumer) builderConsumer).accept(builder);
        new LadderRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends LeavesRequestBuilder> void leaves(Consumer<? extends B> builderConsumer) {
        LeavesRequestBuilderImpl builder = new LeavesRequestBuilderImpl();
        builder.fallback(BlockType.OAK_LEAVES);
        ((Consumer) builderConsumer).accept(builder);
        new LeavesRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void pressurePlate(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl builder = new FromToBlockTypeRequestBuilderImpl();
        builder.fallback(BlockType.STONE_PRESSURE_PLATE);
        ((Consumer) builderConsumer).accept(builder);
        new PressurePlateRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends SlabRequestBuilder> void slab(Consumer<? extends B> builderConsumer) {
        SlabRequestBuilderImpl builder = new SlabRequestBuilderImpl();
        builder.fallback(BlockType.STONE_SLAB);
        ((Consumer) builderConsumer).accept(builder);
        new SlabRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void stairs(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl builder = new FromToBlockTypeRequestBuilderImpl();
        builder.fallback(BlockType.STONE_STAIRS);
        ((Consumer) builderConsumer).accept(builder);
        new StairsRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void trapdoor(Consumer<? extends B> builderConsumer) {
        FromToBlockTypeRequestBuilderImpl builder = new FromToBlockTypeRequestBuilderImpl();
        builder.fallback(BlockType.OAK_TRAPDOOR);
        ((Consumer) builderConsumer).accept(builder);
        new TrapdoorRequestProcessor(builder, this.event).process();
    }

}
