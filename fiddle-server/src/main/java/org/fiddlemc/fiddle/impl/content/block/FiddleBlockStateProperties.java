package org.fiddlemc.fiddle.impl.content.block;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Additional block state properties that are not in {@link BlockStateProperties}.
 */
public final class FiddleBlockStateProperties {

    private FiddleBlockStateProperties() {
        throw new UnsupportedOperationException();
    }

    public static final BooleanProperty WEST_DOWN_NORTH = BooleanProperty.create("west_down_north");
    public static final BooleanProperty WEST_DOWN_SOUTH = BooleanProperty.create("west_down_south");
    public static final BooleanProperty WEST_UP_NORTH = BooleanProperty.create("west_up_north");
    public static final BooleanProperty WEST_UP_SOUTH = BooleanProperty.create("west_up_south");
    public static final BooleanProperty EAST_DOWN_NORTH = BooleanProperty.create("east_down_north");
    public static final BooleanProperty EAST_DOWN_SOUTH = BooleanProperty.create("east_down_south");
    public static final BooleanProperty EAST_UP_NORTH = BooleanProperty.create("east_up_north");
    public static final BooleanProperty EAST_UP_SOUTH = BooleanProperty.create("east_up_south");

}
