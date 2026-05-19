package spout.common.moredatadriven.minecraft.blocktype;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import spout.common.branding.SpoutNamespace;
import spout.common.builtincontent.block.HalfTransparentSlabBlock;
import spout.common.builtincontent.block.HalfTransparentStairBlock;
import spout.common.builtincontent.block.QuadBlock;
import spout.common.builtincontent.block.TransparentSlabBlock;
import spout.common.builtincontent.block.TransparentStairBlock;
import spout.common.builtincontent.block.VerticalSlabBlock;
import spout.common.moredatadriven.minecraft.BuiltInSpoutMoreDataDrivenRegistries;
import spout.common.moredatadriven.minecraft.SpoutMoreDataDrivenRegistries;

/**
 * Built-in values for the {@link SpoutMoreDataDrivenRegistries#BLOCK_TYPE} registry.
 */
public final class SpoutBlockTypes {

    private SpoutBlockTypes() {
        throw new UnsupportedOperationException();
    }

    // public static final SpoutBlockType HALF_TRANSPARENT_SLAB = register(Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "half_transparent_slab"), HalfTransparentSlabBlock.CODEC);
    // public static final SpoutBlockType HALF_TRANSPARENT_STAIR = register(Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "half_transparent_stair"), HalfTransparentStairBlock.CODEC);
    // public static final SpoutBlockType TRANSPARENT_SLAB = register(Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "transparent_slab"), TransparentSlabBlock.CODEC);
    // public static final SpoutBlockType TRANSPARENT_STAIR = register(Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "transparent_stair"), TransparentStairBlock.CODEC);
    // public static final SpoutBlockType QUAD = register(Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "quad"), QuadBlock.CODEC);
    // public static final SpoutBlockType VERTICAL_SLAB = register(Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "vertical_slab"), VerticalSlabBlock.CODEC);
    //
    // private static <B extends Block> SpoutBlockType register(String id, MapCodec<? extends B> codec) {
    //     return register(Identifier.parse(id), codec);
    // }
    //
    // private static <B extends Block> SpoutBlockType register(Identifier id, MapCodec<? extends B> codec) {
    //     return register(id, new CodecSpoutBlockType(id, codec));
    // }
    //
    // private static SpoutBlockType register(Identifier id, SpoutBlockType blockType) {
    //     return Registry.register(BuiltInSpoutMoreDataDrivenRegistries.BLOCK_TYPE, id, blockType);
    // }

    public static SpoutBlockType bootstrap(Registry<SpoutBlockType> registry) {
        // return VERTICAL_SLAB;
        return null;
    }

}
