package spout.server.paper.impl.packetmapping.block.automatic;

import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import spout.server.paper.api.packetmapping.block.automatic.FromBlockStateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.FromBlockTypeRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.LeavesRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.AutomaticBlockMappings;
import spout.server.paper.api.packetmapping.block.automatic.SlabRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.ToBlockStateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.ToBlockTypeRequestBuilder;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * The implementation of {@link AutomaticBlockMappings}.
 */
public final class AutomaticBlockMappingsImpl implements AutomaticBlockMappings {

    private final BlockMappingsComposeEventImpl event;

    public AutomaticBlockMappingsImpl(BlockMappingsComposeEventImpl event) {
        this.event = event;
    }

    private <B extends FromBlockStateRequestBuilder & ToBlockStateRequestBuilder> void simpleBlockState(
        BlockData defaultFallback,
        BiFunction<FromToBlockStateRequestBuilderImpl, BlockMappingsComposeEventImpl, RequestProcessor<FromToBlockStateRequestBuilderImpl>> processorFactory,
        Consumer<? extends B> builderConsumer
    ) {
        FromToBlockStateRequestBuilderImpl builder = new FromToBlockStateRequestBuilderImpl();
        builder.fallback(defaultFallback);
        ((Consumer) builderConsumer).accept(builder);
        processorFactory.apply(builder, event).process();
    }

    private <B extends FromBlockStateRequestBuilder & ToBlockStateRequestBuilder> void simpleBlockStateDefaultStateOf(
        BlockType defaultFallback,
        BiFunction<FromToBlockStateRequestBuilderImpl, BlockMappingsComposeEventImpl, RequestProcessor<FromToBlockStateRequestBuilderImpl>> processorFactory,
        Consumer<? extends B> builderConsumer
    ) {
        this.simpleBlockState(defaultFallback.createBlockData(), processorFactory, builderConsumer);
    }

    private <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void simpleBlockType(
        BlockType defaultFallback,
        BiFunction<FromToBlockTypeRequestBuilderImpl, BlockMappingsComposeEventImpl, RequestProcessor<FromToBlockTypeRequestBuilderImpl>> processorFactory,
        Consumer<? extends B> builderConsumer
    ) {
        FromToBlockTypeRequestBuilderImpl builder = new FromToBlockTypeRequestBuilderImpl();
        builder.fallback(defaultFallback);
        ((Consumer) builderConsumer).accept(builder);
        processorFactory.apply(builder, this.event).process();
    }

    private <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void blockTypeFullBlock(BlockType defaultFallback, Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(defaultFallback, FullBlockBlockTypeRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void barrel(Consumer<? extends B> builderConsumer) {
        this.blockTypeFullBlock(BlockType.BARREL, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void brushable(Consumer<? extends B> builderConsumer) {
        this.blockTypeFullBlock(BlockType.SUSPICIOUS_SAND, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void button(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.STONE_BUTTON, StandardBlockTypeRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void chain(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.IRON_CHAIN, ChainRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void chiseledBookshelf(Consumer<? extends B> builderConsumer) {
        this.blockTypeFullBlock(BlockType.CHISELED_BOOKSHELF, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void door(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.OAK_DOOR, DoorRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void fence(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.OAK_FENCE, StandardBlockTypeRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void fenceGate(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.OAK_FENCE_GATE, FenceGateRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void flowerPot(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.FLOWER_POT, StandardBlockTypeRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockStateRequestBuilder & ToBlockStateRequestBuilder> void fullBlock(Consumer<? extends B> builderConsumer) {
        this.simpleBlockStateDefaultStateOf(BlockType.STONE, FullBlockRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void furnace(Consumer<? extends B> builderConsumer) {
        this.blockTypeFullBlock(BlockType.FURNACE, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void glazedTerracotta(Consumer<? extends B> builderConsumer) {
        this.blockTypeFullBlock(BlockType.WHITE_GLAZED_TERRACOTTA, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void ladder(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.LADDER, StandardBlockTypeRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends LeavesRequestBuilder> void leaves(Consumer<? extends B> builderConsumer) {
        LeavesRequestBuilderImpl builder = new LeavesRequestBuilderImpl();
        builder.fallback(BlockType.OAK_LEAVES);
        ((Consumer) builderConsumer).accept(builder);
        new LeavesRequestProcessor(builder, this.event).process();
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void loom(Consumer<? extends B> builderConsumer) {
        this.blockTypeFullBlock(BlockType.LOOM, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void netherPortal(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.NETHER_PORTAL, NetherPortalRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void pressurePlate(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.STONE_PRESSURE_PLATE, PressurePlateRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void pumpkin(Consumer<? extends B> builderConsumer) {
        this.blockTypeFullBlock(BlockType.PUMPKIN, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void redstoneOre(Consumer<? extends B> builderConsumer) {
        this.blockTypeFullBlock(BlockType.REDSTONE_ORE, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void respawnAnchor(Consumer<? extends B> builderConsumer) {
        this.blockTypeFullBlock(BlockType.RESPAWN_ANCHOR, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void rotatedPillar(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.QUARTZ_PILLAR, RotatedPillarRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void sapling(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.OAK_SAPLING, SaplingRequestProcessor::new, builderConsumer);
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
        this.simpleBlockType(BlockType.STONE_STAIRS, StairsRequestProcessor::new, builderConsumer);
    }

    @Override
    public <B extends FromBlockTypeRequestBuilder & ToBlockTypeRequestBuilder> void trapdoor(Consumer<? extends B> builderConsumer) {
        this.simpleBlockType(BlockType.OAK_TRAPDOOR, TrapdoorRequestProcessor::new, builderConsumer);
    }

}
