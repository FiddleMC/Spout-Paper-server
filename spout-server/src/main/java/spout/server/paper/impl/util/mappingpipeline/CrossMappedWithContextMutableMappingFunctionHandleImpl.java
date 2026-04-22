package spout.server.paper.impl.util.mappingpipeline;

import spout.server.paper.api.util.mapping.MappingFunctionContext;
import spout.server.paper.api.util.mapping.MutableMappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithContextMappingFunctionHandle;

/**
 * A {@link CrossMappedMutableMappingFunctionHandleImpl}
 * that also implements {@link WithContextMappingFunctionHandle}.
 */
public abstract class CrossMappedWithContextMutableMappingFunctionHandleImpl<T, MT extends T, C extends MappingFunctionContext, IT, IMT extends IT, IH extends WithContextMappingFunctionHandle<IT, C> & MutableMappingFunctionHandle<IT, IMT>> extends CrossMappedMutableMappingFunctionHandleImpl<T, MT, IT, IMT, IH> implements WithContextMappingFunctionHandle<T, C> {

    public CrossMappedWithContextMutableMappingFunctionHandleImpl(IH internal) {
        super(internal);
    }

    @Override
    public C getContext() {
        return this.internal.getContext();
    }

}
