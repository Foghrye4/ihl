package ihl.processing.invslots;

import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ihl.utils.IHLUtils;
import net.minecraft.item.ItemStack;

public class InvSlotUpgradeIHL extends InvSlot {

	public InvSlotUpgradeIHL(int count) {
		super(count);
	}

	public InvSlotUpgradeIHL(TileEntityInventory base, int oldStartIndex, Access access, int count, InvSide side) {
		super(base, "invSlotUpgrade", oldStartIndex, access, count, side);
	}

	public double getPowerConsumtionMultiplier() {
		double base = 1d;
		for (int i = 0; i < this.size(); i++) {
			if (IHLUtils.isItemStacksIsEqual(this.get(i), Ic2Items.overclockerUpgrade, false)) {
				int i1 = this.get(i).stackSize;
				while (i1-- > 0 && base < 600) {
					base *= 1.6f;
				}
			}
		}
		return base;
	}

	public float getProgressMultiplier() {
		float base = 1f;
		for (int i = 0; i < this.size(); i++) {
			if (IHLUtils.isItemStacksIsEqual(this.get(i), Ic2Items.overclockerUpgrade, false)) {
				int i1 = this.get(i).stackSize;
				while (i1-- > 0 && base < 600) {
					base *= 1.428571429f;
				}
			}
		}
		return base;
	}

	public int getAdditionalEnergyStorage() {
		int base = 0;
		for (int i = 0; i < this.size(); i++) {
			if (IHLUtils.isItemStacksIsEqual(this.get(i), Ic2Items.energyStorageUpgrade, false)) {
				base += this.get(i).stackSize * 10000;
			}
		}
		return base;
	}

	@Override
	public boolean accepts(ItemStack stack) {
		return IHLUtils.isItemStacksIsEqual(stack, Ic2Items.overclockerUpgrade, false) || 
				IHLUtils.isItemStacksIsEqual(stack, Ic2Items.energyStorageUpgrade, false);
	}

}
