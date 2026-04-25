package spout.server.paper.impl.logging;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import spout.server.paper.impl.configuration.SpoutGlobalConfiguration;
import spout.server.paper.impl.moredatadriven.minecraft.BlockRegistry;
import spout.server.paper.impl.moredatadriven.minecraft.BlockStateRegistry;
import spout.server.paper.impl.moredatadriven.minecraft.ItemRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Utility class to log the loaded custom content.
 */
public final class CustomContentLogging {

    private CustomContentLogging() {
        throw new UnsupportedOperationException();
    }

    public static void logCustomContent(Logger logger) {
        SpoutGlobalConfiguration.Logging.OnStartup onStartupLogging = SpoutGlobalConfiguration.get().logging.onStartup;
        if (onStartupLogging.blocks || onStartupLogging.blockStates || onStartupLogging.items) {
            // Do detailed logging
            if (onStartupLogging.blocks || onStartupLogging.blockCount) {
                List<Block> blocks = BlockRegistry.get().stream().filter(block -> !block.isVanilla()).toList();
                boolean list = onStartupLogging.blocks && !blocks.isEmpty();
                logger.info("Loaded {}custom blocks{}", onStartupLogging.blockCount ? blocks.size() + " " : "", list ? ":" : "");
                if (list) {
                    blocks.stream().map(block -> "* " + block.keyInBlockRegistry).sorted().forEach(logger::info);
                }
            }
            if (onStartupLogging.blockStates || onStartupLogging.blockStateCount) {
                List<BlockState> blockStates = StreamSupport.stream(BlockStateRegistry.get().spliterator(), false).filter(blockState -> !blockState.isVanilla()).toList();
                boolean list = onStartupLogging.blockStates && !blockStates.isEmpty();
                logger.info("Loaded {}custom block states{}", onStartupLogging.blockStateCount ? blockStates.size() + " " : "", list ? ":" : "");
                if (list) {
                    blockStates.stream().map(blockState -> "* " + blockState.createCraftBlockData().getAsString(false)).sorted().forEach(logger::info);
                }
            }
            if (onStartupLogging.items || onStartupLogging.itemCount) {
                List<Item> items = ItemRegistry.get().stream().filter(item -> !item.isVanilla()).toList();
                boolean list = onStartupLogging.items && !items.isEmpty();
                logger.info("Loaded {}custom items{}", onStartupLogging.itemCount ? items.size() + " " : "", list ? ":" : "");
                if (list) {
                    items.stream().map(item -> "* " + item.keyInItemRegistry).sorted().forEach(logger::info);
                }
            }
        } else if (onStartupLogging.blockCount || onStartupLogging.blockStateCount || onStartupLogging.itemCount) {
            // Do simple count logging
            List<String> parts = new ArrayList<>(3);
            if (onStartupLogging.blockCount) {
                parts.add(BlockRegistry.get().stream().filter(block -> !block.isVanilla()).count() + " custom blocks");
            }
            if (onStartupLogging.blockStateCount) {
                parts.add(StreamSupport.stream(BlockStateRegistry.get().spliterator(), false).filter(blockState -> !blockState.isVanilla()).count() + " custom block states");
            }
            if (onStartupLogging.itemCount) {
                parts.add(ItemRegistry.get().stream().filter(item -> !item.isVanilla()).count() + " custom items");
            }
            StringBuilder lineBuilder = new StringBuilder("Loaded ");
            for (int i = 0; i < parts.size(); i++) {
                lineBuilder.append(parts.get(i));
                if (i < parts.size() - 2) {
                    lineBuilder.append(", ");
                } else if (i == parts.size() - 2) {
                    lineBuilder.append(" and ");
                }
            }
            logger.info(lineBuilder.toString());
        }
    }

}
