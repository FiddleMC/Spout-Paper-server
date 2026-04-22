package spout.server.paper.api.bukkit.enuminjection;

import spout.server.paper.api.util.mapping.MappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A {@link MappingFunctionHandle} for {@link BukkitEnumNames}s.
 */
public interface BukkitEnumNamePickFunctionHandle<S> extends WithOriginalMappingFunctionHandle<String> {

    /**
     * @return The source value for which the enum name is being picked.
     */
    S getSourceValue();

}
