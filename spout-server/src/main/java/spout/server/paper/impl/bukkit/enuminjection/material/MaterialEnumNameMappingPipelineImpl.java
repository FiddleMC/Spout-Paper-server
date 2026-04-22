package spout.server.paper.impl.bukkit.enuminjection.material;

import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemType;
import spout.server.paper.api.bukkit.enuminjection.material.MaterialEnumNames;
import spout.server.paper.impl.bukkit.enuminjection.BukkitEnumNameMappingPipelineImpl;
import spout.server.paper.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;

/**
 * The implementation of {@link MaterialEnumNames}.
 */
public final class MaterialEnumNameMappingPipelineImpl extends BukkitEnumNameMappingPipelineImpl<Triple<NamespacedKey, @Nullable BlockType, @Nullable ItemType>> implements MaterialEnumNames {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<MaterialEnumNames, MaterialEnumNameMappingPipelineImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(MaterialEnumNameMappingPipelineImpl.class);
        }

    }

    public static MaterialEnumNameMappingPipelineImpl get() {
        return (MaterialEnumNameMappingPipelineImpl) MaterialEnumNames.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "spout_material_enum_name_mapping_pipeline";
    }

}
