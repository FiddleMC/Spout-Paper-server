package spout.server.paper.impl.bukkit.enuminjection;

import spout.server.paper.api.bukkit.enuminjection.BukkitEnumNamePickFunctionHandle;
import spout.server.paper.impl.util.mappingpipeline.SimpleMappingFunctionHandleImpl;

/**
 * The implementation of {@link BukkitEnumNamePickFunctionHandle}.
 */
public final class BukkitEnumNameMappingHandleImpl<S> extends SimpleMappingFunctionHandleImpl<String, String> implements BukkitEnumNamePickFunctionHandle<S> {

    private final S sourceValue;

    public BukkitEnumNameMappingHandleImpl(String data, S sourceValue) {
        super(data, false);
        this.sourceValue = sourceValue;
    }

    @Override
    public S getSourceValue() {
        return this.sourceValue;
    }

}
