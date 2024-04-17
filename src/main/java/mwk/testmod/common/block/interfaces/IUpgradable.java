package mwk.testmod.common.block.interfaces;

import mwk.testmod.common.item.upgrades.base.UpgradeItem;

public interface IUpgradable {

    boolean isUpgradeValid(UpgradeItem upgrade);

    void applyUpgrades();
}
