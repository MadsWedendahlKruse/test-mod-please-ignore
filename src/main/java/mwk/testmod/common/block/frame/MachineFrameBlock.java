package mwk.testmod.common.block.frame;

import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import net.minecraft.world.level.block.SoundType;

/**
 * The basic block from which all machines are built. 
 * Either through crafting or through the multiblock structure.
 */
public class MachineFrameBlock extends MultiBlockPartBlock {

    public MachineFrameBlock(Properties properties) {
        super(properties);
        properties
            .sound(SoundType.METAL)
            // TODO: What do these numbers mean?
            .destroyTime(10.0f)
            .explosionResistance(10.0f);
    }
}
