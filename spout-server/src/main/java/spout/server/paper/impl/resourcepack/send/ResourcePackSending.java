package spout.server.paper.impl.resourcepack.send;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import spout.server.paper.impl.configuration.SpoutGlobalConfiguration;
import spout.server.paper.impl.resourcepack.construct.ConstructedResourcePackImpl;
import spout.server.paper.impl.resourcepack.construct.ResourcePackConstructionImpl;
import spout.server.paper.impl.resourcepack.serve.ResourcePackServing;
import org.jspecify.annotations.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles resource pack sending.
 */
public final class ResourcePackSending {

    private ResourcePackSending() {
        throw new UnsupportedOperationException();
    }

    /**
     * The return value for {@link #getVanillaPacket()}.
     */
    private static @Nullable ClientboundResourcePackPushPacket vanillaPacket;

    /**
     * The return value for {@link #getClientModPacket()}.
     */
    private static @Nullable ClientboundResourcePackPushPacket clientModPacket;

    private static String sha1(byte[] pack) {
        try {
            return ByteSource.wrap(pack).hash(Hashing.sha1()).toString();
        } catch (Exception e) {
            throw new RuntimeException("Exception while hashing generated resource pack contents", e);
        }
    }

    private static UUID uuid(String sha1) {
        return UUID.nameUUIDFromBytes(sha1.getBytes(StandardCharsets.UTF_8));
    }

    public static void initialize(ConstructedResourcePackImpl vanillaPack, ConstructedResourcePackImpl clientModPack) {
        ResourcePackConstructionImpl construction = ResourcePackConstructionImpl.get();
        if (!construction.isEnabled()) return;
        String vanillaURL;
        String clientModURL;
        if (ResourcePackServing.isEnabled()) {
            String baseURL = "http://" + SpoutGlobalConfiguration.get().generatedResourcePack.output.serveOverHttp.ip + ":" + SpoutGlobalConfiguration.get().generatedResourcePack.output.serveOverHttp.externalPort.or(ResourcePackServing.getPort()) + "/";
            vanillaURL = baseURL + ResourcePackServing.VANILLA_PACK_PATH;
            clientModURL = baseURL + ResourcePackServing.CLIENT_MOD_PACK_PATH;
        } else {
            throw new IllegalStateException(); // TODO allow using URL referring to some external service
        }
        vanillaPacket = new ClientboundResourcePackPushPacket(
            vanillaPack.getUUID(),
            vanillaURL,
            vanillaPack.getSHA1Hash(),
            false,
            Optional.of(Component.literal("\n")
                .append(Component.literal("\nThis server adds additional custom blocks and items"))
                .append(Component.literal("\nTo use them, click \"Yes\" below (Optional)")))
        );
        clientModPacket = new ClientboundResourcePackPushPacket(
            clientModPack.getUUID(),
            clientModURL,
            clientModPack.getSHA1Hash(),
            true,
            Optional.of(Component.literal("(This resource pack contains the textures for the custom blocks and items, and must be accepted"))
        );
    }

    /**
     * @return A {@link ClientboundResourcePackPushPacket} for sending the generated resource pack
     * meant for clients with a vanilla client, or null if not applicable.
     */
    public static @Nullable ClientboundResourcePackPushPacket getVanillaPacket() {
        return vanillaPacket;
    }

    /**
     * @return A {@link ClientboundResourcePackPushPacket} for sending the generated resource pack
     * meant for clients with the client mod, or null if not applicable.
     */
    public static @Nullable ClientboundResourcePackPushPacket getClientModPacket() {
        return clientModPacket;
    }

}
