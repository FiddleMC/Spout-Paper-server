package spout.common.builtincontent.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * A vertical slab.
 */
public class VerticalSlabBlock extends Block implements SimpleWaterloggedBlock {

    public static final MapCodec<VerticalSlabBlock> CODEC = simpleCodec(VerticalSlabBlock::new);

    public static final EnumProperty<VerticalSlabType> TYPE = SpoutBlockStateProperties.VERTICAL_SLAB_TYPE;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /**
     * An array of {@link VoxelShape} that can be efficiently queried,
     * where the index to a block state is calculated as:
     * {@link #TYPE}<code>.ordinal() + 3 * (</code>{@link #AXIS}<code> == X ? 0 : 1)</code>.
     */
    private static final VoxelShape[] SHAPES;

    static {
        SHAPES = AXIS.getPossibleValues().stream().flatMap(axis -> TYPE.getPossibleValues().stream().map(type -> {
            double start = type == VerticalSlabType.HIGHER ? 8.0 : 0.0;
            double end = type == VerticalSlabType.LOWER ? 8.0 : 16.0;
            return Block.box(axis == Direction.Axis.X ? start : 0.0, 0.0, axis == Direction.Axis.Z ? start : 0.0, axis == Direction.Axis.X ? end : 16.0, 16.0, axis == Direction.Axis.Z ? end : 16.0);
        })).toArray(VoxelShape[]::new);
    }

    public VerticalSlabBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(TYPE, VerticalSlabType.LOWER)
                .setValue(AXIS, Direction.Axis.X)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    public MapCodec<? extends VerticalSlabBlock> codec() {
        return CODEC;
    }

    /**
     * @see SlabBlock#useShapeForLightOcclusion
     */
    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return !isFull(state);
    }

    /**
     * @see SlabBlock#createBlockStateDefinition
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
            TYPE,
            AXIS,
            WATERLOGGED
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[(state.getValue(TYPE).ordinal() + (state.getValue(AXIS) == Direction.Axis.X ? 0 : 3))];
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState replacedState = context.getLevel().getBlockState(pos);

        // Placing onto an existing vertical slab -> make double slab
        if (replacedState.is(this)) {
            return replacedState
                .setValue(TYPE, VerticalSlabType.DOUBLE)
                .setValue(WATERLOGGED, false);
        }

        FluidState fluidState = context.getLevel().getFluidState(pos);

        Direction clickedFace = context.getClickedFace();

        // Determine axis from placement direction.
        // Clicking east/west -> slab split along X
        // Clicking north/south -> slab split along Z
        Direction.Axis axis = switch (clickedFace.getAxis()) {
            case X -> Direction.Axis.X;
            case Z -> Direction.Axis.Z;

            // If placing on top/bottom, use player facing
            default -> context.getHorizontalDirection().getAxis();
        };

        double local =
            axis == Direction.Axis.X
                ? context.getClickLocation().x - pos.getX()
                : context.getClickLocation().z - pos.getZ();

        VerticalSlabType type =
            local > 0.5
                ? VerticalSlabType.HIGHER
                : VerticalSlabType.LOWER;

        return this.defaultBlockState()
            .setValue(AXIS, axis)
            .setValue(TYPE, type)
            .setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        VerticalSlabType type = state.getValue(TYPE);

        // Only merge with same item and non-double slabs
        if (type == VerticalSlabType.DOUBLE || !stack.is(this.asItem())) {
            return false;
        }

        Direction.Axis axis = state.getValue(AXIS);

        // Position within block along slab axis
        double local =
            axis == Direction.Axis.X
                ? context.getClickLocation().x - context.getClickedPos().getX()
                : context.getClickLocation().z - context.getClickedPos().getZ();

        boolean higher = local > 0.5;

        if (context.replacingClickedOnBlock()) {
            Direction clickedFace = context.getClickedFace();

            if (axis == Direction.Axis.X) {
                return type == VerticalSlabType.LOWER
                    ? clickedFace == Direction.EAST
                    || (higher && clickedFace.getAxis().isVertical())
                    : clickedFace == Direction.WEST
                    || (!higher && clickedFace.getAxis().isVertical());
            } else {
                return type == VerticalSlabType.LOWER
                    ? clickedFace == Direction.SOUTH
                    || (higher && clickedFace.getAxis().isVertical())
                    : clickedFace == Direction.NORTH
                    || (!higher && clickedFace.getAxis().isVertical());
            }
        }

        return true;
    }

    /**
     * @see SlabBlock#getFluidState
     */
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    /**
     * @see SlabBlock#placeLiquid
     */
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return !isFull(state) && SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }

    /**
     * @see SlabBlock#canPlaceLiquid
     */
    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity owner, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return !isFull(state) && SimpleWaterloggedBlock.super.canPlaceLiquid(owner, level, pos, state, fluid);
    }

    /**
     * @see SlabBlock#updateShape
     */
    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess scheduledTickAccess,
        BlockPos pos,
        Direction direction,
        BlockPos neighborPos,
        BlockState neighborState,
        RandomSource random
    ) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    /**
     * @see SlabBlock#isPathfindable
     */
    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        switch (pathComputationType) {
            case LAND:
                return false;
            case WATER:
                return state.getFluidState().is(FluidTags.WATER);
            case AIR:
                return false;
            default:
                return false;
        }
    }

    /**
     * @return Whether the block state is a full cube.
     */
    public static boolean isFull(BlockState state) {
        return state.getValue(TYPE) == VerticalSlabType.DOUBLE;
    }

}
