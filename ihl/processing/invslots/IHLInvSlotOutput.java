package ihl.processing.invslots;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.recipes.RecipeOutputItemStack;
import ihl.utils.IHLUtils;

public class IHLInvSlotOutput extends InvSlotOutput{

	private final Map<Long,Float> substanceAmount = new HashMap();
	public IHLInvSlotOutput(TileEntityInventory base1, String name1, int oldStartIndex1, int count) {
		super(base1, name1, oldStartIndex1, count);
	}

	@Override
	public boolean canAdd(List itemOutputs) 
	{
		if(itemOutputs==null || itemOutputs.isEmpty())
		{
			return true;
		}
		Iterator ioi = itemOutputs.iterator();
		if(this.size()>=itemOutputs.size())
		{
			Object rois;
			if(ioi.hasNext())
			{
				rois = ioi.next();
			}
			else
			{
				return true;
			}
			for(int i=0;i<this.size();i++)
			{
				if(this.get(i)==null || (this.objectMatchesSlot(rois, i) && this.get(i).stackSize+this.getAmoutOfObject(rois)<this.getStackSizeLimit()))
				{
					if(ioi.hasNext())
					{
						rois = ioi.next();
					}
					else
					{
						return true;
					}
				}
				else
				{
					if(i==this.size()-1)
					{
						return false;
					}
				}
			}
			
		}
		return false;
	}

	private float getAmoutOfObject(Object obj) {
		if(obj instanceof ItemStack)
		{
			return ((ItemStack) obj).stackSize;
		}
		else if(obj instanceof RecipeOutputItemStack)
		{
			return ((RecipeOutputItemStack)obj).quantity;
		}
		return Short.MAX_VALUE;
	}

	public boolean objectMatchesSlot(Object obj, int slot)
	{
		boolean matches=true;
		if(this.get(slot)==null)
		{
			return true;
		}
		else
		{
			if(obj instanceof ItemStack)
			{
				return IHLUtils.isItemStacksIsEqual(this.get(slot), (ItemStack) obj, true);
			}
			else if(obj instanceof RecipeOutputItemStack)
			{
				return ((RecipeOutputItemStack)obj).matches(this.get(slot));
			}
		}
		return false;
	}

	public void add(RecipeOutputItemStack rois) 
	{
		for(int i=0;i<this.size();i++)
		{
			if(this.get(i)==null || (this.objectMatchesSlot(rois, i) && this.get(i).stackSize+this.getAmoutOfObject(rois)<this.getStackSizeLimit()))
			{
				this.add(i, rois);
				break;
			}
		}
	}

	private void add(int i, RecipeOutputItemStack rois) 
	{
		long key = (Item.getIdFromItem(rois.itemStack.getItem())<<32)+rois.itemStack.getItemDamage();
		float amount=0f;
		if(this.substanceAmount.containsKey(key))
		{
			amount = this.substanceAmount.get(key);
		}
		amount+=rois.quantity;
		while(amount>=1)
		{
			amount--;
			this.add(rois.itemStack.copy());
		}
		this.substanceAmount.put(key, amount);
	}

	@Override
	public int add(List itemOutputs) 
	{
		if(itemOutputs==null || itemOutputs.isEmpty())
		{
			return 0;
		}
		Iterator ioi = itemOutputs.iterator();
		if(this.size()>=itemOutputs.size() && ioi.hasNext())
		{
			Object rois = ioi.next();
			for(int i=0;i<this.size();i++)
			{
				if(this.get(i)==null || (this.objectMatchesSlot(rois, i) && this.get(i).stackSize+this.getAmoutOfObject(rois)<this.getStackSizeLimit()))
				{
					if(rois instanceof ItemStack)
					{
						this.add(((ItemStack) rois).copy());
					}
					else if(rois instanceof RecipeOutputItemStack)
					{
						this.add(i, (RecipeOutputItemStack) rois);
					}
					if(ioi.hasNext())
					{
						rois = ioi.next();
					}
					else
					{
						return itemOutputs.size();
					}
				}
				else
				{
					if(i==this.size()-1)
					{
						return 0;
					}
				}
			}
			
		}
		return 0;
	}
	
	@Override
    public void readFromNbt(NBTTagCompound nbtTagCompound)
    {	
		super.readFromNbt(nbtTagCompound);
		NBTTagList amountTagList = nbtTagCompound.getTagList("substanceAmountMap", 10);
    	for(int i=0;i<amountTagList.tagCount();i++)
    	{
    		if(amountTagList.getCompoundTagAt(i).hasKey("substanceKey"))
    		{
       			long substanceKey = amountTagList.getCompoundTagAt(i).getLong("substanceKey");
           		float substanceAmount = amountTagList.getCompoundTagAt(i).getFloat("substanceAmount");
           		this.substanceAmount.put(substanceKey, substanceAmount);
    		}
    	}
    }
    
	@Override
    public void writeToNbt(NBTTagCompound nbtTagCompound)
    {
		super.writeToNbt(nbtTagCompound);
    	NBTTagList sAmountsList = new NBTTagList();
    	int i = 0;
    	Iterator<Entry<Long, Float>> entrySetIterator = this.substanceAmount.entrySet().iterator();
    	while(entrySetIterator.hasNext())
    	{
    		Entry<Long, Float> entry = entrySetIterator.next();
    		NBTTagCompound tag = new NBTTagCompound();
    		tag.setLong("substanceKey", entry.getKey());
    		tag.setFloat("substanceAmount", entry.getValue());
    		sAmountsList.appendTag(tag);
    	}
    	nbtTagCompound.setTag("substanceAmountMap", sAmountsList);
    }
}
