package spout.client.fabric.moredatadriven.minecraft.type.mixin;

import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import spout.common.moredatadriven.minecraft.common.subtypes.FeatureFlagRegistryNamesAccessor;
import java.util.Map;

@Mixin(FeatureFlagRegistry.class)
public interface FeatureFlagRegistryNamesAccessorFeatureFlagRegistryMixin extends FeatureFlagRegistryNamesAccessor {

    @Accessor("names")
    @Override
    Map<Identifier, FeatureFlag> spout$names();

}
