package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import spout.server.paper.api.packetmapping.block.automatic.ProxyStatesRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A base implementation of {@link ProxyStatesRequestBuilder}.
 */
public abstract class ProxyStatesRequestBuilderImpl implements ProxyStatesRequestBuilder {

    public boolean createFromToUsedStatesMappings;
    public boolean createProxyToVisualDuplicateMappings;
    public boolean createItemMappings;
    public boolean createVanillaMappings;
    public boolean createResourcePackBlockstatesEntries;
    public @Nullable List<Consumer<UsedStates>> resultConsumers;

    public ProxyStatesRequestBuilderImpl() {
        this.createFromToUsedStatesMappings = true;
        this.createProxyToVisualDuplicateMappings = true;
        this.createItemMappings = true;
        this.createVanillaMappings = true;
        this.createResourcePackBlockstatesEntries = true;
        this.resultConsumers = null;
    }

    @Override
    public void createFromToUsedStatesMappings(boolean createFromToUsedStatesMappings) {
        this.createFromToUsedStatesMappings = createFromToUsedStatesMappings;
    }

    @Override
    public boolean createFromToUsedStatesMappings() {
        return this.createFromToUsedStatesMappings;
    }

    @Override
    public void createProxyToVisualDuplicateMappings(boolean createProxyToVisualDuplicateMappings) {
        this.createProxyToVisualDuplicateMappings = createProxyToVisualDuplicateMappings;
    }

    @Override
    public boolean createProxyToVisualDuplicateMappings() {
        return this.createProxyToVisualDuplicateMappings;
    }

    @Override
    public void createItemMappings(boolean createItemMappings) {
        this.createItemMappings = createItemMappings;
    }

    @Override
    public boolean createItemMappings() {
        return this.createItemMappings;
    }

    @Override
    public void createVanillaMappings(boolean createVanillaMappings) {
        this.createVanillaMappings = createVanillaMappings;
    }

    @Override
    public boolean createVanillaMappings() {
        return this.createVanillaMappings;
    }

    @Override
    public void createResourcePackBlockstatesEntries(boolean createResourcePackBlockstatesEntries) {
        this.createResourcePackBlockstatesEntries = createResourcePackBlockstatesEntries;
    }

    @Override
    public boolean createResourcePackBlockstatesEntries() {
        return this.createResourcePackBlockstatesEntries;
    }

    @Override
    public void useResult(Consumer<UsedStates> resultConsumer) {
        if (this.resultConsumers == null) {
            this.resultConsumers = new ArrayList<>(1);
        }
        this.resultConsumers.add(resultConsumer);
    }

    public @Nullable Function<BlockState, @Nullable Item> getFromItemFunction() {
        return null;
    }

    public @Nullable Function<BlockState, @Nullable Item> getToItemFunction() {
        return null;
    }

    public void validateArguments() {
    }

}
