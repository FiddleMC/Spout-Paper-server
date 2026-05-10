package spout.common.moredatadriven.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import spout.server.paper.impl.moredatadriven.minecraft.BlockRegistry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides alternative validation for the {@link BlockEntityType#isValid}.
 */
public final class BlockEntityAlternativeValidation {

    private BlockEntityAlternativeValidation() {
        throw new UnsupportedOperationException();
    }

    private static boolean initialized = false;
    private static boolean skipValidation = false;

    private static final Map<BlockEntityType<?>, Set<Block>> alternativelyValid = new HashMap<>(1);

    public static boolean isAlternativelyValid(BlockEntityType<?> blockEntityType, BlockState state) {
        if (skipValidation) return true;
        if (!initialized) {
            initialize();
        }
        Set<Block> blocks = alternativelyValid.get(blockEntityType);
        return blocks != null && blocks.contains(state.getBlock());
    }

    public static Collection<Block> getAlternativelyValidBlocks(BlockEntityType<?> blockEntityType) {
        if (!initialized) {
            initialize();
        }
        return alternativelyValid.getOrDefault(blockEntityType, Collections.emptySet());
    }

    public static void clear() {
        alternativelyValid.clear();
        initialized = false;
    }

    public static void initialize() {
        clear();
        BlockRegistry.get().forEach(block -> {
            if (block instanceof EntityBlock entityBlock) {
                skipValidation = true;
                BlockEntity entity = entityBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
                skipValidation = false;
                if (entity != null) {
                    alternativelyValid.computeIfAbsent(entity.getType(), $ -> new HashSet<>(1)).add(block);
                }
            }
        });
        initialized = true;
    }

}
