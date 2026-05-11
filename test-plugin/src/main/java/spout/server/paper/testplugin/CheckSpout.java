package spout.server.paper.testplugin;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public final class CheckSpout {

    public static boolean checkSpout() {
        try {
            Class.forName("spout.server.paper.api.SpoutMarker");
            return true;
        } catch (ClassNotFoundException ignored) {
            ComponentLogger.logger().warn("This plugin requires Spout: https://github.com/ModernSpout/Spout");
            return false;
        }
    }

}
