package spout.common.branding;

/**
 * Holds the {@link #SPOUT} namespace.
 */
public final class SpoutNamespace {

    private SpoutNamespace() {
        throw new UnsupportedOperationException();
    }

    /**
     * The namespace for Spout namespaced keys.
     *
     * <p>
     * This is for namespaced keys that are defined by and belong to Spout itself,
     * not those of content added by Spout plugins (those plugins should use their own namespaces).
     * </p>
     */
    public static final String SPOUT = "spout";

}
