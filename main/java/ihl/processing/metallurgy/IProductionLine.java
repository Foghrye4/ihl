package ihl.processing.metallurgy;

import net.minecraft.item.ItemStack;

public interface IProductionLine 
{

	short getFacing();
	boolean canProcess(ItemStack stack);
	void process(ItemStack stack);

}
