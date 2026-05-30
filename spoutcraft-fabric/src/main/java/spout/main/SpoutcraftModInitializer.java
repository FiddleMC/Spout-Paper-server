package spout.main;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpoutcraftModInitializer implements ModInitializer {

	public static final String MOD_ID = "spoutcraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        try {
            Class.forName("spout.main.SpoutcraftClientModInitializer");
        } catch (ClassNotFoundException ignored) {
            LOGGER.warn("Spoutcraft client mod was loaded on a server - will have no effect");
        }
	}

}
