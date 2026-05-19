package spout.common.moredatadriven.minecraft.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.RecordBuilder;
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
import spout.common.moredatadriven.minecraft.common.subtypes.SubtypeCodecs;
import spout.common.util.mojang.codec.EnumViaIdentifierCodec;
import spout.common.util.mojang.codec.StaticFieldViaIdentifierCodec;
import spout.server.paper.impl.moredatadriven.minecraft.type.BlockPropertiesWithDefaultsForDataDrivenType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Holder for codecs related to blocks.
 */
public final class BlockCodecs {

    private static <B extends Block, T1> MapCodec<B> simpleCodec(
        App<RecordCodecBuilder.Mu<B>, T1> t1,
        BiFunction<T1, BlockBehaviour.Properties, B> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            t1,
            BlockBehaviour.propertiesCodec()
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
            BlockBehaviour.propertiesCodec()
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
            BlockBehaviour.propertiesCodec()
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
            Identifier.CODEC.fieldOf("turns_into").forGetter(brushableBlock -> brushableBlock.turnsInto.keyInBlockRegistry),
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
            Identifier.CODEC.fieldOf("candle").forGetter(candleCakeBlock -> candleCakeBlock.candleBlock.keyInBlockRegistry),
            factory,
            Blocks.WHITE_CANDLE
        );
    }

    public static <B extends ChorusFlowerBlock> MapCodec<B> chorusFlowerCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(
            Identifier.CODEC.fieldOf("plant").forGetter(chorusFlowerBlock -> chorusFlowerBlock.plant.keyInBlockRegistry),
            factory
        );
    }

    public static <B extends ConcretePowderBlock> MapCodec<B> concretePowderCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(
            Identifier.CODEC.fieldOf("concrete").forGetter(concretePowderBlock -> concretePowderBlock.concrete.keyInBlockRegistry),
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
            coralDeadBlockAppCodec().forGetter(block -> deadBlockGetter.apply(block).keyInBlockRegistry),
            factory
        );
    }

    public static <B extends CoralBlock> MapCodec<B> coralCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return abstractCoralCodec(coralBlock -> coralBlock.deadBlock, factory);
    }

    public static <B extends CoralFanBlock> MapCodec<B> coralFanCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return abstractCoralCodec(coralFanBlock -> coralFanBlock.deadBlock, factory);
    }

    public static <B extends CoralPlantBlock> MapCodec<B> coralPlantCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return abstractCoralCodec(coralPlantBlock -> coralPlantBlock.deadBlock, factory);
    }

    public static <B extends CoralWallFanBlock> MapCodec<B> coralWallFanCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return abstractCoralCodec(coralWallFanBlock -> coralWallFanBlock.deadBlock, factory);
    }

    public static <B extends FlowerPotBlock> MapCodec<B> flowerPotCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(
            Identifier.CODEC.fieldOf("potted").forGetter(flowerPotBlock -> flowerPotBlock.potted.keyInBlockRegistry),
            factory
        );
    }

    public static <B extends InfestedBlock> MapCodec<B> infestedCodec(
        BiFunction<Block, BlockBehaviour.Properties, B> factory
    ) {
        return simpleCodecWithTemporaryBlockIdentifier(
            Identifier.CODEC.fieldOf("host").forGetter(infestedBlock -> infestedBlock.hostBlock.keyInBlockRegistry),
            factory
        );
    }

    private static <B extends StairBlock> App<RecordCodecBuilder.Mu<B>, String> stairBaseStateApp() {
        return Codec.STRING.fieldOf("base_state").forGetter(stairBlock -> BlockStateStringConversion.blockStateToString(stairBlock.baseState));
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
