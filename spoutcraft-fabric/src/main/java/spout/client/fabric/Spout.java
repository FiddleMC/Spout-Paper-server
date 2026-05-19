package spout.client.fabric;

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Spout implements ModInitializer {

	public static final String MOD_ID = "spoutcraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        try {
            Class.forName("spout.client.fabric.SpoutClient");
        } catch (ClassNotFoundException ignored) {
            LOGGER.warn("Spoutcraft loaded on a Fabric server - will have no effect");
        }
	}

}
