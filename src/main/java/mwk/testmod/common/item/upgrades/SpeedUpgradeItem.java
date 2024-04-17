package mwk.testmod.common.item.upgrades;

import mwk.testmod.common.item.upgrades.base.UpgradeItem;

public class SpeedUpgradeItem extends UpgradeItem {

    private final float speedMultiplier;
    private final float energyMultiplier;

    public SpeedUpgradeItem(Properties properties, float speedMultiplier, float energyMultiplier) {
        super(properties);
        this.speedMultiplier = speedMultiplier;
        this.energyMultiplier = energyMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getEnergyMultiplier() {
        return energyMultiplier;
    }
}
