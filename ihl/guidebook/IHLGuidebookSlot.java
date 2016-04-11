package ihl.guidebook;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class IHLGuidebookSlot extends Slot
{
	IHLGuidebookInventory inventory;
    public IHLGuidebookSlot(IHLGuidebookInventory arg0, int arg1, int arg2, int arg3) 
    {
		super(arg0, arg1, arg2, arg3);
		inventory=arg0;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack)
    {
        return false;
    }
}
