package mwk.testmod.common.capabilities;

public interface ITemporalFluxHandler {

    public boolean canReceive();

    public int receiveTemporalFlux(int amount, boolean simulate);

    public boolean canExtract();

    public int extractTemporalFlux(int amount, boolean simulate);

    public int getTemporalFluxStored();

    public int getMaxTemporalFluxStored();
}
