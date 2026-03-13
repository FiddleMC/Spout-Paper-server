package org.fiddlemc.fiddle.impl.resourcepack.construct;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventRunner;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.PrioritizedLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.PrioritizableLifecycleEventType;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructFinishEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstruction;
import org.fiddlemc.fiddle.impl.configuration.FiddleGlobalConfiguration;
import org.fiddlemc.fiddle.impl.resourcepack.send.FiddleResourcePackSending;
import org.fiddlemc.fiddle.impl.resourcepack.serve.FiddleResourcePackServing;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.util.EnumMap;
import java.util.Map;

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
        // Create the event
        FiddleResourcePackConstructEventImpl event = new FiddleResourcePackConstructEventImpl();
        // Initialize built-in resources
        for (ClientView.AwarenessLevel awarenessLevel : ClientView.AwarenessLevel.getAll()) {
            // Skip if the awareness level is not relevant
            if (!generateForAwarenessLevel(awarenessLevel)) continue;
            // Add pack.mcmeta
            event.path(awarenessLevel, "pack.mcmeta").asJsonObject().setParsedFromString("""
                {
                  "pack": {
                    "min_format": 75,
                    "max_format": 75,
                    "description": "Fiddle server resource pack"
                  }
                }
                """);
        }
        // Return the event
        return event;
    }

    @Override
    protected void copyInformationFromEvent(final FiddleResourcePackConstructEventImpl event) {
        // Build the pack contents
        Map<ClientView.AwarenessLevel, byte[]> packBytes;
        try {
            packBytes = event.buildPacks();
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while constructing the server resource pack", e);
        }
        // Create pack instances
        Map<ClientView.AwarenessLevel, FiddleConstructedResourcePackImpl> packs = new EnumMap<>(ClientView.AwarenessLevel.class);
        for (Map.Entry<ClientView.AwarenessLevel, byte[]> entry : packBytes.entrySet()) {
            packs.put(entry.getKey(), new FiddleConstructedResourcePackImpl(entry.getKey(), entry.getValue()));
        }
        // Call plugins
        LifecycleEventRunner.INSTANCE.callEvent(this.finish(), new FiddleResourcePackConstructFinishEventImpl(packs));
        // Get the packs to pass to built-in output use cases
        FiddleConstructedResourcePackImpl vanillaPack = packs.get(ClientView.AwarenessLevel.RESOURCE_PACK);
        FiddleConstructedResourcePackImpl clientModPack = packs.get(ClientView.AwarenessLevel.CLIENT_MOD);
        // TODO save to file if enabled
        // Set up the HTTP serving
        if (FiddleResourcePackServing.isEnabled()) {
            FiddleResourcePackServing.start(vanillaPack, clientModPack);
        }
        // Initialize the packet sending
        FiddleResourcePackSending.initialize(vanillaPack, clientModPack);
    }

    /**
     * Whether constructing the resource pack is enabled.
     */
    public boolean isEnabled() {
        return FiddleGlobalConfiguration.get().generatedResourcePack.output.serveOverHttp.enabled;
    }

    private static class FiddleResourcePackConstructFinishEventType extends PrioritizableLifecycleEventType.Simple<BootstrapContext, FiddleResourcePackConstructFinishEvent> {

        public FiddleResourcePackConstructFinishEventType() {
            super("fiddle_resource_pack_construction_finish", BootstrapContext.class);
        }

    }

    /**
     * The cached return value of {@link #finish()},
     * or null if not cached yet.
     */
    private @Nullable FiddleResourcePackConstructFinishEventType finishEventType;

    @Override
    public LifecycleEventType<BootstrapContext, FiddleResourcePackConstructFinishEvent, PrioritizedLifecycleEventHandlerConfiguration<BootstrapContext>> finish() {
        if (this.finishEventType == null) {
            this.finishEventType = new FiddleResourcePackConstructFinishEventType();
        }
        return this.finishEventType;
    }

    public static boolean generateForAwarenessLevel(ClientView.AwarenessLevel awarenessLevel) {
        return awarenessLevel != ClientView.AwarenessLevel.VANILLA;
    }

}
