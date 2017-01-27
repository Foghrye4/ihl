package ihl.flexible_cable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InvSlotOutputInProgress extends IronWorkbenchInvSlot{
	public short[] slotRecipe;
	private int size;
	public InvSlotOutputInProgress(IronWorkbenchTileEntity base1, String name1,	int oldStartIndex1, int count) {
		super(base1, name1, oldStartIndex1, Access.NONE, count);
		slotRecipe = new short[count];
		size=count;
		for(int i=0;i<slotRecipe.length;i++)
		{
			slotRecipe[i]=-1;
		}
	}
	
	@Override
    public boolean accepts(ItemStack itemStack)
    {
        return false;
    }

	@Override
	public boolean getCanTakeStack()
	{
		return false;
	}
	
	@Override
    public void writeToNbt(NBTTagCompound nbtTagCompound){}
	
	@Override
    public void readFromNbt(NBTTagCompound nbtTagCompound){}
	
	public short put(List<ItemStack> outputs) 
	{
		short slot=this.getFirstEmptySlot();
		if(slot!=-1 && outputs!=null && !outputs.isEmpty() && slot<=this.size()-outputs.size())
		{
			for(int i=0;i<outputs.size();i++)
			{
				ItemStack output = outputs.get(i);
				this.put(slot+i,output.copy());
				this.slotRecipe[slot+i]=slot;
			}
		}
		return slot;
	}
	
	
	
	private short getFirstEmptySlot() 
	{
		for(short i=0; i<this.size();i++)
		{
			if(this.get(i)==null)
			{
				return i;
			}
		}
		return -1;
	}
	
	public int getCheckSum()
	{
		int sum=0;
		for(short i=0; i<this.size();i++)
		{
			sum+=this.slotRecipe[i];
		}
		return sum;
	}

	public List<ItemStack> getRecipeOutputs(int currentSlot) 
	{
		int slotRecipeIndex=this.slotRecipe[currentSlot];
		List<ItemStack> list = new ArrayList<ItemStack>();
		for(int i=0;i<this.size;i++)
		{
			if(this.slotRecipe[i]==slotRecipeIndex)
			{
				list.add(get(i));
			}
		}
		return list;
	}

}
