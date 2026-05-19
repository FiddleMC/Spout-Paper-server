package spout.common.moredatadriven.minecraft.common.subtypes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import spout.common.moredatadriven.minecraft.type.BlockStateFunction;
import spout.common.util.mojang.codec.EnumViaIdentifierCodec;
import spout.common.util.mojang.codec.StaticFieldViaIdentifierCodec;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Holder for codecs of subtypes.
 */
public final class SubtypeCodecs {

    private SubtypeCodecs() {
        throw new UnsupportedOperationException();
    }

    public static final Codec<MapColor> MAP_COLOR_CODEC = new StaticFieldViaIdentifierCodec<>(MapColor.class);
    public static final Codec<BlockStateFunction<MapColor>> MAP_COLOR_FUNCTION_CODEC = BlockStateFunction.codec(MAP_COLOR_CODEC);
    public static final Codec<SoundType> SOUND_TYPE_CODEC = new StaticFieldViaIdentifierCodec<>(SoundType.class);
    public static final Codec<BlockStateFunction<Integer>> LIGHT_EMISSION_CODEC = BlockStateFunction.codec(Codec.INT);
    public static final Codec<PushReaction> PUSH_REACTION_CODEC = new EnumViaIdentifierCodec<>(PushReaction.class);
    public static final Codec<NoteBlockInstrument> NOTE_BLOCK_INSTRUMENT_CODEC = new EnumViaIdentifierCodec<>(NoteBlockInstrument.class);
    public static final Codec<BlockBehaviour.OffsetType> OFFSET_TYPE_CODEC = new EnumViaIdentifierCodec<>(BlockBehaviour.OffsetType.class);

    public static final Codec<FeatureFlag> FEATURE_FLAG_CODEC = Identifier.CODEC.comapFlatMap(identifier -> {
        FeatureFlag featureFlag = ((FeatureFlagRegistryNamesAccessor) FeatureFlags.REGISTRY).spout$names().get(identifier);
        if (featureFlag != null) {
            return DataResult.success(featureFlag);
        }
        return DataResult.error(() -> "No such feature flag: " + identifier);
    }, featureFlag -> ((FeatureFlagRegistryNamesAccessor) FeatureFlags.REGISTRY).spout$names().entrySet().stream().filter(entry -> entry.getValue().equals(featureFlag)).findAny().get().getKey());

    public static final Codec<FeatureFlagSet> FEATURE_FLAG_SET_CODEC = FEATURE_FLAG_CODEC.listOf().xmap(
        featureFlags -> {
            FeatureFlag[] array = featureFlags.toArray(FeatureFlag[]::new);
            return array.length == 0 ? FeatureFlagSet.of() : array.length == 1 ? FeatureFlagSet.of(array[0]) : FeatureFlagSet.of(array[0], Arrays.copyOfRange(array, 1, array.length));
        },
        featureFlagSet -> ((FeatureFlagRegistryNamesAccessor) FeatureFlags.REGISTRY).spout$names().values().stream().filter(featureFlagSet::contains).toList()
    );

    public static final Codec<BlockBehaviour.PostProcess> POST_PROCESS_CODEC = new Codec<>() {

        @Override
        public <T> DataResult<T> encode(BlockBehaviour.PostProcess input, DynamicOps<T> ops, T prefix) {
            BlockPos output;
            try {
                output = input.getPostProcessPos(null, null, BlockPos.ZERO);
            } catch (Exception e) {
                return DataResult.error(() -> "Not an encodable post process: " + e);
            }
            if (output == null) {
                return DataResult.success(ops.createString("null"));
            }
            return DataResult.success(ops.createIntList(IntStream.of(output.getX(), output.getY(), output.getZ())));
        }

        @Override
        public <T> DataResult<Pair<BlockBehaviour.PostProcess, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<String> stringResult = ops.getStringValue(input);
            if (stringResult.isSuccess() && stringResult.getOrThrow().equals("null")) {
                return DataResult.success(Pair.of((state, level, pos) -> null, input));
            }
            DataResult<IntStream> intStreamResult = ops.getIntStream(input);
            IntStream intStream = intStreamResult.getOrThrow();
            int dx = intStream.findFirst().getAsInt();
            int dy = intStream.findFirst().getAsInt();
            int dz = intStream.findFirst().getAsInt();
            return DataResult.success(Pair.of((state, level, pos) -> pos.offset(dx, dy, dz), input));
        }

    };

}
