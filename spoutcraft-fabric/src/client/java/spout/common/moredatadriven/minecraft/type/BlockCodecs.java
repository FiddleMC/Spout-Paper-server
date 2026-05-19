package spout.common.moredatadriven.minecraft.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.CoralWallFanBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperStairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import spout.client.fabric.moredatadriven.minecraft.type.BlockBehaviourOffsetTypeProxy;
import spout.client.fabric.moredatadriven.minecraft.type.MapColorProxy;
import spout.client.fabric.moredatadriven.minecraft.type.NoteBlockInstrumentProxy;
import spout.client.fabric.moredatadriven.minecraft.type.PushReactionProxy;
import spout.client.fabric.moredatadriven.minecraft.type.SoundTypeProxy;
import spout.client.fabric.moredatadriven.minecraft.type.mixin.BlockBehaviourPropertiesAccessor;
import spout.common.util.mojang.codec.EnumViaIdentifierCodec;
import spout.common.util.mojang.codec.ProxyCodec;
import spout.common.util.mojang.codec.StaticFieldViaIdentifierCodec;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Holder for codecs related to blocks.
 */
public final class BlockCodecs {

    private static final Codec<MapColor> MAP_COLOR_CODEC = new StaticFieldViaIdentifierCodec<>(MapColor.class);
    private static final Codec<BlockStateFunction<MapColor>> MAP_COLOR_FUNCTION_CODEC = BlockStateFunction.codec(MAP_COLOR_CODEC);
    private static final Codec<SoundType> SOUND_TYPE_CODEC = new StaticFieldViaIdentifierCodec<>(SoundType.class);
    private static final Codec<BlockStateFunction<Integer>> LIGHT_EMISSION_CODEC = BlockStateFunction.codec(Codec.INT);
    private static final Codec<PushReaction> PUSH_REACTION_CODEC = new EnumViaIdentifierCodec<>(PushReaction.class);
    private static final Codec<NoteBlockInstrument> NOTE_BLOCK_INSTRUMENT_CODEC = new EnumViaIdentifierCodec<>(NoteBlockInstrument.class);
    private static final Codec<BlockBehaviour.OffsetType> OFFSET_TYPE_CODEC = new EnumViaIdentifierCodec<>(BlockBehaviour.OffsetType.class);

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

    public static final Codec<BlockBehaviour.Properties> PROPERTIES_CODEC = new Codec<>() {

        @Override
        public <T> DataResult<T> encode(BlockBehaviour.Properties input, DynamicOps<T> ops, T prefix) {
            // No need: only needs to happen on the server
            return null;
        }

        @Override
        public <T> DataResult<Pair<BlockBehaviour.Properties, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).flatMap(mapLike -> {
                BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
                BlockBehaviourPropertiesAccessor accessor = (BlockBehaviourPropertiesAccessor) properties;
                T mapColorInput = mapLike.get("map_color");
                if (mapColorInput != null) {
                    DataResult<BlockStateFunction<MapColor>> mapColor = MAP_COLOR_FUNCTION_CODEC.decode(ops, mapColorInput).map(Pair::getFirst);
                    if (mapColor.isError()) {
                        return mapColor.map($ -> null);
                    }
                    accessor.setMapColor(mapColor.getOrThrow());
                }
                T hasCollisionInput = mapLike.get("has_collision");
                if (hasCollisionInput != null) {
                    DataResult<Boolean> hasCollision = ops.getBooleanValue(hasCollisionInput);
                    if (hasCollision.isError()) {
                        return hasCollision.map($ -> null);
                    }
                    accessor.setHasCollision(hasCollision.getOrThrow());
                }
                T soundTypeInput = mapLike.get("sound_type");
                if (soundTypeInput != null) {
                    DataResult<SoundType> soundType = SOUND_TYPE_CODEC.decode(ops, soundTypeInput).map(Pair::getFirst);
                    if (soundType.isError()) {
                        return soundType.map($ -> null);
                    }
                    accessor.setSoundType(soundType.getOrThrow());
                }
                T lightEmissionInput = mapLike.get("light_emission");
                if (lightEmissionInput != null) {
                    DataResult<BlockStateFunction<Integer>> lightEmission = LIGHT_EMISSION_CODEC.decode(ops, lightEmissionInput).map(Pair::getFirst);
                    if (lightEmission.isError()) {
                        return lightEmission.map($ -> null);
                    }
                    BlockStateFunction<Integer> lightEmissionFunction = lightEmission.getOrThrow();
                    accessor.setLightEmission(lightEmissionFunction::apply);
                }
                T explosionResistanceInput = mapLike.get("explosion_resistance");
                if (explosionResistanceInput != null) {
                    DataResult<Number> explosionResistance = ops.getNumberValue(explosionResistanceInput);
                    if (explosionResistance.isError()) {
                        return explosionResistance.map($ -> null);
                    }
                    accessor.setExplosionResistance(explosionResistance.getOrThrow().floatValue());
                }
                T destroyTimeInput = mapLike.get("destroy_time");
                if (destroyTimeInput != null) {
                    DataResult<Number> destroyTime = ops.getNumberValue(destroyTimeInput);
                    if (destroyTime.isError()) {
                        return destroyTime.map($ -> null);
                    }
                    accessor.setDestroyTime(destroyTime.getOrThrow().floatValue());
                }
                T requiresCorrectToolForDropsInput = mapLike.get("requires_correct_tool_for_drops");
                if (requiresCorrectToolForDropsInput != null) {
                    DataResult<Boolean> requiresCorrectToolForDrops = ops.getBooleanValue(requiresCorrectToolForDropsInput);
                    if (requiresCorrectToolForDrops.isError()) {
                        return requiresCorrectToolForDrops.map($ -> null);
                    }
                    accessor.setRequiresCorrectToolForDrops(requiresCorrectToolForDrops.getOrThrow());
                }
                T isRandomlyTickingInput = mapLike.get("is_randomly_ticking");
                if (isRandomlyTickingInput != null) {
                    DataResult<Boolean> isRandomlyTicking = ops.getBooleanValue(isRandomlyTickingInput);
                    if (isRandomlyTicking.isError()) {
                        return isRandomlyTicking.map($ -> null);
                    }
                    accessor.setIsRandomlyTicking(isRandomlyTicking.getOrThrow());
                }
                T frictionInput = mapLike.get("friction");
                if (frictionInput != null) {
                    DataResult<Number> friction = ops.getNumberValue(frictionInput);
                    if (friction.isError()) {
                        return friction.map($ -> null);
                    }
                    accessor.setFriction(friction.getOrThrow().floatValue());
                }
                T speedFactorInput = mapLike.get("speed_factor");
                if (speedFactorInput != null) {
                    DataResult<Number> speedFactor = ops.getNumberValue(speedFactorInput);
                    if (speedFactor.isError()) {
                        return speedFactor.map($ -> null);
                    }
                    accessor.setSpeedFactor(speedFactor.getOrThrow().floatValue());
                }
                T jumpFactorInput = mapLike.get("jump_factor");
                if (jumpFactorInput != null) {
                    DataResult<Number> jumpFactor = ops.getNumberValue(jumpFactorInput);
                    if (jumpFactor.isError()) {
                        return jumpFactor.map($ -> null);
                    }
                    accessor.setJumpFactor(jumpFactor.getOrThrow().floatValue());
                }
                T idInput = mapLike.get("id");
                if (idInput != null) {
                    DataResult<Identifier> id = Identifier.CODEC.decode(ops, idInput).map(Pair::getFirst);
                    if (id.isError()) {
                        return id.map($ -> null);
                    }
                    accessor.setId(ResourceKey.create(BuiltInRegistries.BLOCK.key(), id.getOrThrow()));
                }
                T canOccludeInput = mapLike.get("can_occlude");
                if (canOccludeInput != null) {
                    DataResult<Boolean> canOcclude = ops.getBooleanValue(canOccludeInput);
                    if (canOcclude.isError()) {
                        return canOcclude.map($ -> null);
                    }
                    accessor.setCanOcclude(canOcclude.getOrThrow());
                }
                T isAirInput = mapLike.get("is_air");
                if (isAirInput != null) {
                    DataResult<Boolean> isAir = ops.getBooleanValue(isAirInput);
                    if (isAir.isError()) {
                        return isAir.map($ -> null);
                    }
                    accessor.setIsAir(isAir.getOrThrow());
                }
                T ignitedByLavaInput = mapLike.get("ignited_by_lava");
                if (ignitedByLavaInput != null) {
                    DataResult<Boolean> ignitedByLava = ops.getBooleanValue(ignitedByLavaInput);
                    if (ignitedByLava.isError()) {
                        return ignitedByLava.map($ -> null);
                    }
                    accessor.setIgnitedByLava(ignitedByLava.getOrThrow());
                }
                T liquidInput = mapLike.get("liquid");
                if (liquidInput != null) {
                    DataResult<Boolean> liquid = ops.getBooleanValue(liquidInput);
                    if (liquid.isError()) {
                        return liquid.map($ -> null);
                    }
                    accessor.setLiquid(liquid.getOrThrow());
                }
                T forceSolidOffInput = mapLike.get("force_solid_off");
                if (forceSolidOffInput != null) {
                    DataResult<Boolean> forceSolidOff = ops.getBooleanValue(forceSolidOffInput);
                    if (forceSolidOff.isError()) {
                        return forceSolidOff.map($ -> null);
                    }
                    accessor.setForceSolidOff(forceSolidOff.getOrThrow());
                }
                T forceSolidOnInput = mapLike.get("force_solid_on");
                if (forceSolidOnInput != null) {
                    DataResult<Boolean> forceSolidOn = ops.getBooleanValue(forceSolidOnInput);
                    if (forceSolidOn.isError()) {
                        return forceSolidOn.map($ -> null);
                    }
                    accessor.setForceSolidOn(forceSolidOn.getOrThrow());
                }
                T pushReactionInput = mapLike.get("push_reaction");
                if (pushReactionInput != null) {
                    DataResult<PushReaction> pushReaction = PUSH_REACTION_CODEC.decode(ops, pushReactionInput).map(Pair::getFirst);
                    if (pushReaction.isError()) {
                        return pushReaction.map($ -> null);
                    }
                    accessor.setPushReaction(pushReaction.getOrThrow());
                }
                T spawnTerrainParticlesInput = mapLike.get("spawn_terrain_particles");
                if (spawnTerrainParticlesInput != null) {
                    DataResult<Boolean> spawnTerrainParticles = ops.getBooleanValue(spawnTerrainParticlesInput);
                    if (spawnTerrainParticles.isError()) {
                        return spawnTerrainParticles.map($ -> null);
                    }
                    accessor.setSpawnTerrainParticles(spawnTerrainParticles.getOrThrow());
                }
                T instrumentInput = mapLike.get("instrument");
                if (instrumentInput != null) {
                    DataResult<NoteBlockInstrument> instrument = NOTE_BLOCK_INSTRUMENT_CODEC.decode(ops, instrumentInput).map(Pair::getFirst);
                    if (instrument.isError()) {
                        return instrument.map($ -> null);
                    }
                    accessor.setInstrument(instrument.getOrThrow());
                }
                T replaceableInput = mapLike.get("replaceable");
                if (replaceableInput != null) {
                    DataResult<Boolean> replaceable = ops.getBooleanValue(replaceableInput);
                    if (replaceable.isError()) {
                        return replaceable.map($ -> null);
                    }
                    accessor.setReplaceable(replaceable.getOrThrow());
                }
                T isRedstoneConductorInput = mapLike.get("is_redstone_conductor");
                if (isRedstoneConductorInput != null) {
                    DataResult<KnownStatePredicate> isRedstoneConductor = KnownStatePredicate.CODEC.decode(ops, isRedstoneConductorInput).map(Pair::getFirst);
                    if (isRedstoneConductor.isError()) {
                        return isRedstoneConductor.map($ -> null);
                    }
                    accessor.setIsRedstoneConductor(isRedstoneConductor.getOrThrow());
                }
                T isSuffocatingInput = mapLike.get("is_suffocating");
                if (isSuffocatingInput != null) {
                    DataResult<KnownStatePredicate> isSuffocating = KnownStatePredicate.CODEC.decode(ops, isSuffocatingInput).map(Pair::getFirst);
                    if (isSuffocating.isError()) {
                        return isSuffocating.map($ -> null);
                    }
                    accessor.setIsSuffocating(isSuffocating.getOrThrow());
                }
                T isViewBlockingInput = mapLike.get("is_view_blocking");
                if (isViewBlockingInput != null) {
                    DataResult<KnownStatePredicate> isViewBlocking = KnownStatePredicate.CODEC.decode(ops, isViewBlockingInput).map(Pair::getFirst);
                    if (isViewBlocking.isError()) {
                        return isViewBlocking.map($ -> null);
                    }
                    accessor.setIsViewBlocking(isViewBlocking.getOrThrow());
                }
                T postProcessInput = mapLike.get("post_process");
                if (postProcessInput != null) {
                    DataResult<BlockBehaviour.PostProcess> postProcess = POST_PROCESS_CODEC.decode(ops, postProcessInput).map(Pair::getFirst);
                    if (postProcess.isError()) {
                        return postProcess.map($ -> null);
                    }
                    accessor.setPostProcess(postProcess.getOrThrow());
                }
                T emissiveRenderingInput = mapLike.get("emissive_rendering");
                if (emissiveRenderingInput != null) {
                    DataResult<KnownStatePredicate> emissiveRendering = KnownStatePredicate.CODEC.decode(ops, emissiveRenderingInput).map(Pair::getFirst);
                    if (emissiveRendering.isError()) {
                        return emissiveRendering.map($ -> null);
                    }
                    accessor.setEmissiveRendering(emissiveRendering.getOrThrow());
                }
                T dynamicShapeInput = mapLike.get("dynamic_shape");
                if (dynamicShapeInput != null) {
                    DataResult<Boolean> dynamicShape = ops.getBooleanValue(dynamicShapeInput);
                    if (dynamicShape.isError()) {
                        return dynamicShape.map($ -> null);
                    }
                    accessor.setDynamicShape(dynamicShape.getOrThrow());
                }
                T requiredFeaturesInput = mapLike.get("required_features");
                if (requiredFeaturesInput != null) {
                    DataResult<FeatureFlagSet> requiredFeatures = FeatureFlagCodecs.FEATURE_FLAG_SET_CODEC.decode(ops, requiredFeaturesInput).map(Pair::getFirst);
                    if (requiredFeatures.isError()) {
                        return requiredFeatures.map($ -> null);
                    }
                    accessor.setRequiredFeatures(requiredFeatures.getOrThrow());
                }
                T offsetFunctionInput = mapLike.get("offset_function");
                if (offsetFunctionInput != null) {
                    DataResult<BlockBehaviour.OffsetType> offsetFunction = OFFSET_TYPE_CODEC.decode(ops, offsetFunctionInput).map(Pair::getFirst);
                    if (offsetFunction.isError()) {
                        return offsetFunction.map($ -> null);
                    }
                    properties.offsetType(offsetFunction.getOrThrow());
                }
                return DataResult.success(Pair.of(properties, input));
            });
        }

    };

    private static <B extends Block> App<RecordCodecBuilder.Mu<B>, BlockBehaviour.Properties> propertiesCodecApp() {
        return BlockBehaviour.Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties); // BlockBehaviour#propertiesCodec
    }

    private static <B extends Block, T1> MapCodec<B> simpleCodec(
        App<RecordCodecBuilder.Mu<B>, T1> t1,
        BiFunction<T1, BlockBehaviour.Properties, B> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            t1,
            propertiesCodecApp()
        ).apply(instance, factory));
    }

    private static <B extends Block, T1, T2> MapCodec<B> simpleCodec(
        App<RecordCodecBuilder.Mu<B>, T1> t1,
        App<RecordCodecBuilder.Mu<B>, T2> t2,
        Function3<T1, T2, BlockBehaviour.Properties, B> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            t1,
            t2,
            propertiesCodecApp()
        ).apply(instance, factory));
    }

    private static <B extends Block, T1, T2, T3> MapCodec<B> simpleCodec(
        App<RecordCodecBuilder.Mu<B>, T1> t1,
        App<RecordCodecBuilder.Mu<B>, T2> t2,
        App<RecordCodecBuilder.Mu<B>, T3> t3,
        Function4<T1, T2, T3, BlockBehaviour.Properties, B> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            t1,
            t2,
            t3,
            propertiesCodecApp()
        ).apply(instance, factory));
    }

    public static <B extends Block> MapCodec<B> simpleCodecWithTemporaryBlockIdentifier(
        App<RecordCodecBuilder.Mu<B>, Identifier> app,
        BiFunction<Block, BlockBehaviour.Properties, B> factory,
        Block placeholder
    ) {
        return simpleCodec(
            app,
            (temporaryValue, properties) -> {
                B block = factory.apply(placeholder /* We replace it later */, properties);
                TemporaryValuesForLazyValues.setBlockIdentifier(block, temporaryValue);
                return block;
            }
        );
    }

    public static <B extends Block> MapCodec<B> simpleCodecWithTemporaryBlockIdentifier(
        App<RecordCodecBuilder.Mu<B>, Identifier> app,
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(app, factory, Blocks.STONE);
    }

    public static <B extends Block> MapCodec<B> simpleCodecWithTemporaryBlockStateString(
        App<RecordCodecBuilder.Mu<B>, String> app,
        BiFunction<BlockState, BlockBehaviour.Properties, B> factory,
        BlockState placeholder
    ) {
        return simpleCodec(
            app,
            (temporaryValue, properties) -> {
                B block = factory.apply(placeholder /* We replace it later */, properties);
                TemporaryValuesForLazyValues.setBlockString(block, temporaryValue);
                return block;
            }
        );
    }

    public static <B extends Block> MapCodec<B> simpleCodecWithTemporaryBlockStateString(
        App<RecordCodecBuilder.Mu<B>, String> app,
        BiFunction<BlockState, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockStateString(app, factory, Blocks.STONE.defaultBlockState());
    }

    public static <B extends BrushableBlock> MapCodec<B> brushableCodec(
        Function4<Block, SoundEvent, SoundEvent, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodec(
            Identifier.CODEC.fieldOf("turns_into").forGetter(brushableBlock -> null /* Only needs to happen on server */),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("brush_sound").forGetter(BrushableBlock::getBrushSound),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("brush_completed_sound").forGetter(BrushableBlock::getBrushCompletedSound),
            (temporaryValue, brushSound, brushCompletedSound, properties) -> {
                B block = factory.apply(Blocks.STONE /* We replace it later */, brushSound, brushCompletedSound, properties);
                TemporaryValuesForLazyValues.setBlockIdentifier(block, temporaryValue);
                return block;
            }
        );
    }

    public static <B extends CandleCakeBlock> MapCodec<B> candleCakeCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(
            Identifier.CODEC.fieldOf("candle").forGetter(candleCakeBlock -> null /* Only needs to happen on server */),
            factory,
            Blocks.WHITE_CANDLE
        );
    }

    public static <B extends ChorusFlowerBlock> MapCodec<B> chorusFlowerCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(
            Identifier.CODEC.fieldOf("plant").forGetter(chorusFlowerBlock -> null /* Only needs to happen on server */),
            factory
        );
    }

    public static <B extends ConcretePowderBlock> MapCodec<B> concretePowderCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(
            Identifier.CODEC.fieldOf("concrete").forGetter(concretePowderBlock -> null /* Only needs to happen on server */),
            factory
        );
    }

    private static MapCodec<Identifier> coralDeadBlockAppCodec() {
        return Identifier.CODEC.fieldOf("dead");
    }

    public static <B extends Block> MapCodec<B> abstractCoralCodec(
        Function<B, Block> deadBlockGetter,
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(
            coralDeadBlockAppCodec().forGetter(block -> null /* Only needs to happen on server */),
            factory
        );
    }

    public static <B extends CoralBlock> MapCodec<B> coralCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return abstractCoralCodec(null /* Only needs to happen on server */, factory);
    }

    public static <B extends CoralFanBlock> MapCodec<B> coralFanCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return abstractCoralCodec(null /* Only needs to happen on server */, factory);
    }

    public static <B extends CoralPlantBlock> MapCodec<B> coralPlantCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return abstractCoralCodec(null /* Only needs to happen on server */, factory);
    }

    public static <B extends CoralWallFanBlock> MapCodec<B> coralWallFanCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return abstractCoralCodec(null /* Only needs to happen on server */, factory);
    }

    public static <B extends FlowerPotBlock> MapCodec<B> flowerPotCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(
            Identifier.CODEC.fieldOf("potted").forGetter(flowerPotBlock -> null /* Only needs to happen on server */),
            factory
        );
    }

    public static <B extends InfestedBlock> MapCodec<B> infestedCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(
            Identifier.CODEC.fieldOf("host").forGetter(infestedBlock -> null /* Only needs to happen on server */),
            factory
        );
    }

    private static <B extends StairBlock> App<RecordCodecBuilder.Mu<B>, String> stairBaseStateApp() {
        return Codec.STRING.fieldOf("base_state").forGetter(stairBlock -> null /* Only needs to happen on server */);
    }

    public static <B extends StairBlock> MapCodec<B> stairCodec(
        BiFunction<BlockState, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockStateString(
            stairBaseStateApp(),
            factory
        );
    }

    public static <B extends WeatheringCopperStairBlock> MapCodec<B> weatheringCopperStairCodec(
        Function3<WeatheringCopper.WeatherState, BlockState, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodec(
            WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringCopperStairBlock::getAge),
            stairBaseStateApp(),
            (weatherState, temporaryValue, properties) -> {
                B block = factory.apply(weatherState, Blocks.STONE.defaultBlockState() /* We replace it later */, properties);
                TemporaryValuesForLazyValues.setBlockString(block, temporaryValue);
                return block;
            }
        );
    }

}
