package mwk.testmod.common.block.conduit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public enum ConduitType {
    ITEM {
        @Override
        public BlockEntity getBlockEntity(BlockPos pos, BlockState state) {
            return new ItemConduitBlockEntity(pos, state);
        }

        @Override
        public BlockCapability<IItemHandler, Direction> getCapability() {
            return Capabilities.ItemHandler.BLOCK;
        }
    },
    FLUID {
        @Override
        public BlockEntity getBlockEntity(BlockPos pos, BlockState state) {
            return new FluidConduitBlockEntity(pos, state);
        }

        @Override
        public BlockCapability<IFluidHandler, Direction> getCapability() {
            return Capabilities.FluidHandler.BLOCK;
        }
    },
    ENERGY {
        @Override
        public BlockEntity getBlockEntity(BlockPos pos, BlockState state) {
            return new EnergyConduitBlockEntity(pos, state);
        }

        @Override
        public BlockCapability<IEnergyStorage, Direction> getCapability() {
            return Capabilities.EnergyStorage.BLOCK;
        }
    };

    public abstract BlockEntity getBlockEntity(BlockPos pos, BlockState state);

    public abstract <T> BlockCapability<T, Direction> getCapability();
}
