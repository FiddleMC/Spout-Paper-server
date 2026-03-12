package org.fiddlemc.fiddle.impl.util.composable;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventRunner;
import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.PrioritizedLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.PrioritizableLifecycleEventType;
import org.fiddlemc.fiddle.api.util.composable.Composable;
import org.fiddlemc.fiddle.api.util.composable.ComposableEventType;
import org.jspecify.annotations.Nullable;

/**
 * A base implementation of {@link Composable}.
 */
public abstract class ComposableImpl<E extends LifecycleEvent, EI extends PaperLifecycleEvent> implements Composable<E> {

    /**
     * A generic {@link ComposableEventType} implementation.
     */
    private static class ComposableEventTypeImpl extends PrioritizableLifecycleEventType.Simple<BootstrapContext, LifecycleEvent> implements ComposableEventType<LifecycleEvent>, LifecycleEventType<BootstrapContext, LifecycleEvent, PrioritizedLifecycleEventHandlerConfiguration<BootstrapContext>> {

        public ComposableEventTypeImpl(String name) {
            super(name, BootstrapContext.class);
        }

    }

    /**
     * The cached return value of {@link #compose()},
     * or null if not cached yet.
     */
    private @Nullable ComposableEventTypeImpl composeEventType;

    /**
     * The prefix for the {@link LifecycleEventType#name()} of events for this composable.
     */
    protected abstract String getEventTypeNamePrefix();

    /**
     * The {@link LifecycleEventType#name()} of events for this composable.
     */
    protected String getEventTypeName() {
        return this.getEventTypeNamePrefix() + "/compose";
    }

    @Override
    public ComposableEventType<E> compose() {
        if (this.composeEventType == null) {
            this.composeEventType = new ComposableEventTypeImpl(this.getEventTypeName());
        }
        return (ComposableEventType<E>) this.composeEventType;
    }

    /**
     * @return A new compose event.
     */
    protected abstract EI createComposeEvent();

    /**
     * {@linkplain LifecycleEventRunner#callEvent Fires} a compose event for this pipeline.
     */
    public void fireComposeEvent() {
        EI event = this.createComposeEvent();
        LifecycleEventRunner.INSTANCE.callEvent((ComposableEventType<EI>) this.compose(), event);
        this.copyInformationFromEvent(event);
    }

    /**
     * Copies the information registered by handlers of the event to this composable.
     */
    protected void copyInformationFromEvent(EI event) {
        // To be overridden if desired
    }

}
