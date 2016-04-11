package ihl.flexible_cable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import ic2.core.block.invslot.InvSlot;

public class IronWorkbenchInvSlot extends InvSlot {

	public IronWorkbenchInvSlot(IronWorkbenchTileEntity base1, String name1,
			int oldStartIndex1, Access access1, int count) {
		super(base1, name1, oldStartIndex1, access1, count);
	}
	
	public List<ItemStack> getItemStackList()
	{
		List<ItemStack> list = new ArrayList();
		for(int i=0; i<this.size(); i++)
		{
			if(get(i)!=null)list.add(get(i));
		}
		return list;
	}

	public boolean getCanTakeStack() 
	{
		return true;
	}
}
