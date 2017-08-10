package ihl.interfaces;

import net.minecraft.item.ItemStack;

public interface IWire {
	public String getTag();
	public String getTagSecondary();
	public boolean isSameWire(ItemStack stack1, ItemStack stack2);
}
