package org.fiddlemc.fiddle.api.resourcepack.construct;

import org.fiddlemc.fiddle.api.util.composable.Composable;
import org.fiddlemc.fiddle.impl.java.serviceloader.GenericServiceProvider;
import java.util.ServiceLoader;

/**
 * A service to construct the Fiddle server resource pack.
 */
public interface FiddleResourcePackConstruction extends Composable<FiddleResourcePackConstructEvent> {

    /**
     * An internal interface to get the {@link FiddleResourcePackConstruction} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<FiddleResourcePackConstruction> {
    }

    /**
     * @return The {@link FiddleResourcePackConstruction} instance.
     */
    static FiddleResourcePackConstruction get() {
        return ServiceLoader.load(FiddleResourcePackConstruction.ServiceProvider.class, FiddleResourcePackConstruction.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

}
