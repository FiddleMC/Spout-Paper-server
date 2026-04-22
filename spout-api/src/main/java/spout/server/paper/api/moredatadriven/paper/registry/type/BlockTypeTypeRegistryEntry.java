package spout.server.paper.api.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

/**
 * A data-centric version-specific registry entry for the {@link BlockTypeType} type.
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface BlockTypeTypeRegistryEntry {

    Object getWrappedCodec();

    /**
     * A mutable builder for the {@link BlockTypeTypeRegistryEntry},
     * that plugins may change in applicable registry events.
     *
     * <p>
     * Currently, this must be cast to {@code BlockTypeTypeRegistryEntryNMS.Builder} to be used.
     * </p>
     */
    @ApiStatus.Experimental
    @ApiStatus.NonExtendable
    interface Builder extends BlockTypeTypeRegistryEntry, RegistryBuilder<BlockTypeType> {
    }

}
