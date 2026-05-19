package spout.common.moredatadriven.clientmodprotocol;

import it.unimi.dsi.fastutil.Pair;
import org.jspecify.annotations.Nullable;
import spout.client.fabric.clientview.ClientModState;
import spout.client.fabric.clientview.SpoutProtocol;
import spout.client.fabric.moredatadriven.TemporaryRegistryModifiers;
import spout.client.fabric.moredatadriven.minecraft.type.WithItemProperties;
import spout.client.fabric.moredatadriven.minecraft.type.mixin.BlockBehaviourPropertiesAccessor;

/**
 * Collects all custom content received.
 */
public final class ClientModCustomContentReceiving {

    private ClientModCustomContentReceiving() {
        throw new UnsupportedOperationException();
    }

    /**
     * The current received content, or null if not currently receiving.
     */
    private static volatile @Nullable ClientModCustomContent receivedContent;

    public static void handlePacket(ClientModCustomContentPacketPayload payload) {
        while (SpoutProtocol.getState() != ClientModState.RECEIVED_CUSTOM_CONTENT && !SpoutProtocol.tryChangeState(ClientModState.CLIENT_MOD_DETECTED, ClientModState.RECEIVED_CUSTOM_CONTENT)) {
            Thread.onSpinWait();
        }
        if (receivedContent == null) {
            receivedContent = new ClientModCustomContent();
        }
        for (ClientModCustomContentPacketPayload.Element element : payload.elements) {
            switch (element.type) {
                case BLOCK -> receivedContent.blockJSONs.add(element.getContentAsJsonElement());
                case ITEM -> receivedContent.itemJSONs.add(element.getContentAsJsonElement());
                case END -> {
                    // Add the received content
                    TemporaryRegistryModifiers.prepareToAddCustomContent();
                    TemporaryRegistryModifiers.addCustomContent(
                        () -> receivedContent.getParsedBlocks().stream().map(block -> Pair.of(((BlockBehaviourPropertiesAccessor) block.properties()).getId(), block)).toList(),
                        () -> receivedContent.getParsedItems().stream().map(item -> Pair.of((((WithItemProperties) item).getItemProperties()).id, item)).toList()
                    );
                    SpoutProtocol.changeState(ClientModState.RECEIVED_CUSTOM_CONTENT, ClientModState.ADDED_CUSTOM_CONTENT);
                    receivedContent = null;
                }
            }
        }
    }

}
