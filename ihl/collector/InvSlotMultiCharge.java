package ihl.collector;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;

public class InvSlotMultiCharge extends InvSlot {
	    public int tier;

	    public InvSlotMultiCharge(TileEntityInventory base1, int oldStartIndex1, int tier1, int count)
	    {
	        super(base1, "charge", oldStartIndex1, InvSlot.Access.IO, count, InvSlot.InvSide.TOP);
	        this.tier = tier1;
	    }

	    @Override
		public boolean accepts(ItemStack itemStack)
	    {
	        Item item = itemStack.getItem();
	        return item instanceof IElectricItem ? ((IElectricItem)item).getTier(itemStack) <= this.tier : false;
	    }

	    public IElectricItem getItem(int slotNum)
	    {
	        ItemStack itemStack = this.get(slotNum);
	        return itemStack == null ? null : (IElectricItem)itemStack.getItem();
	    }

	    public double charge(int amount, int slotNum)
	    {
	        ItemStack itemStack = this.get(slotNum);

	        if (itemStack == null)
	        {
	            return 0;
	        }
	        else
	        {
	            Item item = itemStack.getItem();
	            return item instanceof IElectricItem ? ElectricItem.manager.charge(itemStack, amount, this.tier, false, false) : 0;
	        }
	    }

	    public void setTier(int tier1)
	    {
	        this.tier = tier1;
	    }
	    
	    public boolean addItemStackToFirstEmptyStack(ItemStack stack)
	    {
	    	int slotNum = this.getFirstEmptyStack();
	    	if(slotNum<0 || slotNum>=this.size())
	    	{
	    		return false;
	    	}
	    	else
	    	{
	    		if(this.accepts(stack))
	    		{
	    			this.put(slotNum, stack);
	    			return true;
	    		}
	    		else
	    		{
	    			return false;
	    		}
	    	}
	    }
	    
	    @Override
	    public void put(ItemStack stack)
	    {
	    	this.addItemStackToFirstEmptyStack(stack);
	    }
	    
	    public int getFirstEmptyStack()
	    {
	    	for(int i=0;i<this.size();i++)
	    	{
	    		if(this.get(i)==null)
	    		{
	    			return i;
	    		}
	    	}
	    	return -1;
	    }
	    
	    @Override
	    public boolean isEmpty()
	    {
	    	return this.getFirstEmptyStack()>=0;
	    }
}
