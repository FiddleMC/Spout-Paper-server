package spout.clientview.packetmapping.blockstate;

import net.minecraft.core.BlockPos;
import spout.clientview.model.ClientView;
import spout.clientview.model.FallbackClientViewImpl;
import spout.clientview.packetmapping.WithClientViewMappingsApplicationContext;

/**
 * The context for applying mappings to a block state.
 */
public class BlockStateMappingsApplicationContext extends WithClientViewMappingsApplicationContext {

    public static final BlockStateMappingsApplicationContext FALLBACK = new BlockStateMappingsApplicationContext(FallbackClientViewImpl.INSTANCE);

    private final boolean isStateOfPhysicalBlockInWorld;
    private final int physicalBlockX;
    private final int physicalBlockY;
    private final int physicalBlockZ;

    public BlockStateMappingsApplicationContext(ClientView clientView, boolean isStateOfPhysicalBlockInWorld, int physicalBlockX, int physicalBlockY, int physicalBlockZ) {
        super(clientView);
        this.isStateOfPhysicalBlockInWorld = isStateOfPhysicalBlockInWorld;
        this.physicalBlockX = physicalBlockX;
        this.physicalBlockY = physicalBlockY;
        this.physicalBlockZ = physicalBlockZ;
    }

    public BlockStateMappingsApplicationContext(ClientView clientView) {
        this(clientView, false, 0, 0, 0);
    }

    public BlockStateMappingsApplicationContext(ClientView clientView, int physicalBlockX, int physicalBlockY, int physicalBlockZ) {
        this(clientView, true, physicalBlockX, physicalBlockY, physicalBlockZ);
    }

    public BlockStateMappingsApplicationContext(ClientView clientView, BlockPos physicalBlockPos) {
        this(clientView, physicalBlockPos.getX(), physicalBlockPos.getY(), physicalBlockPos.getZ());
    }

    /**
     * @return Whether the block state on which this mapping is being applied
     * is the block state of a physical block, i.e. a block at certain coordinates in a world.
     *
     * <p>
     * An example of where this method would return false is the block state of a block display entity.
     * </p>
     */
    public boolean isStateOfPhysicalBlockInWorld() {
        return this.isStateOfPhysicalBlockInWorld;
    }

    /**
     * @return The x-coordinate of the physical block for which this mapping is being applied.
     * This is only available if {@link #isStateOfPhysicalBlockInWorld()} is true
     * and if {@link BlockMappingBuilder#to} had {@code requiresCoordinates = true}.
     * Otherwise, the returned value is meaningless.
     */
    public int getPhysicalBlockX() {
        return this.physicalBlockX;
    }

    /**
     * @return The y-coordinate of the physical block for which this mapping is being applied,
     * This is only available if {@link #isStateOfPhysicalBlockInWorld()} is true
     * and if {@link BlockMappingBuilder#to} had {@code requiresCoordinates = true}.
     * Otherwise, the returned value is meaningless.
     */
    public int getPhysicalBlockY() {
        return this.physicalBlockY;
    }

    /**
     * @return The z-coordinate of the physical block for which this mapping is being applied,
     * This is only available if {@link #isStateOfPhysicalBlockInWorld()} is true
     * and if {@link BlockMappingBuilder#to} had {@code requiresCoordinates = true}.
     * Otherwise, the returned value is meaningless.
     */
    public int getPhysicalBlockZ() {
        return this.physicalBlockZ;
    }

}
