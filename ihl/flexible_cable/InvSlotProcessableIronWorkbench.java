package ihl.flexible_cable;

import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import ic2.api.recipe.IRecipeInput;
import ic2.core.IC2;
import ic2.core.item.ItemUpgradeModule;
import ihl.interfaces.IWire;
import ihl.utils.IHLUtils;

public class InvSlotProcessableIronWorkbench extends IronWorkbenchInvSlot {
	
	public InvSlotProcessableIronWorkbench(IronWorkbenchTileEntity base1,
			String name1, int oldStartIndex1, Access access1, int count) {
		super(base1, name1, oldStartIndex1, access1, count);
	}

	@Override
    public boolean accepts(ItemStack itemStack)
    {
        if (itemStack != null && itemStack.getItem() instanceof ItemUpgradeModule)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
	
	@Override
    public void put(int index, ItemStack content)
    {
		super.put(index, content);
		if(IC2.platform.isSimulating() && ((IronWorkbenchTileEntity)this.base).container!=null)
		{
			((IronWorkbenchTileEntity)this.base).resetOutput();
			((IronWorkbenchTileEntity)this.base).container.detectAndSendChanges();
		}
    }

	public void substract(List<IRecipeInput> materials, int multiplier) 
	{
		Iterator<IRecipeInput> i1  = materials.iterator();
		while(i1.hasNext())
		{
			IRecipeInput is1 = i1.next();
			for(int i=0;i<this.size();i++)
			{
				ItemStack is = this.get(i);
				if(is!=null && (is1.matches(is)))
				{
					if(is.getItem() instanceof IWire)
					{
						if(IHLUtils.adjustWireLength(is, -is1.getAmount()*multiplier))
						{
							is.stackSize=0;
						}
					}
					else
					{
						is.stackSize-=is1.getAmount()*multiplier;
					}
					if(is.stackSize<=0)
					{
						this.put(i, null);
					}
					break;
				}
			}
		}
	}

	public int getAmountOf(ItemStack rubber) 
	{
		int amount = 0;
		for(int i=0;i<this.size();i++)
		{
			ItemStack is = this.get(i);
			if(is!=null && ((is.getItem() == rubber.getItem() && is.getItemDamage() == rubber.getItemDamage())||IHLUtils.isItemsHaveSameOreDictionaryEntry(is, rubber)))
			{
				amount+=is.stackSize;
			}
		}
		return amount;
	}

	public int getMultiplier(List<IRecipeInput> materials) 
	{
		int m = Integer.MAX_VALUE;
		for(int i=0;i<this.size();i++)
		{
			ItemStack is = this.get(i);
			for(IRecipeInput recipeInput:materials)
			{
				if(is!=null && recipeInput.matches(is))
				{
					m = Math.min(m, Math.max(IHLUtils.getAmountOf(is)/recipeInput.getAmount(),1));
				}
			}
		}
		return m;
	}
}
