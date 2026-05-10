package spout.server.paper.impl.moredatadriven.minecraft.type;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A utility class to create {@link BlockBehaviour.Properties}
 * instances that have defaults applied to them,
 * if being created for a data-driven block.
 */
public final class BlockPropertiesWithDefaultsForDataDrivenType {

    /**
     * The initializers per type,
     * or null if not initialized yet.
     */
    private static @Nullable Map<Identifier, Consumer<BlockBehaviour.Properties>> initializers;

    private static void addInitializer(String key, Consumer<BlockBehaviour.Properties> value) {
        addInitializer(Identifier.parse(key), value);
    }

    private static void addInitializer(Identifier key, Consumer<BlockBehaviour.Properties> value) {
        initializers.put(key, value);
    }

    private static @Nullable Consumer<BlockBehaviour.Properties> getInitializer(String type) {
        return getInitializer(Identifier.parse(type));
    }

    private static @Nullable Consumer<BlockBehaviour.Properties> getInitializer(Identifier type) {
        if (initializers == null) {
            initializers = new HashMap<>();
            addInitializer("base_pressure_plate", properties -> {
                properties.noCollision();
                properties.strength(0.5f);
                properties.forceSolidOn();
                properties.pushReaction(PushReaction.DESTROY);
            });
            addInitializer("button", properties -> {
                properties.noCollision();
                properties.strength(0.5f);
                properties.pushReaction(PushReaction.DESTROY);
            });
            addInitializer("door", properties -> {
                properties.noOcclusion();
                properties.pushReaction(PushReaction.DESTROY);
            });
            addInitializer("fence_gate", properties -> {
                properties.forceSolidOn();
            });
            addInitializer("ladder", properties -> {
                properties.sound(SoundType.LADDER);
                properties.strength(0.4f);
                properties.noOcclusion();
                properties.forceSolidOff();
            });
            addInitializer("pressure_plate", properties -> {
                getInitializer("base_pressure_plate").accept(properties);
            });
            addInitializer("trapdoor", properties -> {
                properties.noOcclusion();
                properties.isValidSpawn(Blocks::never);
            });
            addInitializer("weathering_copper_door", properties -> {
                getInitializer("door").accept(properties);
                properties.mapColor(MapColor.COLOR_ORANGE);
                properties.strength(3.0f, 6.0f);
                properties.requiresCorrectToolForDrops();
            });
            addInitializer("weathering_copper_trap_door", properties -> {
                getInitializer("trapdoor").accept(properties);
                properties.mapColor(MapColor.COLOR_ORANGE);
                properties.strength(3.0f, 6.0f);
                properties.requiresCorrectToolForDrops();
            });
            addInitializer("weighted_pressure_plate", properties -> {
                getInitializer("base_pressure_plate").accept(properties);
            });
        }
        return initializers.get(type);
    }

    /**
     * A thread local of the internal codec of the data-driven block type
     * that properties are currently being decoded for.
     */
    private static final ThreadLocal<@Nullable Identifier> decodingForType = ThreadLocal.withInitial(() -> null);

    /**
     * Sets the current data-driven type for which decoding is being done.
     */
    public static void setDecodingForType(Identifier type) {
        decodingForType.set(type);
    }

    /**
     * Clears the type that was last set with {@link #setDecodingForType}.
     */
    public static void clearDecodingForType() {
        decodingForType.remove();
    }

    /**
     * @return A new {@link BlockBehaviour.Properties} instance,
     * potentially with data-driven block type defaults applied.
     */
    public static BlockBehaviour.Properties create() {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
        @Nullable Identifier type = decodingForType.get();
        if (type != null) {
            @Nullable Consumer<BlockBehaviour.Properties> initializer = getInitializer(type);
            if (initializer != null) {
                initializer.accept(properties);
            }
        }
        return properties;
    }

}
