package spout.common.branding;

import net.kyori.adventure.key.Key;

/**
 * Holder for {@link #BRAND_ID}.
 */
public final class SpoutPaperServerBrandId {

    private SpoutPaperServerBrandId() {
        throw new UnsupportedOperationException();
    }

    /**
     * The Brand id of Spout.
     * Replacement for {@link io.papermc.paper.ServerBuildInfo#BRAND_PAPER_ID}.
     */
    public static final Key BRAND_ID = Key.key("spout", "spout-paper-server");

}
