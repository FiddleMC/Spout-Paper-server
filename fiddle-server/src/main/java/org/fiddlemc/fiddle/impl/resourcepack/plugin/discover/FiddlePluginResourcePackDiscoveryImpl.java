package org.fiddlemc.fiddle.impl.resourcepack.plugin.discover;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.Identifier;
import org.fiddlemc.fiddle.api.resourcepack.content.Blockstates;
import org.fiddlemc.fiddle.api.resourcepack.content.Lang;
import org.fiddlemc.fiddle.api.resourcepack.plugin.discover.FiddlePluginResourcePackDiscoverEvent;
import org.fiddlemc.fiddle.api.resourcepack.plugin.discover.FiddlePluginResourcePackDiscovery;
import org.fiddlemc.fiddle.impl.packetmapping.component.translatable.MinecraftLocaleUtil;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The implementation for {@link FiddlePluginResourcePackDiscovery}.
 */
public final class FiddlePluginResourcePackDiscoveryImpl extends ComposableImpl<FiddlePluginResourcePackDiscoverEvent, FiddlePluginResourcePackDiscoverEventImpl> implements FiddlePluginResourcePackDiscovery {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<FiddlePluginResourcePackDiscovery, FiddlePluginResourcePackDiscoveryImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(FiddlePluginResourcePackDiscoveryImpl.class);
        }

    }

    public static FiddlePluginResourcePackDiscoveryImpl get() {
        return (FiddlePluginResourcePackDiscoveryImpl) FiddlePluginResourcePackDiscovery.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "fiddle_plugin_resource_pack_discovery";
    }

    /**
     * A map from each {@link PluginBootstrap} to a pair of
     * the {@linkplain BootstrapContext#getPluginSource() plugin source}
     * and the path inside the JAR.
     */
    Map<PluginBootstrap, Pair<Path, String>> resourcePackPaths = new LinkedHashMap<>();

    private final Map<Pair<PluginBootstrap, Identifier>, Blockstates> cachedBlockstates = new HashMap<>();

    private FiddlePluginResourcePackDiscoveryImpl() {
    }

    @Override
    protected FiddlePluginResourcePackDiscoverEventImpl createComposeEvent() {
        return new FiddlePluginResourcePackDiscoverEventImpl();
    }

    public @Nullable String getIncludedResourcePackPath(PluginBootstrap pluginBootstrap) {
        return this.resourcePackPaths.get(pluginBootstrap).right();
    }

    public List<Pair<PluginBootstrap, Pair<Path, String>>> getIncludedResourcePackPaths() {
        return this.resourcePackPaths.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).toList();
    }

    public Blockstates getResourcePackBlockstates(PluginBootstrap bootstrap, Identifier blockIdentifier) {
        return this.cachedBlockstates.computeIfAbsent(Pair.of(bootstrap, blockIdentifier), $ -> {
            String path = this.getIncludedResourcePackPath(bootstrap) + "/assets/" + blockIdentifier.getNamespace() + "/blockstates/" + blockIdentifier.getPath() + ".json";
            try {
                byte[] bytes = bootstrap.getClass().getClassLoader().getResourceAsStream(path).readAllBytes();
                String string = new String(bytes, StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(string);
                return Blockstates.ofMutable(jsonElement.getAsJsonObject());
            } catch (Exception e) {
                throw new RuntimeException("Missing or invalid blockstates file in plugin (" + bootstrap.getClass().getName() + ") resource pack: " + path, e);
            }
        });
    }

    public List<Pair<PluginBootstrap, List<Pair<MinecraftLocaleUtil.KnownLocale, Lang>>>> getResourcePackLangs() {
        return this.resourcePackPaths.entrySet().stream().map(entry -> {
            PluginBootstrap bootstrap = entry.getKey();
            String resourcePackPath = entry.getValue().right();
            List<Pair<MinecraftLocaleUtil.KnownLocale, Lang>> langs = new ArrayList<>(1);
            for (MinecraftLocaleUtil.KnownLocale locale : MinecraftLocaleUtil.getKnownLocales()) {
                try {
                    InputStream stream = bootstrap.getClass().getClassLoader().getResourceAsStream(resourcePackPath + "/assets/minecraft/lang/" + locale.lowerCaseLocale + ".json");
                    if (stream != null) {
                        Reader reader = new InputStreamReader(stream);
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        Lang lang = Lang.wrap(json);
                        langs.add(Pair.of(locale, lang));
                    }
                } catch (Exception ignored) {
                }
            }
            return Pair.of(bootstrap, langs);
        }).filter(pair -> !pair.right().isEmpty()).toList();
    }

}
