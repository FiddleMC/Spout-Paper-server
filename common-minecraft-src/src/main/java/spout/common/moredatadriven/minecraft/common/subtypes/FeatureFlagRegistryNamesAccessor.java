package spout.common.moredatadriven.minecraft.common.subtypes;

import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagRegistry;
import java.util.Map;

/**
 * An accessor interface for {@link FeatureFlagRegistry#names}.
 */
public interface FeatureFlagRegistryNamesAccessor {

    Map<Identifier, FeatureFlag> spout$names();

}
