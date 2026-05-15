package spout.server.paper.impl.packetmapping.command;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/**
 * Extends commands suggestions to include {@link SuggestionProviders#ASK_SERVER}
 * for nodes with arguments that are registry keys.
 */
public final class AskServerCommandSuggestionsExtender {

    public static @Nullable Identifier getIdForNode(ArgumentCommandNode<CommandSourceStack, ?> node) {
        // TODO only apply this for non-client-mod clients
        return switch (node.getType()) {
            case ItemArgument _, ResourceArgument<?> _, ResourceOrTagArgument<?> _, ResourceOrTagKeyArgument<?> _ -> SuggestionProviders.getName(SuggestionProviders.ASK_SERVER);
            default -> null;
        };
    }

}
