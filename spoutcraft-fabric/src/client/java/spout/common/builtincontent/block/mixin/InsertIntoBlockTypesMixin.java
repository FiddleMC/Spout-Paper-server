package spout.common.builtincontent.block.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spout.common.branding.SpoutNamespace;
import spout.common.builtincontent.block.HalfTransparentSlabBlock;
import spout.common.builtincontent.block.HalfTransparentStairBlock;
import spout.common.builtincontent.block.QuadBlock;
import spout.common.builtincontent.block.TransparentSlabBlock;
import spout.common.builtincontent.block.TransparentStairBlock;
import spout.common.builtincontent.block.VerticalSlabBlock;

@Mixin(BlockTypes.class)
public abstract class InsertIntoBlockTypesMixin {

    @Inject(
        method = "bootstrap",
        at = @At("HEAD")
    )
    private static void onBootstrap(
        Registry<MapCodec<? extends Block>> registry,
        CallbackInfoReturnable<MapCodec<? extends Block>> cir
    ) {
        Registry.register(registry, Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "half_transparent_slab"), HalfTransparentSlabBlock.CODEC);
        Registry.register(registry, Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "half_transparent_stair"), HalfTransparentStairBlock.CODEC);
        Registry.register(registry, Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "transparent_slab"), TransparentSlabBlock.CODEC);
        Registry.register(registry, Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "transparent_stair"), TransparentStairBlock.CODEC);
        Registry.register(registry, Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "quad"), QuadBlock.CODEC);
        Registry.register(registry, Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "vertical_slab"), VerticalSlabBlock.CODEC);
    }

}
