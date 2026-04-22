package spout.server.paper.impl.packetmapping.component;

import java.util.Collection;
import java.util.function.Consumer;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.packetmapping.component.ComponentMappingBuilder;
import spout.server.paper.api.packetmapping.component.ComponentMappingsComposeEvent;
import spout.server.paper.api.packetmapping.component.ComponentTarget;
import spout.server.paper.api.packetmapping.component.nms.ComponentMappingBuilderNMS;
import spout.server.paper.api.packetmapping.component.nms.ComponentMappingsComposeEventNMS;
import spout.server.paper.impl.util.composable.AwarenessLevelPairKeyedBuilderComposeEventImpl;

/**
 * The implementation of {@link ComponentMappingsComposeEvent}.
 */
public final class ComponentMappingsComposeEventImpl extends AwarenessLevelPairKeyedBuilderComposeEventImpl<ComponentTarget, ComponentMappingsStep, ComponentMappingBuilder> implements ComponentMappingsComposeEventNMS<ComponentMappingsStep> {

    @Override
    public void register(Consumer<ComponentMappingBuilder> builderConsumer) {
        ComponentMappingBuilderImpl builder = new ComponentMappingBuilderImpl();
        builderConsumer.accept(builder);
        builder.registerWith(this);
    }

    @Override
    public void registerNMS(Consumer<ComponentMappingBuilderNMS> builderConsumer) {
        ComponentMappingBuilderNMSImpl builder = new ComponentMappingBuilderNMSImpl();
        builderConsumer.accept(builder);
        builder.registerWith(this);
    }

    @Override
    protected int keyPartToInt(ComponentTarget key) {
        return key.ordinal();
    }

    @Override
    protected ComponentTarget intToKeyPart(int internalKey) {
        return ComponentTargetUtil.getByOrdinal(internalKey);
    }

    @Override
    public void register(Collection<ClientView.AwarenessLevel> awarenessLevels, Collection<ComponentTarget> keys, ComponentMappingsStep element) {
        super.register(awarenessLevels, ComponentTargetUtil.expandTargets(keys), element);
    }
}
