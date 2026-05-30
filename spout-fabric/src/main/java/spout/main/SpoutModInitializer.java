package spout.main;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpoutModInitializer implements ModInitializer {

	public static final String MOD_ID = "spout";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        System.out.println("Initializing Spouts server mod...");
	}

}
