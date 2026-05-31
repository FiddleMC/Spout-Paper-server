package spout.server.paper.impl.packetmapping.component.translatable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import spout.clientview.model.ClientView;
import spout.server.paper.api.packetmapping.component.translatable.ServerSideTranslations;
import spout.server.paper.impl.packetmapping.component.ComponentMappingHandleNMSImpl;
import spout.server.paper.impl.packetmapping.component.ComponentMappingsImpl;
import spout.server.paper.impl.packetmapping.component.ComponentMappingsStep;

/**
 * A {@link ComponentMappingsStep} to be registered with {@link ComponentMappingsImpl},
 * that applies the {@linkplain ServerSideTranslations registered server-side translations}.
 */
public final class ServerSideTranslationsComponentMappingsStep implements ComponentMappingsStep {

    @Override
    public void apply(ComponentMappingHandleNMSImpl handle) {
        ClientView clientView = (ClientView) handle.getContext().getClientView();
        if (clientView.understandsAllServerSideTranslatables()) return;
        Component immutable = handle.getImmutable();
        ComponentContents contents = immutable.getContents();
        if (contents instanceof TranslatableContents translatableContents) {
            String key = translatableContents.getKey();
            ServerSideTranslations.ServerSideTranslation translation = ServerSideTranslations.get().get(key, clientView.getLocale());
            if (translation != null) {
                if (translation.overrideClientSide()) {
                    handle.setMutable(Component.literal(translation.translation()).withStyle(immutable.getStyle()));
                } else {
                    handle.setMutable(Component.translatableWithFallback(key, translation.translation()).withStyle(immutable.getStyle()));
                }
            }
        }
    }

}
