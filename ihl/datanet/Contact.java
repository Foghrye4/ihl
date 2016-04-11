package ihl.datanet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ic2.core.Ic2Items;
import ihl.IHLMod;
import ihl.interfaces.IDataCableHolder;
import ihl.interfaces.IDataNode;
import ihl.processing.invslots.InvSlotSignalProcessor;
import ihl.utils.IHLUtils;

public class Contact implements IDataNode 
{
	public final int innerId;
	private int dataGridID=-1;
	private Set<IDataNode> connectedContacts = new HashSet();
	public final InvSlotSignalProcessor attachedSlot;
	public final int attachedSlotNumber;
	public final int type; //Free:-1; A1:0; A2:1; C:2; NO:3; NC:4;
	private final TileEntity base;
	
	public Contact(TileEntity base1, int innerId1,InvSlotSignalProcessor attachedSlot1,int attachedSlotNumber1, int type1)
	{
		type=type1;
		base=base1;
		innerId=innerId1;
		attachedSlot=attachedSlot1;
		attachedSlotNumber=attachedSlotNumber1;
	}
	
	public void onLoad()
	{
        if(dataGridID!=-1)
        {
           	DataGrid grid = IHLMod.datanet.getGrid(dataGridID);
           	grid.telist.add(this);
        }	
	}

	@Override
	public int getDataGridID() {
		return dataGridID;
	}

	@Override
	public void setDataGrid(int newGridID) 
	{
		dataGridID=newGridID;
		onLoad();
	}

	@Override
	public Set<IDataNode> getConnectedDataNodes() {
		return this.connectedContacts;
	}

	public void removeLinkTo(Contact contact2) 
	{
		this.connectedContacts.remove(contact2);
		contact2.getConnectedDataNodes().remove(this);
		if(dataGridID!=-1)
		{
			IHLMod.datanet.splitGrids(dataGridID);
		}
		if(dataGridID!=-1)
		{
			for(IDataNode contact3:IHLMod.datanet.getGrid(dataGridID).telist)
			{	
				contact3.checkAttachedSlots();
			}
		}
		else
		{
			this.checkAttachedSlots();
		}
		if(contact2.getDataGridID()!=-1)
		{
			for(IDataNode contact3:IHLMod.datanet.getGrid(contact2.getDataGridID()).telist)
			{	
				contact3.checkAttachedSlots();
			}
		}
		else
		{
			contact2.checkAttachedSlots();
		}

	}

	public void removeConnections(Contact[] contacts) 
	{
		if(this.dataGridID!=-1)
		{
			this.connectedContacts.removeAll(Arrays.asList(contacts));
			IHLMod.datanet.splitGrids(dataGridID);
		}
	}

	public void establishLink(Contact contact) 
	{
		this.connectedContacts.add(contact);
		contact.getConnectedDataNodes().add(this);
		if(dataGridID!=contact.getDataGridID() || dataGridID==-1 || contact.getDataGridID()==-1)
		{
			int newDataGridID = IHLMod.datanet.mergeGrids(dataGridID,contact.getDataGridID());
			if(this.dataGridID==-1)
			{
				this.setDataGrid(newDataGridID);
			}
			if(contact.getDataGridID()==-1)
			{
				contact.setDataGrid(newDataGridID);
			}
			for(IDataNode contact2:IHLMod.datanet.getGrid(newDataGridID).telist)
			{
				contact2.checkAttachedSlots();
			}
		}
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("dataGridID", dataGridID);
    	NBTTagList connections = new NBTTagList();
		for(IDataNode contact1:this.connectedContacts)
		{
			NBTTagCompound contactNBT = new NBTTagCompound();
			contactNBT.setInteger("x", contact1.getXPos());
			contactNBT.setInteger("y", contact1.getYPos());
			contactNBT.setInteger("z", contact1.getZPos());
			contactNBT.setInteger("innerId", contact1.getInnerId());
			connections.appendTag(contactNBT);
		}
		nbt.setTag("connections"+innerId, connections);
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.setDataGrid(nbt.getInteger("dataGridID"));
		NBTTagList connections = nbt.getTagList("connections"+innerId, 10);
        for(int i=0;i<connections.tagCount();i++)
        {
         	NBTTagCompound contactNBT = connections.getCompoundTagAt(i);
         	int innerId1 = contactNBT.getInteger("innerId");
			int x = contactNBT.getInteger("x");
			int y = contactNBT.getInteger("y");
			int z = contactNBT.getInteger("z");
			World world = this.base.getWorldObj();
			TileEntity te = world.getTileEntity(x, y, z);
			if(te!=null && te instanceof IDataCableHolder)
			{
				IDataCableHolder idch = (IDataCableHolder)te;
				Contact contact1 = idch.getContact(innerId1);
				this.establishLink(contact1);
			}
			else
			{
				IHLMod.log.error("Can't load contacts, because IDataCableHolder is null.");
			}
         }
	}

	@Override
	public int getXPos() {
		return base.xCoord;
	}

	@Override
	public int getYPos() {
		return base.yCoord;
	}

	@Override
	public int getZPos() {
		return base.zCoord;
	}

	@Override
	public int getInnerId() {
		return innerId;
	}

	public boolean isConnectedToContact(int id) 
	{
		if(dataGridID!=-1)
		{
			return IHLMod.datanet.getGrid(getDataGridID()).isConnectedToContact(this,id);
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isIndirectlyConnectedWithContact(int id) {
		if(this.attachedSlot!=null)
		{
			return this.attachedSlot.isIndirectlyConnectedWithContact(this,id);
		}
		return false;
	}

	@Override
	public void checkAttachedSlots() 
	{
		if(this.attachedSlot!=null)
		{
			ItemStack is = this.attachedSlot.get(this.attachedSlotNumber);
			if(is!=null && IHLUtils.isItemStacksIsEqual(is, Ic2Items.detectorCableItem, true))
			{
				if((this.isConnectedToContact(0) && this.attachedSlot.getOppositeContact(this).isConnectedToContact(1))||
						(this.isConnectedToContact(1) && this.attachedSlot.getOppositeContact(this).isConnectedToContact(0)))
				{
					this.attachedSlot.slotStatus[this.attachedSlotNumber]=true;
				}
				else
				{
					this.attachedSlot.slotStatus[this.attachedSlotNumber]=false;
				}
			}
   			else
			{
   				this.attachedSlot.slotStatus[this.attachedSlotNumber]=false;
			}
			this.attachedSlot.notifyNeighbors();
		}
	}

	@Override
	public InvSlotSignalProcessor getAttachedSlot() {
		return this.attachedSlot;
	}

	@Override
	public int getAttachedSlotNumber() 
	{
		return this.attachedSlotNumber;
	}

	@Override
	public int getType() 
	{
		return this.type;
	}

}
