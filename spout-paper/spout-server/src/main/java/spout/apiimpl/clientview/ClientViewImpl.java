package spout.apiimpl.clientview;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import spout.api.clientview.ClientViewNMS;
import spout.api.clientview.ClientView;

/**
 * Implementation of {@link ClientView} and {@link ClientViewNMS}.
 */
public class ClientViewImpl implements ClientViewNMS {

    private final spout.clientview.model.ClientViewImpl handle;

    public ClientViewImpl(spout.clientview.model.ClientViewImpl handle) {
        this.handle = handle;
    }

    @Override
    public AwarenessLevel getAwarenessLevel() {
        return this.handle.getAwarenessLevel();
        // return new AwarenessLevelImpl(this.handle.getAwarenessLevel());
    }

    @Override
    public @Nullable Connection getConnection() {
        return this.handle.getConnection();
    }

    @Override
    public @Nullable ServerPlayer getPlayerNMS() {
        return this.handle.getPlayerNMS();
    }

    @Override
    public @Nullable Player getPlayer() {
        @Nullable ServerPlayer playerNMS = this.getPlayerNMS();
        return playerNMS == null ? null : playerNMS.getBukkitEntity();
    }

    @Override
    public @Nullable String getLocale() {
        return this.handle.getLocale();
    }

    @Override
    public boolean understandsAllServerSideTranslatables() {
        return this.handle.understandsAllServerSideTranslatables();
    }

    @Override
    public boolean understandsAllServerSideItems() {
        return this.handle.understandsAllServerSideItems();
    }

    @Override
    public boolean understandsAllServerSideBlocks() {
        return this.handle.understandsAllServerSideBlocks();
    }

    // private static class AwarenessLevelImpl implements AwarenessLevel {
    //
    //     private final spout.clientview.model.ClientViewImpl.AwarenessLevel handle;
    //
    //     public AwarenessLevelImpl(spout.clientview.model.ClientViewImpl.AwarenessLevel handle) {
    //         this.handle = handle;
    //     }
    //
    //     @Override
    //     public boolean alwaysUnderstandsAllServerSideTranslatables() {
    //         return this.handle.alwaysUnderstandsAllServerSideTranslatables();
    //     }
    //
    //     @Override
    //     public boolean alwaysUnderstandsAllServerSideItems() {
    //         return this.handle.alwaysUnderstandsAllServerSideItems();
    //     }
    //
    //     @Override
    //     public boolean alwaysUnderstandsAllServerSideBlocks() {
    //         return this.handle.alwaysUnderstandsAllServerSideBlocks();
    //     }
    //
    // }

}
