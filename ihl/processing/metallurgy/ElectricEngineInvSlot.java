package ihl.processing.metallurgy;

import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.item.ItemStack;

public class ElectricEngineInvSlot extends InvSlot {

	public ElectricEngineInvSlot(TileEntityInventory base1, String name1,
			int oldStartIndex1, Access access1, int count, int stackSizeLimit1) {
		super(base1, name1, oldStartIndex1, access1, count);
		this.setStackSizeLimit(stackSizeLimit1);
	}
	
	public float getEfficiency()
	{
		if(this.isEmpty())
		{
			return 0F;
		}
		else if(this.get().getItem() instanceof ElectricEngineItem)
		{
			return ((ElectricEngineItem)this.get().getItem()).type.efficiency;
		}
		else if(this.get().getItem() == Ic2Items.elemotor.getItem() && this.get().getItemDamage() == Ic2Items.elemotor.getItemDamage())
		{
			return 0.2f;
		}
		else
		{
			return 0f;
		}
	}
	
	@Override
	public boolean accepts(ItemStack stack)
	{
		if(stack!=null && stack.getItem() == Ic2Items.elemotor.getItem() && stack.getItemDamage() == Ic2Items.elemotor.getItemDamage())
		{
			return true;
		}
		else
		{
			return stack==null?true:stack.getItem() instanceof ElectricEngineItem;
		}
	}
	
	public boolean correctContent()
	{
		return this.get()==null?false:this.get().stackSize==1 && ((this.get().getItem() instanceof ElectricEngineItem) || (this.get().getItem() == Ic2Items.elemotor.getItem() && this.get().getItemDamage() == Ic2Items.elemotor.getItemDamage()));
	}

}
