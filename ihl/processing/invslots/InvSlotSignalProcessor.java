package ihl.processing.invslots;

import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.core.Ic2Items;
import ic2.core.block.invslot.InvSlot;
import ihl.datanet.Contact;
import ihl.datanet.RedstoneSignalConverterTileEntity;
import ihl.interfaces.IDataNode;
import ihl.utils.IHLUtils;

public class InvSlotSignalProcessor extends InvSlot
{
	private RedstoneSignalConverterTileEntity rscBase;
	public final boolean[] slotStatus;//false=normal;true=activated
	public InvSlotSignalProcessor(RedstoneSignalConverterTileEntity base1, String name1, int oldStartIndex1, Access access1, int count, int linksPerSlot) {
		super(base1, name1, oldStartIndex1, access1, count);
		rscBase = base1;
		slotStatus = new boolean[count];
	}
	
	public Contact getOppositeContact(IDataNode contact3) 
	{
		for(Contact contact1 : this.rscBase.contacts)
		{
			if(contact1.attachedSlot==this && 
			contact3.getAttachedSlotNumber()==contact1.attachedSlotNumber)
			{
				switch(contact3.getType())
				{
					case 0:
						if(contact1.type==1)
						{
							return contact1;
						}
						break;
					case 1:
						if(contact1.type==0)
						{
							return contact1;
						}
						break;
					case 2:
						if(slotStatus[contact1.attachedSlotNumber])
						{
							if(contact1.type==3)
							{
								return contact1;
							}
						}
						else
						{
							if(contact1.type==4)
							{
								return contact1;
							}
						}
						break;
					case 3:
						if(slotStatus[contact1.attachedSlotNumber])
						{
							if(contact1.type==2)
							{
								return contact1;
							}
						}
						break;
					case 4:
						if(!slotStatus[contact1.attachedSlotNumber])
						{
							if(contact1.type==2)
							{
								return contact1;
							}
						}
						break;
				}
			}
		}
		return null;
	}

	public boolean isSlotActivated(int slot)
	{
		return this.slotStatus[slot];
	}
	
	public void notifyNeighbors()
	{
    	int xyz[] = {0,0,1,0,0,-1,0,0};
    	Block block;
    	int x,y,z;
    	for(int i=0;i<=5;i++)
		{
    		x=rscBase.xCoord+xyz[i];
    		y=rscBase.yCoord+xyz[i+1];
    		z=rscBase.zCoord+xyz[i+2];
			block = rscBase.getWorldObj().getBlock(rscBase.xCoord,rscBase.yCoord,rscBase.zCoord);
			rscBase.getWorldObj().notifyBlockOfNeighborChange(x,y,z,block);
		}
	}

	public boolean isIndirectlyConnectedWithContact(Contact contact, int id) 
	{
		if(this.get(contact.attachedSlotNumber)!=null)
		{
			if(IHLUtils.isItemStacksIsEqual(this.get(contact.attachedSlotNumber), Ic2Items.splitterCableItem, true))//Redstone sensor
			{
				ForgeDirection dir1 = ForgeDirection.getOrientation(contact.attachedSlotNumber);
				if(this.base.getWorldObj().getIndirectPowerOutput(base.xCoord+dir1.offsetX, base.yCoord+dir1.offsetY, base.zCoord+dir1.offsetZ, dir1.getOpposite().flag))
				{
					Contact oppositeContact = this.getOppositeContact(contact);
					if(oppositeContact.getDataGridID()!=contact.getDataGridID())
					{
						return oppositeContact.isConnectedToContact(id);
					}
				}
			}
		}
		return false;
	}

}