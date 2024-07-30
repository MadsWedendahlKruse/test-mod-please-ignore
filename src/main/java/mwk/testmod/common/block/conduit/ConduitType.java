package mwk.testmod.common.block.conduit;

import mwk.testmod.common.block.conduit.network.EnergyConduitNetwork;
import mwk.testmod.common.block.conduit.network.FluidConduitNetwork;
import mwk.testmod.common.block.conduit.network.ItemConduitNetwork;
import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public enum ConduitType {
    ITEM {
        @Override
        public ItemConduitBlockEntity getBlockEntity(BlockPos pos, BlockState state) {
            return new ItemConduitBlockEntity(pos, state);
        }

        @Override
        public ItemConduitNetwork createNetwork() {
            return new ItemConduitNetwork();
        }

        @Override
        public BlockCapability<IItemHandler, Direction> getCapability() {
            return Capabilities.ItemHandler.BLOCK;
        }
    },
    FLUID {
        @Override
        public FluidConduitBlockEntity getBlockEntity(BlockPos pos, BlockState state) {
            return new FluidConduitBlockEntity(pos, state);
        }

        @Override
        public FluidConduitNetwork createNetwork() {
            return new FluidConduitNetwork();
        }

        @Override
        public BlockCapability<IFluidHandler, Direction> getCapability() {
            return Capabilities.FluidHandler.BLOCK;
        }
    },
    ENERGY {
        @Override
        public EnergyConduitBlockEntity getBlockEntity(BlockPos pos, BlockState state) {
            return new EnergyConduitBlockEntity(pos, state);
        }

        @Override
        public EnergyConduitNetwork createNetwork() {
            return new EnergyConduitNetwork();
        }

        @Override
        public BlockCapability<IEnergyStorage, Direction> getCapability() {
            return Capabilities.EnergyStorage.BLOCK;
        }
    };

    public abstract ConduitBlockEntity<?> getBlockEntity(BlockPos pos, BlockState state);

    public abstract ConduitNetwork<?, ?> createNetwork();

    public abstract <T> BlockCapability<T, Direction> getCapability();
}
