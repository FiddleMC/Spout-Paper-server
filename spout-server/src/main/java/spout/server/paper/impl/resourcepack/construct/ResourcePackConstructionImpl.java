package spout.server.paper.impl.resourcepack.construct;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventRunner;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.PrioritizedLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.PrioritizableLifecycleEventType;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstructEvent;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstructFinishEvent;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstruction;
import spout.server.paper.impl.configuration.SpoutGlobalConfiguration;
import spout.server.paper.impl.moredatadriven.minecraft.BlockRegistry;
import spout.server.paper.impl.resourcepack.plugin.discover.PluginResourcePackDiscoveryImpl;
import spout.server.paper.impl.resourcepack.send.ResourcePackSending;
import spout.server.paper.impl.resourcepack.serve.ResourcePackServing;
import spout.server.paper.impl.util.composable.ComposableImpl;
import spout.server.paper.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The implementation for {@link ResourcePackConstruction}.
 */
public final class ResourcePackConstructionImpl extends ComposableImpl<ResourcePackConstructEvent, ResourcePackConstructEventImpl> implements ResourcePackConstruction {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<ResourcePackConstruction, ResourcePackConstructionImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(ResourcePackConstructionImpl.class);
        }

    }

    public static ResourcePackConstructionImpl get() {
        return (ResourcePackConstructionImpl) ResourcePackConstruction.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "spout_resource_pack_construction";
    }

    private ResourcePackConstructionImpl() {
    }

    @Override
    protected ResourcePackConstructEventImpl createComposeEvent() {
        // Create the event
        ResourcePackConstructEventImpl event = new ResourcePackConstructEventImpl();
        // Add default contents
        for (String path : DEFAULT_RESOURCE_PACK_CONTENTS_PATHS) {
            try {
                event.copyPluginResource((Class) this.getClass(), Arrays.stream(ClientView.AwarenessLevel.getAll()).filter(ResourcePackConstructionImpl::generateForAwarenessLevel).toList(), "default_resource_pack_contents/" + path, path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Add included plugin contents
        PluginResourcePackDiscoveryImpl.get().getProvidingPluginsByResourcePackFileRelativePath().forEach(pair -> {
            List<Pair<PluginBootstrap, String>> providingPlugins = pair.right();
            if (providingPlugins.isEmpty()) {
                return;
            }
            Pair<PluginBootstrap, String> providingPlugin = providingPlugins.getLast();
            String pathInResourcePack = pair.left();
            // Filter out language files
            if (isLangFile(pathInResourcePack)) {
                return;
            }
            // Filter out non-vanilla blockstates for vanilla clients
            boolean isNonVanillaBlockstates;
            Matcher blockstatesMatcher = BLOCKSTATES_FILE_PATTERN.matcher(pathInResourcePack);
            if (blockstatesMatcher.find()) {
                try {
                    String namespace = blockstatesMatcher.group(1);
                    String path = blockstatesMatcher.group(2);
                    Identifier identifier = Identifier.fromNamespaceAndPath(namespace, path);
                    Optional<Block> optionalBlock = BlockRegistry.get().getOptional(identifier);
                    if (optionalBlock.isEmpty()) {
                        // The block doesn't exist, don't include it
                    }
                    Block block = optionalBlock.get();
                    isNonVanillaBlockstates = !block.isVanilla();
                } catch (Exception ignored) {
                    // Something is weird, let's assume we shouldn't include it
                    isNonVanillaBlockstates = true;
                }
            } else {
                // Not a blockstates file
                isNonVanillaBlockstates = false;
            }
            ClientView.AwarenessLevel[] awarenessLevels = isNonVanillaBlockstates ? new ClientView.AwarenessLevel[]{ClientView.AwarenessLevel.CLIENT_MOD} : new ClientView.AwarenessLevel[]{ClientView.AwarenessLevel.RESOURCE_PACK, ClientView.AwarenessLevel.CLIENT_MOD};
            try {
                event.copyPluginResource(providingPlugin.left(), awarenessLevels, (providingPlugin.right().isEmpty() ? "" : providingPlugin.right() + "/") + pathInResourcePack, pathInResourcePack);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // Return the event
        return event;
    }

    @Override
    protected void copyInformationFromEvent(final ResourcePackConstructEventImpl event) {
        // Build the pack contents
        Map<ClientView.AwarenessLevel, byte[]> packBytes;
        try {
            packBytes = event.buildPacks();
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while constructing the server resource pack", e);
        }
        // Create pack instances
        Map<ClientView.AwarenessLevel, ConstructedResourcePackImpl> packs = new EnumMap<>(ClientView.AwarenessLevel.class);
        for (Map.Entry<ClientView.AwarenessLevel, byte[]> entry : packBytes.entrySet()) {
            packs.put(entry.getKey(), new ConstructedResourcePackImpl(entry.getKey(), entry.getValue()));
        }
        // Call plugins
        LifecycleEventRunner.INSTANCE.callEvent(this.finish(), new ResourcePackConstructFinishEventImpl(packs));
        // Get the packs to pass to built-in output use cases
        ConstructedResourcePackImpl vanillaPack = packs.get(ClientView.AwarenessLevel.RESOURCE_PACK);
        ConstructedResourcePackImpl clientModPack = packs.get(ClientView.AwarenessLevel.CLIENT_MOD);
        // TODO save to file if enabled
        // Set up the HTTP serving
        if (ResourcePackServing.isEnabled()) {
            ResourcePackServing.start(vanillaPack, clientModPack);
        }
        // Initialize the packet sending
        ResourcePackSending.initialize(vanillaPack, clientModPack);
    }

    /**
     * Whether constructing the resource pack is enabled.
     */
    public boolean isEnabled() {
        return SpoutGlobalConfiguration.get().generatedResourcePack.output.serveOverHttp.enabled;
    }

    private static class ResourcePackConstructFinishEventType extends PrioritizableLifecycleEventType.Simple<BootstrapContext, ResourcePackConstructFinishEvent> {

        public ResourcePackConstructFinishEventType() {
            super("spout_resource_pack_construction_finish", BootstrapContext.class);
        }

    }

    /**
     * The cached return value of {@link #finish()},
     * or null if not cached yet.
     */
    private ResourcePackConstructionImpl.@Nullable ResourcePackConstructFinishEventType finishEventType;

    @Override
    public LifecycleEventType<BootstrapContext, ResourcePackConstructFinishEvent, PrioritizedLifecycleEventHandlerConfiguration<BootstrapContext>> finish() {
        if (this.finishEventType == null) {
            this.finishEventType = new ResourcePackConstructFinishEventType();
        }
        return this.finishEventType;
    }

    public static boolean generateForAwarenessLevel(ClientView.AwarenessLevel awarenessLevel) {
        return awarenessLevel != ClientView.AwarenessLevel.VANILLA;
    }

    private static final String[] DEFAULT_RESOURCE_PACK_CONTENTS_PATHS = {
        "pack.mcmeta",
        "assets/spout/models/block/bevel_middle.json",
        "assets/spout/models/block/bevel_nnnnnnnn.json",
        "assets/spout/models/block/bevel_nnnynnyn.json",
        "assets/spout/models/block/bevel_nnnyynnn.json",
        "assets/spout/models/block/bevel_nnynnnnn.json",
        "assets/spout/models/block/bevel_nnyynnnn.json",
        "assets/spout/models/block/bevel_nnyynnyn.json",
        "assets/spout/models/block/bevel_nnyynnyy.json",
        "assets/spout/models/block/bevel_nnyynynn.json",
        "assets/spout/models/block/bevel_nnyynyyn.json",
        "assets/spout/models/block/bevel_nnyyynnn.json",
        "assets/spout/models/block/bevel_nnyyynyn.json",
        "assets/spout/models/block/bevel_nnyyyynn.json",
        "assets/spout/models/block/bevel_nnyyyyyn.json",
        "assets/spout/models/block/bevel_nynnynnn.json",
        "assets/spout/models/block/bevel_nynynnyn.json",
        "assets/spout/models/block/bevel_nynyynnn.json",
        "assets/spout/models/block/bevel_nynyynyn.json",
        "assets/spout/models/block/bevel_nyynnnnn.json",
        "assets/spout/models/block/bevel_nyynnynn.json",
        "assets/spout/models/block/bevel_nyynynnn.json",
        "assets/spout/models/block/bevel_nyynyynn.json",
        "assets/spout/models/block/bevel_nyyynnnn.json",
        "assets/spout/models/block/bevel_nyyynnyn.json",
        "assets/spout/models/block/bevel_nyyynynn.json",
        "assets/spout/models/block/bevel_nyyynyyn.json",
        "assets/spout/models/block/bevel_nyyyynnn.json",
        "assets/spout/models/block/bevel_nyyyynyn.json",
        "assets/spout/models/block/bevel_nyyyynyy.json",
        "assets/spout/models/block/bevel_nyyyyynn.json",
        "assets/spout/models/block/bevel_nyyyyyyn.json",
        "assets/spout/models/block/bevel_ynnnnnnn.json",
        "assets/spout/models/block/bevel_ynnynnnn.json",
        "assets/spout/models/block/bevel_ynnynnyn.json",
        "assets/spout/models/block/bevel_ynnynyyn.json",
        "assets/spout/models/block/bevel_ynnyynnn.json",
        "assets/spout/models/block/bevel_ynnyynyn.json",
        "assets/spout/models/block/bevel_ynynnnnn.json",
        "assets/spout/models/block/bevel_ynyynnnn.json",
        "assets/spout/models/block/bevel_ynyynnyn.json",
        "assets/spout/models/block/bevel_ynyynnyy.json",
        "assets/spout/models/block/bevel_ynyynynn.json",
        "assets/spout/models/block/bevel_ynyynyyn.json",
        "assets/spout/models/block/bevel_ynyyynnn.json",
        "assets/spout/models/block/bevel_ynyyynyn.json",
        "assets/spout/models/block/bevel_ynyyyynn.json",
        "assets/spout/models/block/bevel_ynyyyyyn.json",
        "assets/spout/models/block/bevel_yynnnnnn.json",
        "assets/spout/models/block/bevel_yynnynnn.json",
        "assets/spout/models/block/bevel_yynnyynn.json",
        "assets/spout/models/block/bevel_yynynnnn.json",
        "assets/spout/models/block/bevel_yynynnyn.json",
        "assets/spout/models/block/bevel_yynynyyn.json",
        "assets/spout/models/block/bevel_yynyynnn.json",
        "assets/spout/models/block/bevel_yynyynyn.json",
        "assets/spout/models/block/bevel_yynyyyyn.json",
        "assets/spout/models/block/bevel_yyynnnnn.json",
        "assets/spout/models/block/bevel_yyynnynn.json",
        "assets/spout/models/block/bevel_yyynynnn.json",
        "assets/spout/models/block/bevel_yyynyynn.json",
        "assets/spout/models/block/bevel_yyyynnnn.json",
        "assets/spout/models/block/bevel_yyyynnyn.json",
        "assets/spout/models/block/bevel_yyyynnyy.json",
        "assets/spout/models/block/bevel_yyyynynn.json",
        "assets/spout/models/block/bevel_yyyynyyn.json",
        "assets/spout/models/block/bevel_yyyyynnn.json",
        "assets/spout/models/block/bevel_yyyyynyn.json",
        "assets/spout/models/block/bevel_yyyyynyy.json",
        "assets/spout/models/block/bevel_yyyyyynn.json",
        "assets/spout/models/block/bevel_yyyyyyyn.json",
        "assets/spout/models/block/bevel_yyyyyyyy.json",
        "assets/spout/models/block/top_half_texture_bottom_slab.json",
    };

    private static boolean isLangFile(String pathInResourcePack) {
        return pathInResourcePack.matches("assets/minecraft/lang/[^/]+\\.json");
    }

    private static final Pattern BLOCKSTATES_FILE_PATTERN = Pattern.compile("assets/([^/]+)/blockstates/([^/]+)\\.json");

}
