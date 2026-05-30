package spout.main;

import net.fabricmc.api.ClientModInitializer;
import spout.client.fabric.clientview.SpoutProtocol;

public class SpoutcraftClientModInitializer implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
        SpoutProtocol.initialize();
    }

}
