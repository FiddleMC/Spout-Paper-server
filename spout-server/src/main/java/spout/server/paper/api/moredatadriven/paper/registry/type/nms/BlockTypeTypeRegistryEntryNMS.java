package spout.server.paper.api.moredatadriven.paper.registry.type.nms;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import spout.server.paper.api.moredatadriven.paper.registry.type.BlockTypeType;
import spout.server.paper.api.moredatadriven.paper.registry.type.BlockTypeTypeRegistryEntry;

/**
 * An extension of {@link BlockTypeTypeRegistryEntry} using Minecraft internals.
 */
public interface BlockTypeTypeRegistryEntryNMS extends BlockTypeTypeRegistryEntry {

    @Override
    WrappedBlockCodec<?> getWrappedCodec();

    /**
     * A {@link BlockTypeTypeRegistryEntry.Builder}
     * that allows building a {@link BlockTypeType} type using Minecraft internals.
     */
    interface Builder extends BlockTypeTypeRegistryEntryNMS, BlockTypeTypeRegistryEntry.Builder {

        void setCodec(MapCodec<? extends Block> codecForType);

    }

}
