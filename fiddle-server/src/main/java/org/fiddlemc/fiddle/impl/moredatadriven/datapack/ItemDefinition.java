package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public final class ItemDefinition implements Definition {

    public static final Codec<ItemDefinition> CODEC =
        RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.optionalFieldOf("max_stack_size").forGetter(d -> d.maxStackSize),
                Codec.BOOL.optionalFieldOf("fire_resistant").forGetter(d -> d.fireResistant)
            ).apply(instance, ItemDefinition::new)
        );

    public final Optional<Integer> maxStackSize;
    public final Optional<Boolean> fireResistant;

    public ItemDefinition(
        Optional<Integer> maxStackSize,
        Optional<Boolean> fireResistant
    ) {
        this.maxStackSize = maxStackSize;
        this.fireResistant = fireResistant;
    }

}
