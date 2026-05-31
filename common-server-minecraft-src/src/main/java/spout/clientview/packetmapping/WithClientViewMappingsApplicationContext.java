package spout.clientview.packetmapping;

import spout.clientview.model.ClientView;

/**
 * A context for mappings that happen in the context of some {@link ClientView}.
 */
public class WithClientViewMappingsApplicationContext {

    private final ClientView clientView;

    public WithClientViewMappingsApplicationContext(ClientView clientView) {
        this.clientView = clientView;
    }

    /**
     * @return The {@link ClientView} of the client that mappings are being applied for.
     */
    public ClientView getClientView() {
        return this.clientView;
    }

}
