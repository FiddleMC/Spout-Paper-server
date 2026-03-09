package org.fiddlemc.fiddle.impl.moredatadriven.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import java.util.Optional;

public final class BlockDefinition implements Definition {

    public static final Codec<BlockDefinition> CODEC =
        RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.FLOAT.optionalFieldOf("hardness").forGetter(d -> d.hardness),
                Codec.FLOAT.optionalFieldOf("explosion_resistance").forGetter(d -> d.explosionResistance),
                Codec.BOOL.optionalFieldOf("requires_tool").forGetter(d -> d.requiresTool),
                ResourceKey.codec(Registries.SOUND_EVENT).optionalFieldOf("sound").forGetter(d -> d.sound)
            ).apply(instance, BlockDefinition::new)
        );

    public final Optional<Float> hardness;
    public final Optional<Float> explosionResistance;
    public final Optional<Boolean> requiresTool;
    public final Optional<ResourceKey<SoundEvent>> sound;

    public BlockDefinition(
        Optional<Float> hardness,
        Optional<Float> explosionResistance,
        Optional<Boolean> requiresTool,
        Optional<ResourceKey<SoundEvent>> sound
    ) {
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.requiresTool = requiresTool;
        this.sound = sound;
    }

}
