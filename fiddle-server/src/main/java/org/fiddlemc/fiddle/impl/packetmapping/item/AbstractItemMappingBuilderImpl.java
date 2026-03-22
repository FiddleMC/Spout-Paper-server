package org.fiddlemc.fiddle.impl.packetmapping.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.jspecify.annotations.Nullable;

/**
 * A common base for {@link ItemMappingBuilderImpl} and {@link ItemMappingBuilderNMSImpl}.
 */
public abstract class AbstractItemMappingBuilderImpl<T, H> {

    protected @Nullable ArrayList<ClientView.AwarenessLevel> awarenessLevels;
    protected @Nullable ArrayList<T> from;
    protected @Nullable T to;
    protected @Nullable Consumer<H> function;

    public void awarenessLevel(Collection<ClientView.AwarenessLevel> awarenessLevels) {
        this.awarenessLevels = new ArrayList<>(awarenessLevels);
    }

    public void addAwarenessLevel(ClientView.AwarenessLevel awarenessLevel) {
        if (this.awarenessLevels == null) {
            this.awarenessLevels = new ArrayList<>(1);
        }
        this.awarenessLevels.add(awarenessLevel);
    }

    public void from(Collection<? extends T> from) {
        this.from = new ArrayList<>(from);
    }

    public void addFrom(T from) {
        if (this.from == null) {
            this.from = new ArrayList<>(1);
        }
        this.from.add(from);
    }

    public void to(T to) {
        this.to = to;
    }

    public void to(Consumer<H> function) {
        this.function = function;
    }

    abstract protected Collection<ItemType> getItemsToRegisterFor();

    abstract protected ItemMappingsStep createFunctionStep();

    abstract protected Item getSimpleTo();

    public void registerWith(ItemMappingsComposeEventImpl event) {
        List<ClientView.AwarenessLevel> awarenessLevels = this.awarenessLevels != null ? this.awarenessLevels : Arrays.asList(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideItems());
        if (this.from == null) {
            throw new IllegalStateException("No from was specified");
        }
        if (this.function != null) {
            event.register(awarenessLevels, this.getItemsToRegisterFor(), this.createFunctionStep());
            return;
        }
        if (this.to != null) {
            event.register(awarenessLevels, this.getItemsToRegisterFor(), new SimpleItemMappingsStep(this.getSimpleTo()));
            return;
        }
        throw new IllegalStateException("No to or function was specified");
    }

}
