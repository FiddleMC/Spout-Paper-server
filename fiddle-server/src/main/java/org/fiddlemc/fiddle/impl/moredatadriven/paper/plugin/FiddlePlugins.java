package org.fiddlemc.fiddle.impl.moredatadriven.paper.plugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.Identifier;
import org.fiddlemc.fiddle.api.resourcepack.content.Blockstates;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for Fiddle plugins' settings and details.
 */
public final class FiddlePlugins {

    private static Map<Pair<PluginBootstrap, Identifier>, Blockstates> cachedBlockstates = new HashMap<>();

    public static String getIncludedResourcePackPath(PluginBootstrap bootstrap) {
        return "resource_pack";
    }

    public static Blockstates getResourcePackBlockstates(PluginBootstrap bootstrap, Identifier blockIdentifier) {
        return cachedBlockstates.computeIfAbsent(Pair.of(bootstrap, blockIdentifier), $ -> {
            String path = getIncludedResourcePackPath(bootstrap) + "/assets/" + blockIdentifier.getNamespace() + "/blockstates/" + blockIdentifier.getPath() + ".json";
            try {
                byte[] bytes = bootstrap.getClass().getClassLoader().getResourceAsStream(path).readAllBytes();
                String string = new String(bytes, StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(string);
                return Blockstates.ofMutable(jsonElement.getAsJsonObject());
            } catch (IOException e) {
                throw new RuntimeException("Missing or invalid blockstates file in plugin resource pack: " + path, e);
            }
        });
    }

}
