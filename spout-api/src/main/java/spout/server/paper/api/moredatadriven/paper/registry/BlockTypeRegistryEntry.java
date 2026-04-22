package spout.server.paper.api.moredatadriven.paper.registry;

import io.papermc.paper.registry.RegistryBuilder;
import org.bukkit.FeatureFlag;
import org.bukkit.Instrument;
import org.bukkit.SoundGroup;
import org.bukkit.block.BlockType;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.ApiStatus;

/**
 * A data-centric version-specific registry entry for the {@link BlockType} type.
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface BlockTypeRegistryEntry {

    /**
     * A mutable builder for the {@link BlockTypeRegistryEntry},
     * that plugins may change in applicable registry events.
     *
     * <p>
     * Additional functionality is available by casting this instance to {@code BlockRegistryEntryBuilderNMS}.
     * </p>
     */
    @ApiStatus.Experimental
    @ApiStatus.NonExtendable
    interface Builder extends BlockTypeRegistryEntry, RegistryBuilder<BlockType>, KeyAwareRegistryEntry {

        /**
         * Sets the type of block to a regular full-sized block.
         *
         * <p>
         * This is the default, so normally you don't need to call this.
         * </p>
         */
        BlockTypeRegistryEntry.Builder inheritsFromBlock();

        /**
         * Sets the type of block to an anvil.
         */
        BlockTypeRegistryEntry.Builder inheritsFromAnvil();

        /**
         * Sets the type of block to a barrel.
         */
        BlockTypeRegistryEntry.Builder inheritsFromBarrel();

        /**
         * Sets the type of block to a cactus.
         */
        BlockTypeRegistryEntry.Builder inheritsFromCactus();

        /**
         * Sets the type of block to a cactus flower.
         */
        BlockTypeRegistryEntry.Builder inheritsFromCactusFlower();

        /**
         * Sets the type of block to a cake.
         */
        BlockTypeRegistryEntry.Builder inheritsFromCake();

        /**
         * Sets the type of block to a campfire.
         */
        BlockTypeRegistryEntry.Builder inheritsFromCampfire(boolean spawnParticles, int fireDamage);

        /**
         * Sets the type of block to candles.
         */
        BlockTypeRegistryEntry.Builder inheritsFromCandle();

        /**
         * Sets the type of block to a candle cake.
         *
         * @param candleBlock The candle block corresponding to this cake.
         */
        BlockTypeRegistryEntry.Builder inheritsFromCandleCake(BlockType candleBlock);

        /**
         * Sets the type of block to a carpet.
         */
        BlockTypeRegistryEntry.Builder inheritsFromCarpet();

        /**
         * Sets the type of block to a carved pumpkin.
         */
        BlockTypeRegistryEntry.Builder inheritsFromCarvedPumpkin();

        /**
         * Sets the type of block to a chain.
         */
        BlockTypeRegistryEntry.Builder inheritsFromChain();

        /**
         * Sets the type of block to a fire.
         */
        BlockTypeRegistryEntry.Builder inheritsFromFire();

        /**
         * Sets the type of block to a slab.
         */
        BlockTypeRegistryEntry.Builder inheritsFromSlab();

        /**
         * Sets the type of block to a soul fire.
         */
        BlockTypeRegistryEntry.Builder inheritsFromSoulFire();

        /**
         * Sets the type of block to stairs.
         *
         * @param baseState The default block state of the full block
         *                  corresponding to these stairs.
         */
        BlockTypeRegistryEntry.Builder inheritsFromStairs(BlockData baseState);

        /**
         * Convenience function that calls {@link #inheritsFromStairs}
         * with the default state of the given {@link BlockType}.
         */
        default BlockTypeRegistryEntry.Builder inheritsFromStairs(BlockType baseType) {
            return this.inheritsFromStairs(baseType.createBlockData());
        }

        // Missing: a lot

        /**
         * Sets the map color of this block to the map color of the given block.
         */
        BlockTypeRegistryEntry.Builder mapColor(BlockType referenceBlockType);

        /**
         * Disables collision and occlusion for this block.
         */
        BlockTypeRegistryEntry.Builder noCollision();

        /**
         * Disables occlusion for this block.
         */
        BlockTypeRegistryEntry.Builder noOcclusion();

        /**
         * Sets the friction (slipperiness) of this block.
         *
         * <p>
         * This is used in vanilla to make ice slippery.
         * </p>
         */
        BlockTypeRegistryEntry.Builder friction(float friction);

        /**
         * Sets the speed factor (standard walking speed) of this block.
         *
         * <p>
         * This is used in vanilla to make soul sand slower to walk across.
         * </p>
         */
        BlockTypeRegistryEntry.Builder speedFactor(float speedFactor);

        /**
         * Sets the jump factor of this block.
         *
         * <p>
         * This is used in vanilla to reduce the height that players can jump when on top of a honey block.
         * </p>
         */
        BlockTypeRegistryEntry.Builder jumpFactor(float jumpFactor);

        /**
         * Sets the sounds this blocks makes.
         */
        BlockTypeRegistryEntry.Builder sound(SoundGroup sound);

        /**
         * Convenience function that calls {@link #sound(SoundGroup)} with the sound group
         * of the given {@link BlockData}.
         */
        default BlockTypeRegistryEntry.Builder sound(BlockData blockData) {
            return this.sound(blockData.getSoundGroup());
        }

        /**
         * Convenience function that calls {@link #sound(BlockData)} with the default block state
         * of the given {@link BlockType}.
         */
        default BlockTypeRegistryEntry.Builder sound(BlockType blockType) {
            return this.sound(blockType.createBlockData());
        }

        /**
         * Sets the light level for this block.
         */
        BlockTypeRegistryEntry.Builder lightLevel(int lightEmission);

        // Temporarily removed due to stricter format of light emission for serialization
        // /**
        //  * Sets the light level function for this block.
        //  * This function is used to determine the light level emitted by each state of this block.
        //  *
        //  * @param propertyDependencies The block state properties that the given {@code lightEmission}
        //  *                             function may depend on. For example, the list must contain {@code "berries"}
        //  *                             if you want to check whether a cave vines block has any berries.
        //  */
        // BlockRegistryEntry.Builder lightLevel(List<String> propertyDependencies, ToIntFunction<Map<String, String>> lightEmission);

        /**
         * Convenience function that calls {@link #destroyTime(float)} and {@link #explosionResistance(float)}.
         */
        BlockTypeRegistryEntry.Builder strength(float destroyTime, float explosionResistance);

        /**
         * Convenience function that calls {@link #strength}{@code (0)}.
         */
        BlockTypeRegistryEntry.Builder instabreak();

        /**
         * Convenience function that calls {@link #destroyTime(float)} and {@link #explosionResistance(float)}
         * with the same value.
         */
        BlockTypeRegistryEntry.Builder strength(float strength);

        /**
         * Makes it so that blocks of this type will be randomly ticked.
         */
        BlockTypeRegistryEntry.Builder randomTicks();

        /**
         * Marks this block as having a dynamic collision shape.
         *
         * <p>
         * This must be called for any block where there is at least one block state that doesn't
         * have a constant collision bounding box. While most block states' collision bounding boxes
         * are static and do not depend on anything else, there are few exceptions, such as moving pistons
         * and shulker boxes.
         * </p>
         */
        BlockTypeRegistryEntry.Builder dynamicShape();

        // Missing: noLootTable, overrideLootTable

        /**
         * Makes this block ignitable by lava.
         */
        BlockTypeRegistryEntry.Builder ignitedByLava();

        /**
         * Makes this block behave like liquid.
         */
        BlockTypeRegistryEntry.Builder liquid();

        // Missing: forceSolidOn, forceSolidOff - but they seem to be no longer have any effect so should probably not be added here

        /**
         * Sets the behavior when the block is pushed by a piston.
         */
        BlockTypeRegistryEntry.Builder pushReaction(PistonMoveReaction pushReaction);

        /**
         * Marks this block as air.
         *
         * <p>
         * While it is technically possible to add new types of air blocks this way,
         * this is not recommended due to the special way air blocks are treated
         * by various parts of the code.
         * </p>
         */
        BlockTypeRegistryEntry.Builder air();

        // Missing: isValidSpawn, isRedstoneConductor, isSuffocating, isViewBlocking, hasPostProcess, emissiveRendering

        /**
         * Sets that this block requires the correct tool to drop the drops defined in its loot table.
         *
         * <p>
         * Which tool is a correct tool is identified by the tags associated with this block,
         * for example {@code #minecraft:minable/shovel}.
         * </p>
         */
        BlockTypeRegistryEntry.Builder requiresCorrectToolForDrops();

        /**
         * Sets the time a player needs to break this block.
         */
        BlockTypeRegistryEntry.Builder destroyTime(float destroyTime);

        /**
         * Sets the resistance of this block to explosions.
         */
        BlockTypeRegistryEntry.Builder explosionResistance(float explosionResistance);

        /**
         * Sets the way this block is randomly offset within its coordinates.
         *
         * <p>
         * This is used to place grass, which is smaller than a full block,
         * at some slightly varying offset that is different for each block.
         * </p>
         */
        BlockTypeRegistryEntry.Builder offsetType(OffsetType offsetType);

        /**
         * Makes this block not spawn terrain particles when walking across.
         *
         * <p>
         * In vanilla, this is strictly only used for blocks where the associated texture does not correspond
         * to their physical form at all, such as barriers.
         * </p>
         */
        BlockTypeRegistryEntry.Builder noTerrainParticles();

        /**
         * Marks this block as requiring the given feature flags.
         */
        BlockTypeRegistryEntry.Builder requiredFeatures(FeatureFlag... requiredFeatures);

        /**
         * Sets the sound a note block placed above this block makes.
         */
        BlockTypeRegistryEntry.Builder instrument(Instrument instrument);

        /**
         * Makes this block instantly replaceable, in other words,
         * any block placed on them will replace them instead of being placed against them.
         *
         * <p>
         * Amongst others, this is used for air, liquids and grass.
         * </p>
         */
        BlockTypeRegistryEntry.Builder replaceable();

        // Missing: overrideDescription

    }

    /**
     * A type of offset that can be applied with {@link Builder#offsetType}.
     *
     * <p>
     * Corresponds to {@code net.minecraft.world.level.block.state.BlockBehaviour.OffsetType}.
     * </p>
     */
    enum OffsetType {
        /**
         * No offset.
         */
        NONE,
        /**
         * Horizontal offset only.
         */
        XZ,
        /**
         * Horizontal and vertical offset.
         */
        XYZ
    }

}
