package spout.server.paper.impl.packetmapping.block.chunk;

import io.papermc.paper.antixray.ChunkPacketInfo;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import spout.server.paper.impl.moredatadriven.minecraft.BlockStateRegistry;
import java.util.Arrays;

public abstract class DirectSectionContents implements SectionContents {

    DirectSectionContents() {
        super();
    }

    /**
     * @return The block state id for the given block index.
     */
    public abstract int getBlockStateId(int blockIndex);

    /**
     * @return The non-empty block count for this section contents.
     */
    public abstract short getNonEmptyBlockCount();

    /**
     * @return The fluid count for this section contents.
     */
    public abstract short getFluidCount();

    /**
     * @return A valid but minimal bits per entry for these contents.
     */
    public abstract byte getValidMinimalBitsPerEntry(byte globalPaletteBitsPerEntry);

    /**
     * @return The exact serialized size of the paletted container of these contents,
     * in bytes, assuming the given number of bits per entry.
     */
    public abstract int getPalettedContainerSizeInBytes(byte bitsPerEntry);

    public static final boolean[] IS_NON_EMPTY_BLOCK_STATE;
    public static final boolean[] IS_FLUID_STATE;

    static {
        IS_NON_EMPTY_BLOCK_STATE = new boolean[BlockStateRegistry.get().size()];
        IS_FLUID_STATE = new boolean[BlockStateRegistry.get().size()];
        BlockStateRegistry.get().forEach(blockState -> {
            if (!blockState.isAir()) {
                int index = blockState.indexInBlockStateRegistry;
                IS_NON_EMPTY_BLOCK_STATE[index] = true;
                FluidState fluid = blockState.getFluidState();
                if (!fluid.isEmpty()) {
                    IS_FLUID_STATE[index] = true;
                }
            }
        });
    }

    protected void writeAsSingleValuedPalettedContainer(ChunkPacketBlockMapperWriter writer) {
        writer.writeByte((byte) 0);
        writer.writeVarInt(this.getBlockStateId(0));
    }

    protected abstract void writeAsPalettedContainerInternal(ChunkPacketBlockMapperWriter writer, byte bitsPerEntry);

    public void writeAsPalettedContainer(ChunkPacketBlockMapperWriter writer, int section, ChunkPacketInfo<BlockState> chunkPacketInfo, ChunkPacketBlockMapperReader reader, byte globalPaletteBitsPerEntry) {
        // Determine the bits per entry
        byte bitsPerEntry = this.getValidMinimalBitsPerEntry(globalPaletteBitsPerEntry);
        // Increase write buffer capacity if needed
        int sizeInBytes = this.getPalettedContainerSizeInBytes(bitsPerEntry);
        int chunkSectionsCount = chunkPacketInfo.palettes.length;
        int bytesToReadBeforeNextCheck = chunkPacketInfo.sectionEndIndexes[section] - reader.getIndex() + (section == chunkSectionsCount - 1 ? 0 : 2); // Biomes paletted container + non-empty block count of the next section
        writer.increaseCapacityIfNeeded(sizeInBytes + bytesToReadBeforeNextCheck, bytesToReadBeforeNextCheck); // This section contents + same as bytes to read
        // Write to the buffer
        this.writeAsPalettedContainerInternal(writer, bitsPerEntry);
    }

}
