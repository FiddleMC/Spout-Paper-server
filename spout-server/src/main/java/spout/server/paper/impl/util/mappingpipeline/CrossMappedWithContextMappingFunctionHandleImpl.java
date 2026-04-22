package spout.server.paper.impl.util.mappingpipeline;

import spout.server.paper.api.util.mapping.MappingFunctionContext;
import spout.server.paper.api.util.mapping.WithContextMappingFunctionHandle;

/**
 * A {@link CrossMappedMappingFunctionHandleImpl}
 * that also implements {@link WithContextMappingFunctionHandle}.
 */
public abstract class CrossMappedWithContextMappingFunctionHandleImpl<T, C extends MappingFunctionContext, IT, IH extends WithContextMappingFunctionHandle<IT, C>> extends CrossMappedMappingFunctionHandleImpl<T, IT, IH> implements WithContextMappingFunctionHandle<T, C> {

    public CrossMappedWithContextMappingFunctionHandleImpl(IH internal) {
        super(internal);
    }

    @Override
    public C getContext() {
        return this.internal.getContext();
    }

}
