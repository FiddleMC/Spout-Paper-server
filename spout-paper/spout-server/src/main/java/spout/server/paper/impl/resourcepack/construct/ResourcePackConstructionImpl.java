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
import spout.clientview.model.ClientView;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstructEvent;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstructFinishEvent;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstruction;
import spout.server.paper.impl.configuration.SpoutGlobalConfiguration;
import spout.server.paper.impl.moredatadriven.minecraft.BlockRegistry;
import spout.server.paper.impl.resourcepack.plugin.discover.PluginResourcePackDiscoveryImpl;
import spout.server.paper.impl.resourcepack.send.ResourcePackSending;
import spout.server.paper.impl.resourcepack.serve.ResourcePackServing;
import spout.server.paper.impl.util.composable.ComposableImpl;
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

    public static ResourcePackConstructionImpl get() {
        return (ResourcePackConstructionImpl) ResourcePackConstruction.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "spout_resource_pack_construction";
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
        "pack.png",
        "assets/spout/models/block/quad_middle.json",
        "assets/spout/models/block/quad_nnnnnnnn.json",
        "assets/spout/models/block/quad_nnnynnyn.json",
        "assets/spout/models/block/quad_nnnyynnn.json",
        "assets/spout/models/block/quad_nnynnnnn.json",
        "assets/spout/models/block/quad_nnyynnnn.json",
        "assets/spout/models/block/quad_nnyynnyn.json",
        "assets/spout/models/block/quad_nnyynnyy.json",
        "assets/spout/models/block/quad_nnyynynn.json",
        "assets/spout/models/block/quad_nnyynyyn.json",
        "assets/spout/models/block/quad_nnyyynnn.json",
        "assets/spout/models/block/quad_nnyyynyn.json",
        "assets/spout/models/block/quad_nnyyyynn.json",
        "assets/spout/models/block/quad_nnyyyyyn.json",
        "assets/spout/models/block/quad_nynnynnn.json",
        "assets/spout/models/block/quad_nynynnyn.json",
        "assets/spout/models/block/quad_nynyynnn.json",
        "assets/spout/models/block/quad_nynyynyn.json",
        "assets/spout/models/block/quad_nyynnnnn.json",
        "assets/spout/models/block/quad_nyynnynn.json",
        "assets/spout/models/block/quad_nyynynnn.json",
        "assets/spout/models/block/quad_nyynyynn.json",
        "assets/spout/models/block/quad_nyyynnnn.json",
        "assets/spout/models/block/quad_nyyynnyn.json",
        "assets/spout/models/block/quad_nyyynynn.json",
        "assets/spout/models/block/quad_nyyynyyn.json",
        "assets/spout/models/block/quad_nyyyynnn.json",
        "assets/spout/models/block/quad_nyyyynyn.json",
        "assets/spout/models/block/quad_nyyyynyy.json",
        "assets/spout/models/block/quad_nyyyyynn.json",
        "assets/spout/models/block/quad_nyyyyyyn.json",
        "assets/spout/models/block/quad_ynnnnnnn.json",
        "assets/spout/models/block/quad_ynnynnnn.json",
        "assets/spout/models/block/quad_ynnynnyn.json",
        "assets/spout/models/block/quad_ynnynyyn.json",
        "assets/spout/models/block/quad_ynnyynnn.json",
        "assets/spout/models/block/quad_ynnyynyn.json",
        "assets/spout/models/block/quad_ynynnnnn.json",
        "assets/spout/models/block/quad_ynyynnnn.json",
        "assets/spout/models/block/quad_ynyynnyn.json",
        "assets/spout/models/block/quad_ynyynnyy.json",
        "assets/spout/models/block/quad_ynyynynn.json",
        "assets/spout/models/block/quad_ynyynyyn.json",
        "assets/spout/models/block/quad_ynyyynnn.json",
        "assets/spout/models/block/quad_ynyyynyn.json",
        "assets/spout/models/block/quad_ynyyyynn.json",
        "assets/spout/models/block/quad_ynyyyyyn.json",
        "assets/spout/models/block/quad_yynnnnnn.json",
        "assets/spout/models/block/quad_yynnynnn.json",
        "assets/spout/models/block/quad_yynnyynn.json",
        "assets/spout/models/block/quad_yynynnnn.json",
        "assets/spout/models/block/quad_yynynnyn.json",
        "assets/spout/models/block/quad_yynynyyn.json",
        "assets/spout/models/block/quad_yynyynnn.json",
        "assets/spout/models/block/quad_yynyynyn.json",
        "assets/spout/models/block/quad_yynyyyyn.json",
        "assets/spout/models/block/quad_yyynnnnn.json",
        "assets/spout/models/block/quad_yyynnynn.json",
        "assets/spout/models/block/quad_yyynynnn.json",
        "assets/spout/models/block/quad_yyynyynn.json",
        "assets/spout/models/block/quad_yyyynnnn.json",
        "assets/spout/models/block/quad_yyyynnyn.json",
        "assets/spout/models/block/quad_yyyynnyy.json",
        "assets/spout/models/block/quad_yyyynynn.json",
        "assets/spout/models/block/quad_yyyynyyn.json",
        "assets/spout/models/block/quad_yyyyynnn.json",
        "assets/spout/models/block/quad_yyyyynyn.json",
        "assets/spout/models/block/quad_yyyyynyy.json",
        "assets/spout/models/block/quad_yyyyyynn.json",
        "assets/spout/models/block/quad_yyyyyyyn.json",
        "assets/spout/models/block/quad_yyyyyyyy.json",
        "assets/spout/models/block/top_half_texture_bottom_slab.json",
        "assets/spout/models/block/vertical_slab.json",
        "assets/spout/models/block/vertical_slab_tinted.json",
    };

    private static boolean isLangFile(String pathInResourcePack) {
        return pathInResourcePack.matches("assets/minecraft/lang/[^/]+\\.json");
    }

    private static final Pattern BLOCKSTATES_FILE_PATTERN = Pattern.compile("assets/([^/]+)/blockstates/([^/]+)\\.json");

}
