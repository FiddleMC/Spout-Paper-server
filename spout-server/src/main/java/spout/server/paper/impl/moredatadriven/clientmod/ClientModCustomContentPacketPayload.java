package spout.server.paper.impl.moredatadriven.clientmod;

import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import spout.common.branding.SpoutNamespace;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.impl.clientview.JavaWithClientModClientViewImpl;
import spout.server.paper.impl.moredatadriven.minecraft.BlockRegistry;
import spout.server.paper.impl.moredatadriven.minecraft.ItemRegistry;

public final class ClientModCustomContentPacketPayload implements CustomPacketPayload {

    private static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "custom_content");
    private static final CustomPacketPayload.Type<ClientModCustomContentPacketPayload> TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
    private static final StreamCodec<FriendlyByteBuf, ClientModCustomContentPacketPayload> STREAM_CODEC = CustomPacketPayload.codec(ClientModCustomContentPacketPayload::write, ClientModCustomContentPacketPayload::new);
    public static final CustomPacketPayload.TypeAndCodec<FriendlyByteBuf, ?> TYPE_AND_CODEC = new CustomPacketPayload.TypeAndCodec<>(TYPE, ClientModCustomContentPacketPayload.STREAM_CODEC);

    private static final ClientModCustomContentPacketPayload INSTANCE = new ClientModCustomContentPacketPayload();
    private static final ClientboundCustomPayloadPacket PACKET_INSTANCE = new ClientboundCustomPayloadPacket(INSTANCE);

    private ClientModCustomContentPacketPayload() {
    }

    private ClientModCustomContentPacketPayload(FriendlyByteBuf buffer) {
        // No need to parse: only needs to happen on the client
    }

    @Override
    public Type<ClientModCustomContentPacketPayload> type() {
        return TYPE;
    }

    private void write(FriendlyByteBuf buffer) {

        // Set a client mod client view to prevent any mapping
        spout.server.paper.impl.clientview.lookup.packethandling.ClientViewLookupThreadLocal.THREAD_LOCAL.set(new java.lang.ref.WeakReference<>(new spout.server.paper.impl.clientview.lookup.ClientViewLookup() {
            @Override
            public ClientView getClientView() {
                return new JavaWithClientModClientViewImpl(null);
            }
        }));

        // Write the custom content
        ClientModCustomContent customContent = new ClientModCustomContent(
            BlockRegistry.get().stream().filter(block -> !block.isVanilla()).toList(),
            ItemRegistry.get().stream().filter(item -> !item.isVanilla()).toList()
        );
        buffer.writeJsonWithCodec(ClientModCustomContent.CODEC, customContent);

        // Remove the temporary client view
        spout.server.paper.impl.clientview.lookup.packethandling.ClientViewLookupThreadLocal.THREAD_LOCAL.remove();

    }

    public static void send(Connection connection) {
        connection.send(PACKET_INSTANCE);
    }

}
