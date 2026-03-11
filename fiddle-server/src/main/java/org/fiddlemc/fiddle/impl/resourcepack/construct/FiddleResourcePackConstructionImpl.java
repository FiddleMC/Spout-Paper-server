package org.fiddlemc.fiddle.impl.resourcepack.construct;

import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstruction;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;

/**
 * The implementation for {@link FiddleResourcePackConstruction}.
 */
public final class FiddleResourcePackConstructionImpl extends ComposableImpl<FiddleResourcePackConstructEvent, FiddleResourcePackConstructEventImpl> implements FiddleResourcePackConstruction {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<FiddleResourcePackConstruction, FiddleResourcePackConstructionImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(FiddleResourcePackConstructionImpl.class);
        }

    }

    public static FiddleResourcePackConstructionImpl get() {
        return (FiddleResourcePackConstructionImpl) FiddleResourcePackConstruction.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "fiddle_resource_pack_construction";
    }

    private FiddleResourcePackConstructionImpl() {
    }

    @Override
    protected FiddleResourcePackConstructEventImpl createComposeEvent() {
        return new FiddleResourcePackConstructEventImpl();
    }

    @Override
    protected void copyInformationFromEvent(final FiddleResourcePackConstructEventImpl event) {
        byte[] bytes;
        try {
            bytes = event.buildZip();
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while constructing the server resource pack", e);
        }
        // TODO serve to players
    }

}
