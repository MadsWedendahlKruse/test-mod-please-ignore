package mwk.testmod.common.block.multiblock;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * This block is only used to render a hologram overlay on top of the blocks in a multiblock
 * blueprint
 */
public class HologramBlock extends Block {

    public enum HologramColor implements StringRepresentable {
        CYAN("cyan", new float[] {0, 1, 1}), RED("red", new float[] {1, 0, 0}), GREEN("green",
                new float[] {0, 1, 0});

        private final String name;
        private final float[] color;

        private HologramColor(String name, float[] color) {
            this.name = name;
            this.color = color;
        }

        public String getSerializedName() {
            return this.name.toLowerCase();
        }

        public float[] getFloatColor() {
            return color;
        }
    }

    // State to determine the color
    public static final EnumProperty<HologramColor> COLOR =
            EnumProperty.create("color", HologramColor.class);

    public HologramBlock() {
        super(BlockBehaviour.Properties.of());
        registerDefaultState(getStateDefinition().any().setValue(COLOR, HologramColor.CYAN));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }
}
