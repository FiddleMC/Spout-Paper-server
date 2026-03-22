package org.fiddlemc.fiddle.impl.content.block;

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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;

/**
 * A 1/8th cube.
 */
public class BevelBlock extends Block implements SimpleWaterloggedBlock {

    public static final MapCodec<BevelBlock> CODEC = simpleCodec(BevelBlock::new);
    public static final BooleanProperty WEST_DOWN_NORTH = FiddleBlockStateProperties.WEST_DOWN_NORTH;
    public static final BooleanProperty WEST_DOWN_SOUTH = FiddleBlockStateProperties.WEST_DOWN_SOUTH;
    public static final BooleanProperty WEST_UP_NORTH = FiddleBlockStateProperties.WEST_UP_NORTH;
    public static final BooleanProperty WEST_UP_SOUTH = FiddleBlockStateProperties.WEST_UP_SOUTH;
    public static final BooleanProperty EAST_DOWN_NORTH = FiddleBlockStateProperties.EAST_DOWN_NORTH;
    public static final BooleanProperty EAST_DOWN_SOUTH = FiddleBlockStateProperties.EAST_DOWN_SOUTH;
    public static final BooleanProperty EAST_UP_NORTH = FiddleBlockStateProperties.EAST_UP_NORTH;
    public static final BooleanProperty EAST_UP_SOUTH = FiddleBlockStateProperties.EAST_UP_SOUTH;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /**
     * An array of {@link VoxelShape} that can be efficiently queried by a bitset index,
     * with the bits indicating:
     * <ul>
     *     <li>1 ({@code 1 << 0}): {@link #WEST_DOWN_NORTH}</li>
     *     <li>2 ({@code 1 << 1}): {@link #WEST_DOWN_SOUTH}</li>
     *     <li>4 ({@code 1 << 2}): {@link #WEST_UP_NORTH}</li>
     *     <li>8 ({@code 1 << 3}): {@link #WEST_UP_SOUTH}</li>
     *     <li>16 ({@code 1 << 4}): {@link #EAST_DOWN_NORTH}</li>
     *     <li>32 ({@code 1 << 5}): {@link #EAST_DOWN_SOUTH}</li>
     *     <li>64 ({@code 1 << 6}): {@link #EAST_UP_NORTH}</li>
     *     <li>128 ({@code 1 << 7}): {@link #EAST_UP_SOUTH}</li>
     * </ul>
     */
    private static final VoxelShape[] SHAPES;

    private static boolean areAllEmpty(boolean[][][] filled) {
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    if (filled[x][y][z]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean areAllFilled(boolean[][][] filled, int[] minSidePerDimension, int[] maxSidePerDimension) {
        for (int x = minSidePerDimension[0]; x <= maxSidePerDimension[0]; x++) {
            for (int y = minSidePerDimension[1]; y <= maxSidePerDimension[1]; y++) {
                for (int z = minSidePerDimension[2]; z <= maxSidePerDimension[2]; z++) {
                    if (!filled[x][y][z]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void zeroOut(boolean[][][] filled, int[] minSidePerDimension, int[] maxSidePerDimension) {
        for (int x = minSidePerDimension[0]; x <= maxSidePerDimension[0]; x++) {
            for (int y = minSidePerDimension[1]; y <= maxSidePerDimension[1]; y++) {
                for (int z = minSidePerDimension[2]; z <= maxSidePerDimension[2]; z++) {
                    filled[x][y][z] = false;
                }
            }
        }
    }

    private static @Nullable VoxelShape getVoxelShapeIfNotEmpty(int arrayI) {
        // For an empty block
        if (arrayI == 0) {
            return null;
        }
        // For a full block
        if (arrayI == (1 << 8) - 1) {
            return Block.box(0, 0, 0, 16, 16, 16);
        }
        // For a non-uniform block, construct a convenient 3D array of filled corners
        boolean[][][] filled = new boolean[2][2][2];
        filled[0][0][0] = (arrayI & (1 << 0)) != 0;
        filled[0][0][1] = (arrayI & (1 << 1)) != 0;
        filled[0][1][0] = (arrayI & (1 << 2)) != 0;
        filled[0][1][1] = (arrayI & (1 << 3)) != 0;
        filled[1][0][0] = (arrayI & (1 << 4)) != 0;
        filled[1][0][1] = (arrayI & (1 << 5)) != 0;
        filled[1][1][0] = (arrayI & (1 << 6)) != 0;
        filled[1][1][1] = (arrayI & (1 << 7)) != 0;
        return getDetailedVoxelShapeIfNotEmpty(filled, false);
    }

    private static @Nullable VoxelShape getDetailedVoxelShapeIfNotEmpty(boolean[][][] filled, boolean skipHalf) {
        // For an empty block
        if (areAllEmpty(filled)) {
            return null;
        }
        // Check if there is any full half block, then union it with the other half
        if (!skipHalf) {
            int[] minSidePerDimension = new int[3];
            int[] maxSidePerDimension = new int[3];
            Arrays.fill(maxSidePerDimension, 1);
            for (int dimension = 0; dimension < 3; dimension++) {
                for (int side = 0; side <= 1; side++) {
                    minSidePerDimension[dimension] = side;
                    maxSidePerDimension[dimension] = side;
                    if (areAllFilled(filled, minSidePerDimension, maxSidePerDimension)) {
                        // Zero out the used states
                        zeroOut(filled, minSidePerDimension, maxSidePerDimension);
                        VoxelShape fullPartShape = Block.box(minSidePerDimension[0] * 8, minSidePerDimension[1] * 8, minSidePerDimension[2] * 8, (maxSidePerDimension[0] + 1) * 8, (maxSidePerDimension[1] + 1) * 8, (maxSidePerDimension[2] + 1) * 8);
                        @Nullable VoxelShape otherShape = getDetailedVoxelShapeIfNotEmpty(filled, true);
                        return otherShape != null ? Shapes.or(fullPartShape, otherShape) : fullPartShape;
                    }
                }
                minSidePerDimension[dimension] = 0;
                maxSidePerDimension[dimension] = 1;
            }
        }
        // Check if there is any full smaller cuboid, then union it with the rest
        int[] minSidePerDimension = new int[3];
        int[] maxSidePerDimension = new int[3];
        Arrays.fill(maxSidePerDimension, 1);
        for (int freeDimension = 0; freeDimension < 3; freeDimension++) {
            int dimension1 = (freeDimension + 1) % 3;
            int dimension2 = (freeDimension + 2) % 3;
            for (int side1 = 0; side1 <= 1; side1++) {
                minSidePerDimension[dimension1] = side1;
                maxSidePerDimension[dimension1] = side1;
                for (int side2 = 0; side2 <= 1; side2++) {
                    minSidePerDimension[dimension2] = side2;
                    maxSidePerDimension[dimension2] = side2;
                    if (areAllFilled(filled, minSidePerDimension, maxSidePerDimension)) {
                        // Zero out the used states
                        zeroOut(filled, minSidePerDimension, maxSidePerDimension);
                        VoxelShape fullPartShape = Block.box(minSidePerDimension[0] * 8, minSidePerDimension[1] * 8, minSidePerDimension[2] * 8, (maxSidePerDimension[0] + 1) * 8, (maxSidePerDimension[1] + 1) * 8, (maxSidePerDimension[2] + 1) * 8);
                        @Nullable VoxelShape otherShape = getDetailedVoxelShapeIfNotEmpty(filled, true);
                        return otherShape != null ? Shapes.or(fullPartShape, otherShape) : fullPartShape;
                    }
                    minSidePerDimension[dimension2] = 0;
                    maxSidePerDimension[dimension2] = 1;
                }
                minSidePerDimension[dimension1] = 0;
                maxSidePerDimension[dimension1] = 1;
            }
        }
        // Check for any corner, then union it with the rest
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    if (filled[x][y][z]) {
                        filled[x][y][z] = false;
                        VoxelShape fullPartShape = Block.box(x * 8, y * 8, z * 8, (x + 1) * 8, (y + 1) * 8, (z + 1) * 8);
                        @Nullable VoxelShape otherShape = getDetailedVoxelShapeIfNotEmpty(filled, true);
                        return otherShape != null ? Shapes.or(fullPartShape, otherShape) : fullPartShape;
                    }
                }
            }
        }
        // Can't end up here: empty state was checked at the start
        throw new IllegalStateException();
    }

    private static VoxelShape getVoxelShape(int arrayI) {
        @Nullable VoxelShape shape = getVoxelShapeIfNotEmpty(arrayI);
        return shape != null ? shape : Shapes.empty();
    }

    static {
        SHAPES = new VoxelShape[1 << 8];
        for (int arrayI = 0; arrayI < SHAPES.length; arrayI++) {
            SHAPES[arrayI] = getVoxelShape(arrayI);
        }
    }

    public BevelBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(WEST_DOWN_NORTH, true)
                .setValue(WEST_DOWN_SOUTH, false)
                .setValue(WEST_UP_NORTH, false)
                .setValue(WEST_UP_SOUTH, false)
                .setValue(EAST_DOWN_NORTH, false)
                .setValue(EAST_DOWN_SOUTH, false)
                .setValue(EAST_UP_NORTH, false)
                .setValue(EAST_UP_SOUTH, false)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    public MapCodec<? extends BevelBlock> codec() {
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
            WEST_DOWN_NORTH,
            WEST_DOWN_SOUTH,
            WEST_UP_NORTH,
            WEST_UP_SOUTH,
            EAST_DOWN_NORTH,
            EAST_DOWN_SOUTH,
            EAST_UP_NORTH,
            EAST_UP_SOUTH,
            WATERLOGGED
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[(state.getValue(WEST_DOWN_NORTH) ? (1 << 0) : 0) +
            (state.getValue(WEST_DOWN_SOUTH) ? (1 << 1) : 0) +
            (state.getValue(WEST_UP_NORTH) ? (1 << 2) : 0) +
            (state.getValue(WEST_UP_SOUTH) ? (1 << 3) : 0) +
            (state.getValue(EAST_DOWN_NORTH) ? (1 << 4) : 0) +
            (state.getValue(EAST_DOWN_SOUTH) ? (1 << 5) : 0) +
            (state.getValue(EAST_UP_NORTH) ? (1 << 6) : 0) +
            (state.getValue(EAST_UP_SOUTH) ? (1 << 7) : 0)];
    }

    /**
     * @see SlabBlock#getStateForPlacement
     */
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState existing = level.getBlockState(pos);

        // Determine which sub-cube (0 or 1 per axis) is targeted
        double hitX = context.getClickLocation().x - pos.getX();
        double hitY = context.getClickLocation().y - pos.getY();
        double hitZ = context.getClickLocation().z - pos.getZ();

        int x = hitX < 0.5 ? 0 : 1;
        int y = hitY < 0.5 ? 0 : 1;
        int z = hitZ < 0.5 ? 0 : 1;

        BooleanProperty targetProp =
            (x == 0 && y == 0 && z == 0) ? WEST_DOWN_NORTH :
                (x == 0 && y == 0 && z == 1) ? WEST_DOWN_SOUTH :
                    (x == 0 && y == 1 && z == 0) ? WEST_UP_NORTH :
                        (x == 0 && y == 1 && z == 1) ? WEST_UP_SOUTH :
                            (x == 1 && y == 0 && z == 0) ? EAST_DOWN_NORTH :
                                (x == 1 && y == 0 && z == 1) ? EAST_DOWN_SOUTH :
                                    (x == 1 && y == 1 && z == 0) ? EAST_UP_NORTH :
                                        EAST_UP_SOUTH;

        // If placing into an existing bevel block, try to add to it
        if (existing.is(this)) {
            if (!existing.getValue(targetProp)) {
                return existing.setValue(targetProp, true);
            }
            return null;
        }

        // Otherwise create new state with only that sub-cube filled
        FluidState fluid = level.getFluidState(pos);

        return this.defaultBlockState()
            .setValue(WEST_DOWN_NORTH, false)
            .setValue(WEST_DOWN_SOUTH, false)
            .setValue(WEST_UP_NORTH, false)
            .setValue(WEST_UP_SOUTH, false)
            .setValue(EAST_DOWN_NORTH, false)
            .setValue(EAST_DOWN_SOUTH, false)
            .setValue(EAST_UP_NORTH, false)
            .setValue(EAST_UP_SOUTH, false)
            .setValue(targetProp, true)
            .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    /**
     * @see SlabBlock#canBeReplaced(BlockState, BlockPlaceContext)
     */
    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        // Determine which sub-cube is targeted
        BlockPos pos = useContext.getClickedPos();

        double hitX = useContext.getClickLocation().x - pos.getX();
        double hitY = useContext.getClickLocation().y - pos.getY();
        double hitZ = useContext.getClickLocation().z - pos.getZ();

        int x = hitX < 0.5 ? 0 : 1;
        int y = hitY < 0.5 ? 0 : 1;
        int z = hitZ < 0.5 ? 0 : 1;

        BooleanProperty targetProp =
            (x == 0 && y == 0 && z == 0) ? WEST_DOWN_NORTH :
                (x == 0 && y == 0 && z == 1) ? WEST_DOWN_SOUTH :
                    (x == 0 && y == 1 && z == 0) ? WEST_UP_NORTH :
                        (x == 0 && y == 1 && z == 1) ? WEST_UP_SOUTH :
                            (x == 1 && y == 0 && z == 0) ? EAST_DOWN_NORTH :
                                (x == 1 && y == 0 && z == 1) ? EAST_DOWN_SOUTH :
                                    (x == 1 && y == 1 && z == 0) ? EAST_UP_NORTH :
                                        EAST_UP_SOUTH;

        // Can replace if that specific sub-cube is still empty
        return !state.getValue(targetProp);
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
     * @return Whether every 1/8th cube of the given block state is filled.
     */
    public static boolean isFull(BlockState state) {
        return state.getValue(WEST_DOWN_NORTH) &&
            state.getValue(WEST_DOWN_SOUTH) &&
            state.getValue(WEST_UP_NORTH) &&
            state.getValue(WEST_UP_SOUTH) &&
            state.getValue(EAST_DOWN_NORTH) &&
            state.getValue(EAST_DOWN_SOUTH) &&
            state.getValue(EAST_UP_NORTH) &&
            state.getValue(EAST_UP_SOUTH);
    }

}
