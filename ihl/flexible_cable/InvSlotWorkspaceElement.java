package ihl.flexible_cable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import ihl.IHLMod;
import ihl.interfaces.IWorkspaceElement;
import ihl.utils.IHLUtils;

public class InvSlotWorkspaceElement extends IronWorkbenchInvSlot {

	private final IWorkspaceElement[] workspaceElements;
	public final boolean[] ready;
	
	public InvSlotWorkspaceElement(IronWorkbenchTileEntity base1, String name1,	int oldStartIndex1, Access access1, int count) 
	{
		super(base1, name1, oldStartIndex1, access1, count);
		this.put(0, IHLUtils.getThisModItemStack("ironWorkbench"));
		workspaceElements=new IWorkspaceElement[count];
		ready=new boolean[count];
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

	public boolean containsAndCanUse(List<ItemStack> workspaceElements2) 
	{
		Iterator<ItemStack> iterator = workspaceElements2.iterator();
		while(iterator.hasNext())
		{
			ItemStack rWorkspaceElementItemStack = iterator.next();
			boolean presence=false;
			for(int i=1;i<workspaceElements.length;i++)
			{
				IWorkspaceElement cWorkspaceElement=workspaceElements[i];
				if(cWorkspaceElement!=null && rWorkspaceElementItemStack.getItem() == cWorkspaceElement.getWrenchDrop(null).getItem() && cWorkspaceElement.canBeUsed())
				{
					presence=true;
					break;
				}
			}
			//System.out.println("Checking " + rWorkspaceElementItemStack.getDisplayName());
			if(!presence)
			{
				//System.out.println("Return false");
				return false;
			}
		}
		return true;
	}

	public void use(List<ItemStack> workspaceElements2) 
	{
		Iterator<ItemStack> iterator = workspaceElements2.iterator();
		while(iterator.hasNext())
		{
			ItemStack rWorkspaceElementItemStack = iterator.next();
			boolean presence=false;
			for(int i=1;i<workspaceElements.length;i++)
			{
				IWorkspaceElement cWorkspaceElement=workspaceElements[i];
				if(cWorkspaceElement==null)
				{
					continue;
				}
				if(rWorkspaceElementItemStack.getItem() == cWorkspaceElement.getWrenchDrop(null).getItem() && cWorkspaceElement.canBeUsed())
				{
					cWorkspaceElement.use();
					break;
				}
			}
		}
	}
	
	public void reset()
	{
		for(int i=1;i<workspaceElements.length;i++)
		{
			workspaceElements[i]=null;
			ready[i]=true;
			this.put(i,null);
		}
		for(int i=1;i<workspaceElements.length;i++)
		{
			boolean clear=true;
			//int[] xz = new int [] {0,1,1,0,-1,-1,1,-1,0};
			// -1:1;	0:1;	1:1
			// -1:0;	0:0;	1:0
			// -1:-1;	0:-1;	1;-1
			// 0 1 -1 -1 1 1 0 -1 0
			for(int iy=-1;iy<=1;iy++)
			{
				for(int ix=-2;ix<=2;ix++)
				{
					for(int iz=-2;iz<=2;iz++)
					{
						TileEntity te = this.base.getWorldObj().getTileEntity(this.base.xCoord+ix, this.base.yCoord+iy, this.base.zCoord+iz);
						if(te instanceof IWorkspaceElement)
						{
							IWorkspaceElement wse = (IWorkspaceElement)te;
							if(!wse.getIsInvalid() && !this.contains(wse))
							{
								workspaceElements[i]=wse;
								ready[i]=wse.canBeUsed();
								this.put(i,wse.getWrenchDrop(null));
								clear=false;
								break;
							}
						}
					}
				}
			}
			if(clear)
			{
				workspaceElements[i]=null;
				this.put(i,null);
			}
		}
	}
	
	@Override
    public void writeToNbt(NBTTagCompound nbtTagCompound){}
	
	@Override
    public void readFromNbt(NBTTagCompound nbtTagCompound){}

	private boolean contains(IWorkspaceElement wse) 
	{
		for(int i=0;i<workspaceElements.length;i++)
		{
			if(workspaceElements[i]!=null && workspaceElements[i].getWrenchDrop(null).getItem() == wse.getWrenchDrop(null).getItem())
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean contains(ItemStack wse) 
	{
		for(int i=1;i<size();i++)
		{
			if(this.get(i)!=null && this.get(i).getItem()==wse.getItem())
			{
				return true;
			}
		}
		return false;
	}
	
	public short encodeReadyStatus()
	{
		short result=0;
		for(int i=0;i<ready.length;i++)
		{
			if(ready[i])
			{
				result+= 1 << i;
			}
		}
		//System.out.println("Encoding result = "+ result);
		return result;
	}
	
	public void decodeReadyStatus(short encodedShort)
	{
		for(int i=0;i<ready.length;i++)
		{
			ready[i]=((encodedShort >> i) & 1)==1;
			//System.out.println("Decoding result = "+ ready[i]);
		}
	}
	
	@Override
	public List<ItemStack> getItemStackList()
	{
		List<ItemStack> list = new ArrayList();
		for(int i=1; i<this.size(); i++)
		{
			if(get(i)!=null)list.add(get(i));
		}
		return list;
	}
	
}
