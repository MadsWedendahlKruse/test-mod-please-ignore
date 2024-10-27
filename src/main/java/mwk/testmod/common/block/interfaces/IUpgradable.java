package mwk.testmod.common.block.interfaces;

import mwk.testmod.common.item.upgrades.base.UpgradeItem;

public interface IUpgradable {

    /**
     * Checks if the upgrade is valid for this machine.
     *
     * @param upgrade The upgrade to check.
     * @return True if the upgrade is valid, false otherwise.
     */
    boolean isUpgradeValid(UpgradeItem upgrade);

    /**
     * Installs the upgrade into the machine modifying its properties e.g. speed, energy
     * consumption, etc.
     *
     * @param upgrade The upgrade to install.
     */
    void installUpgrade(UpgradeItem upgrade);

    /**
     * Resets the machine's properties to their default values.
     */
    void resetUpgrades();
}
