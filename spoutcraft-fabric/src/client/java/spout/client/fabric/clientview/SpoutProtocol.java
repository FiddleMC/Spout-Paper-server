package spout.client.fabric.clientview;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import spout.client.fabric.clientview.mixin.ClientCommonPacketListenerImplAccessor;
import spout.client.fabric.moredatadriven.TemporaryRegistryModifiers;
import spout.client.fabric.ui.loadingoverlay.SwitchOverlayStyle;
import spout.common.branding.SpoutNamespace;
import spout.common.moredatadriven.clientmodprotocol.ClientModCustomContentPacketPayload;
import spout.common.moredatadriven.clientmodprotocol.ClientModCustomContentReceiving;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles the detection of the client mod by the server,
 * and the interpretation of related packets.
 */
public final class SpoutProtocol {

    private static final Identifier CLIENT_MOD_DETECTION_PACKET_ID = Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "detect_client_mod");
    private static final int MIN_PROTOCOL_VERSION = 3;
    private static final int MAX_PROTOCOL_VERSION = 3;

    private static final AtomicReference<ClientModState> state = new AtomicReference<>(ClientModState.IDLE);

    private SpoutProtocol() {
        throw new UnsupportedOperationException();
    }

    public static void initialize() {
        ClientLoginNetworking.registerGlobalReceiver(CLIENT_MOD_DETECTION_PACKET_ID, (client, handler, buf, callbacksConsumer) -> {
            // First, the server will send a 0, if not, then there must be a protocol difference that we are unaware of
            int zero = buf.readVarInt();
            if (zero == 0) {
                // Read the nonce
                int nonce = buf.readVarInt();
                // Read the protocol versions supported by the server (and perform basic validation on what we read)
                int minServerProtocolVersion = buf.readVarInt();
                if (minServerProtocolVersion >= 1) {
                    int maxServerProtocolVersion = buf.readVarInt();
                    if (maxServerProtocolVersion >= minServerProtocolVersion) {
                        // The best protocol version is the highest supported by both client and server
                        int bestProtocolVersion = Math.min(maxServerProtocolVersion, MAX_PROTOCOL_VERSION);
                        boolean isBestProtocolVersionAcceptable = bestProtocolVersion >= minServerProtocolVersion && bestProtocolVersion >= MIN_PROTOCOL_VERSION;
                        int responseProtocolVersion = -1; // In case of failure, respond with an invalid version
                        if (isBestProtocolVersionAcceptable) {
                            changeState(ClientModState.HANDSHAKE_STARTED, ClientModState.CLIENT_MOD_DETECTED);
                            SwitchOverlayStyle.setSpout();
                            responseProtocolVersion = bestProtocolVersion;
                        }
                        // Respond
                        FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer(15));
                        response.writeVarInt(0);
                        response.writeVarInt(nonce);
                        response.writeVarInt(responseProtocolVersion);
                        return CompletableFuture.completedFuture(response);
                    }
                }
            }
            // We did not understand this protocol
            return CompletableFuture.completedFuture(null);
        });
        PayloadTypeRegistry.clientboundConfiguration().register(ClientModCustomContentPacketPayload.TYPE, ClientModCustomContentPacketPayload.STREAM_CODEC);
        ClientConfigurationNetworking.registerGlobalReceiver(ClientModCustomContentPacketPayload.TYPE, (payload, context) -> {
            ClientModCustomContentReceiving.handlePacket(payload);
        });
        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
            SpoutProtocol.changeState(ClientModState.IDLE, ClientModState.HANDSHAKE_STARTED);
        });
        ClientConfigurationConnectionEvents.INIT.register((handler, client) -> {
            // Make sure the state is valid
            while (true) {
                if (SpoutProtocol.getState() == ClientModState.CLIENT_MOD_DETECTED) {
                    // Force accepting of packs
                    ClientCommonPacketListenerImplAccessor accessor = (ClientCommonPacketListenerImplAccessor) handler;
                    ServerData serverData = accessor.getServerData();
                    serverData.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);

                    break;
                }
                if (SpoutProtocol.tryChangeState(ClientModState.HANDSHAKE_STARTED, ClientModState.CLIENT_MOD_NOT_DETECTED)) {
                    break;
                }
                Thread.onSpinWait();
            }
        });
        ClientLoginConnectionEvents.DISCONNECT.register((handler, client) -> {
            onDisconnect();
        });
        ClientConfigurationConnectionEvents.DISCONNECT.register((handler, client) -> {
            onDisconnect();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            onDisconnect();
        });
    }

    private static void onDisconnect() {
        SwitchOverlayStyle.setMojang();
        // Clear custom content, if present
        while (true) {
            if (SpoutProtocol.getState() == ClientModState.ADDED_CUSTOM_CONTENT) {
                TemporaryRegistryModifiers.removeCustomContent();
                SpoutProtocol.changeState(ClientModState.ADDED_CUSTOM_CONTENT, ClientModState.REMOVED_CUSTOM_CONTENT);
                break;
            }
            if (SpoutProtocol.tryChangeState(Set.of(ClientModState.IDLE, ClientModState.HANDSHAKE_STARTED, ClientModState.CLIENT_MOD_DETECTED, ClientModState.CLIENT_MOD_NOT_DETECTED), ClientModState.REMOVED_CUSTOM_CONTENT)) {
                break;
            }
            Thread.onSpinWait();
        }
        // Reset to idle
        SpoutProtocol.changeState(ClientModState.REMOVED_CUSTOM_CONTENT, ClientModState.IDLE);
    }

    public static boolean tryChangeState(ClientModState oldState, ClientModState newState) {
        return state.compareAndSet(oldState, newState);
    }

    public static void changeState(ClientModState oldState, ClientModState newState) {
        while (!tryChangeState(oldState, newState)) {
            Thread.onSpinWait();
        }
    }

    public static boolean tryChangeState(Set<ClientModState> oldStates, ClientModState newState) {
        ClientModState currentState = state.get();
        if (oldStates.contains(currentState)) {
            if (state.compareAndSet(currentState, newState)) {
                return true;
            }
        }
        return false;
    }

    public static void changeState(Set<ClientModState> oldStates, ClientModState newState) {
        while (!tryChangeState(oldStates, newState)) {
            Thread.onSpinWait();
        }
    }

    public static ClientModState getState() {
        return state.get();
    }

}
