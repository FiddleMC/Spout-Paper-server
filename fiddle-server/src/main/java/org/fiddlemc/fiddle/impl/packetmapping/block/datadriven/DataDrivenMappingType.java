package org.fiddlemc.fiddle.impl.packetmapping.block.datadriven;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A type of data-driven mapping.
 */
public interface DataDrivenMappingType {

    <T> void apply(BlockMappingsComposeEventImpl event, Block block, DynamicOps<T> dynamicOps, MapLike<T> mapLike);

}
