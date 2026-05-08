package spout.server.paper.impl.packetmapping.block.automatic;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import spout.server.paper.api.resourcepack.content.Blockstates;
import spout.server.paper.impl.packetmapping.block.claim.VisualDuplicatesImpl;
import spout.server.paper.impl.resourcepack.plugin.discover.PluginResourcePackDiscoveryImpl;

/**
 * A utility class that can return whether a {@link Block}
 * has at least 2 visually distinct block states.
 */
public final class BlocksWithVisuallyDifferentBlockstates {

    private BlocksWithVisuallyDifferentBlockstates() {
        throw new UnsupportedOperationException();
    }

    private static final Object2BooleanMap<Block> cached = new Object2BooleanOpenHashMap<>();

    /**
     * @return True if and only if the given {@link Block}
     * has at least 2 visually distinct block states.
     */
    public static boolean check(Block block) {
        if (block.getStateDefinition().getPossibleStates().size() <= 1) {
            return false;
        }
        return cached.computeIfAbsent(block, $ -> {
            if (block.getStateDefinition().getProperties().contains(BlockStateProperties.WATERLOGGED)) {
                // Client will have or not have water particles based on waterlogged property
                return true;
            }
            if (block.isVanilla()) {
                VisualDuplicatesImpl.VisualDuplicateGroupImpl visualDuplicateGroup = VisualDuplicatesImpl.get().getVisualDuplicates(block.defaultBlockState());
                if (visualDuplicateGroup == null) {
                    return true;
                }
                for (BlockState possibleState : block.getStateDefinition().getPossibleStates()) {
                    if (!visualDuplicateGroup.contains(possibleState.indexInVanillaOnlyBlockStateRegistry)) {
                        return true;
                    }
                }
                return false;
            }
            Blockstates blockstates = PluginResourcePackDiscoveryImpl.get().getResourcePackBlockstates(block.keyInBlockRegistry);
            if (blockstates.hasMultipart()) {
                return true;
            }
            return blockstates.getVariants().values().stream().distinct().count() > 1;
        });
    }

}
