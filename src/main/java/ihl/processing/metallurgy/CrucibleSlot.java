package ihl.processing.metallurgy;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CrucibleSlot extends Slot {
	CrucibleInventory inventory;

	public CrucibleSlot(CrucibleInventory arg0, int arg1, int arg2, int arg3) {
		super(arg0, arg1, arg2, arg3);
		inventory = arg0;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return itemstack != null;
	}
}
