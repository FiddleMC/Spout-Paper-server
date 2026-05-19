package spout.common.moredatadriven.minecraft.common.dependent;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class for sorting {@link DependentDataDrivenResource}
 * into an order that is valid.
 */
public final class SortDependentDataDrivenResources {

    private SortDependentDataDrivenResources() {
        throw new UnsupportedOperationException();
    }

    /**
     * A node in a directed acyclic graph.
     */
    private static class DAGNode<T> {

        public final ResourceKey<T> key;
        public final T resource;

        public @Nullable List<DAGNode<T>> dependents;
        public int requiredLeft;

        public DAGNode(ResourceKey<T> key, T resource) {
            this.key = key;
            this.resource = resource;
        }

    }

    public static <T extends DependentDataDrivenResource> Stream<Pair<ResourceKey<T>, T>> sorted(Registry<T> registry) {
        // Create the nodes
        Map<Identifier, DAGNode<T>> nodesByIdentifier = registry.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey().identifier(), entry -> new DAGNode<>(entry.getKey(), entry.getValue())));
        // Link the nodes
        nodesByIdentifier.values().forEach(node -> {
            Collection<Identifier> requiredResources = node.resource.getRequiredResources();
            node.requiredLeft = requiredResources.size();
            for (Identifier requiredResource : requiredResources) {
                DAGNode<T> requiredResourceNode = nodesByIdentifier.get(requiredResource);
                if (requiredResourceNode.dependents == null) {
                    requiredResourceNode.dependents = new ArrayList<>(1);
                }
                requiredResourceNode.dependents.add(node);
            }
        });
        // List the nodes in a valid order
        List<DAGNode<T>> ready = new ArrayList<>(nodesByIdentifier.size());
        nodesByIdentifier.forEach((_, node) -> {
            if (node.requiredLeft == 0) {
                ready.add(node);
            }
        });
        List<DAGNode<T>> result = new ArrayList<>(nodesByIdentifier.size());
        while (!ready.isEmpty()) {
            DAGNode<T> next = ready.removeLast();
            if (next.dependents != null) {
                next.dependents.forEach(dependent -> {
                    if (--dependent.requiredLeft == 0) {
                        ready.add(dependent);
                    }
                });
            }
            result.add(next);
        }
        // Return the result
        return result.stream().map(node -> Pair.of(node.key, node.resource));
    }

}
