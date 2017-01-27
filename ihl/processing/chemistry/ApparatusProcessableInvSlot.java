package ihl.processing.chemistry;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ApparatusProcessableInvSlot extends InvSlot {

	public ApparatusProcessableInvSlot(TileEntityInventory base1, String name1,
			int oldStartIndex1, Access access1, int count, int stackSizeLimit1) {
		super(base1, name1, oldStartIndex1, access1, count);
		this.setStackSizeLimit(stackSizeLimit1);
	}
	
	public List<ItemStack> getItemStackList()
	{
		List<ItemStack> list = new ArrayList<ItemStack>();
		for(int i=0; i<this.size(); i++)
		{
			if(get(i)!=null)
			{
				list.add(get(i));
			}
		}
		return list;
	}
	
	public ItemStack getItemStack(Item item) {
		for(int i=0; i<this.size(); i++)
		{
			if(get(i)!=null && get(i).getItem()==item)
			{
				return get(i);
			}
		}
		return null;
	}

	public void replaceItemStack(ItemStack stack1) 
	{
		for(int i=0; i<this.size(); i++)
		{
			if(this.get(i)!=null && this.get(i).getItem()==stack1.getItem())
			{
				this.put(i,stack1);
			}
		}
	}

	public void consume(int i, int stackSize1) 
	{
		if(this.get(i)==null)return;
		if(this.get(i).stackSize==stackSize1)
		{
			this.put(i, null);
		}
		else
		{
			this.get(i).stackSize-=stackSize1;
		}
	}

}
