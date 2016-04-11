package ihl.datanet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.network.NetworkManager;
import ihl.IHLMod;
import ihl.flexible_cable.NodeEntity;
import ihl.interfaces.IDataCableHolder;
import ihl.interfaces.IDataNode;
import ihl.processing.invslots.InvSlotSignalProcessor;
import ihl.utils.IHLInvSlotDischarge;
import ihl.utils.IHLUtils;

public class RedstoneSignalConverterTileEntity extends TileEntityInventory implements IDataCableHolder, INetworkClientTileEntityEventListener, IHasGui{

	public final InvSlotSignalProcessor sensorEmitterSlots;
	public final List<NBTTagCompound> cableList;
	public final IHLInvSlotDischarge dischargeSlot;
	public final List<Integer> links = new ArrayList<Integer>();//Short has 16 bits
	public int prevButtonPressed=-1;
	public short energy=0;
	public final static short maxEnergy=Short.MAX_VALUE;
	private int tick=0;
	public boolean linksOrInventoryChanged=false;
	private int inventoryCheckSum=0;
	public final Contact[] contacts = new Contact[68];
	private NBTTagCompound nbtread;
	public boolean checkcableList=true;
	public short cablesAmount=0;
	
	/*Contacts is:
	 * 0 - battery slot plus
	 * 1 - battery slot minus
	 * 2-7 sensors/emitters plus
	 * 8-13 sensors/emitters minus
	 * 14-49 cableList
	*/ 
	
	public RedstoneSignalConverterTileEntity()
	{
		sensorEmitterSlots = new InvSlotSignalProcessor(this, "sensorEmitterSlots", 0, Access.IO, 6, 2);
		cableList = new ArrayList();
		dischargeSlot = new IHLInvSlotDischarge(this, 2, Access.IO, 4);
		contacts[0]=new Contact(this,0,null,-1,0);
		contacts[1]=new Contact(this,1,null,-1,1);
		for(int i1=2;i1<8;i1++)
		{
				contacts[i1]=new Contact(this,i1, sensorEmitterSlots,i1-2,0);
		}
		for(int i1=8;i1<14;i1++)
		{
			sensorEmitterSlots.setStackSizeLimit(1);
				contacts[i1]=new Contact(this,i1, sensorEmitterSlots,i1-8,1);
		}
		for(int i1=14;i1<50;i1++)
		{
				contacts[i1]=new Contact(this,i1,null, -1, -1);
		}
		for(int i1=50;i1<56;i1++)
		{
				contacts[i1]=new Contact(this,i1, sensorEmitterSlots,i1-50,2);
		}
		for(int i1=56;i1<62;i1++)
		{
				contacts[i1]=new Contact(this,i1, sensorEmitterSlots,i1-56,3);
		}
		for(int i1=62;i1<68;i1++)
		{
				contacts[i1]=new Contact(this,i1, sensorEmitterSlots,i1-02,4);
		}
	}
	
    @Override
    public boolean wrenchCanRemove(EntityPlayer var1)
    {
    	return this.cableList.isEmpty();
    }
    
	@Override
	public void setFacing(short facing)
	{
		this.removeAttachedChains();
	}

    @Override
	public void onLoaded()
    {
        super.onLoaded();
        if (IC2.platform.isSimulating())
        {
    		IC2.network.get().updateTileEntityField(this, "sensorEmitterSlots");
    		if(nbtread!=null)
    		{
    	        for(Contact contact1:contacts)
    	        {
    	        	contact1.readFromNBT(nbtread);
    	        }
    		}
    		nbtread=null;
        }
   }
    
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("sensorEmitterSlots");
		return fields;
    }
	
    @Override
	public void updateEntityClient()
    {
    	int checkSum=0;
    	for(int i=0;i<this.sensorEmitterSlots.size();i++)
    	{
    		if(this.sensorEmitterSlots.get(i)!=null)
    		{
    			if(IHLUtils.isItemStacksIsEqual(this.sensorEmitterSlots.get(i), Ic2Items.splitterCableItem, true))
    			{
    				checkSum++;
    			}
    			else if(IHLUtils.isItemStacksIsEqual(this.sensorEmitterSlots.get(i), Ic2Items.detectorCableItem, true))
    			{
    				checkSum+=2;
    			}
    		}
    	}
    	if(checkSum!=inventoryCheckSum)
    	{
    		inventoryCheckSum=checkSum;
        	this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    	}
    }

    @Override
	public void updateEntityServer()
    {
        super.updateEntityServer();
        if(this.linksOrInventoryChanged && this.energy>0)
        {
			Set<Integer> checkedGrids = new HashSet();
			Set<Integer> unCheckedGrids = new HashSet();
			InvSlotSignalProcessor slots = this.sensorEmitterSlots;
    		for(int i=0;i<slots.size();i++)
    		{
    			ItemStack is = slots.get(i);
    			if(is!=null && IHLUtils.isItemStacksIsEqual(is, Ic2Items.splitterCableItem, true))
    			{
    				int dgid1 = contacts[i+2].getDataGridID(); 
    				int dgid2 = contacts[i+8].getDataGridID();
    				if(dgid1!=-1)
    				{
    					for(IDataNode contact3:IHLMod.datanet.getGrid(dgid1).telist)
    					{	
    						contact3.checkAttachedSlots();
    						if(contact3.getAttachedSlot()!=null)
    						{
    							Contact c4 = contact3.getAttachedSlot().getOppositeContact(contact3);
    							if(!checkedGrids.contains(c4.getDataGridID()))
    							{
    								unCheckedGrids.add(c4.getDataGridID());
    							}
    						}
    					}
						checkedGrids.add(dgid1);
    				}
    				if(dgid2!=-1 && dgid2!=dgid1)
    				{
    					for(IDataNode contact3:IHLMod.datanet.getGrid(dgid2).telist)
    					{	
    						contact3.checkAttachedSlots();
    						if(contact3.getAttachedSlot()!=null)
    						{
    							Contact c4 = contact3.getAttachedSlot().getOppositeContact(contact3);
    							if(!checkedGrids.contains(c4.getDataGridID()))
    							{
    								unCheckedGrids.add(c4.getDataGridID());
    							}
    						}
    					}
						checkedGrids.add(dgid2);
    				}
    				
    			}

			}
			Iterator<Integer> ugi = unCheckedGrids.iterator();
			while(!unCheckedGrids.isEmpty())
			{
				Integer dgid2 = ugi.next();
				ugi.remove();
        		{
					for(IDataNode contact3:IHLMod.datanet.getGrid(dgid2).telist)
					{	
						contact3.checkAttachedSlots();
						if(contact3.getAttachedSlot()!=null)
						{
							Contact c4 = contact3.getAttachedSlot().getOppositeContact(contact3);
							if(!checkedGrids.contains(c4.getDataGridID()))
							{
								unCheckedGrids.add(c4.getDataGridID());
							}
						}
					}
					checkedGrids.add(dgid2);
        		}
        	}
        	this.linksOrInventoryChanged=false;
        }
    	if(this.energy < 1)
    	{
    		this.energy += (short)(this.dischargeSlot.discharge(getDemandedEnergy(), false)*256D);
    	}
    	if(tick++ % 256==0)
    	{
    		this.energy--;
    	}
    }

	@Override
	public String getInventoryName() 
	{
		return "redstoneSignalConverter";
	}
	@Override
	public boolean addDataCable(NBTTagCompound cable) 
	{
		if(this.cableList.size()<4)
		{
			this.cableList.add(cable);
			return true;
		}
		return false;
	}

	
	@Override
	public double[] getPortPos(EntityLivingBase player) 
	{
		ForgeDirection direction = ForgeDirection.getOrientation(IHLUtils.getFacingFromPlayerView(player, true)).getOpposite();
		return new double[] {this.xCoord+0.5d+0.5d*direction.offsetX,this.yCoord+direction.offsetY*1.0d,this.zCoord+0.5d+0.5d*direction.offsetZ};
	}
	
	@Override
	public void onNetworkEvent(EntityPlayer player, int event) 
	{
		if(prevButtonPressed==-1 && event < 255)
		{
			prevButtonPressed = event;
		}
		else if(prevButtonPressed!=event && event < 255)
		{
			int minor = Math.min(prevButtonPressed,event);
			int major = Math.max(prevButtonPressed,event);
			int link = ((minor<<8) | major);
			this.links.add(link);
			contacts[prevButtonPressed].establishLink(contacts[event]);
			prevButtonPressed=-1;
		}
		else if(event==256 && !this.links.isEmpty())
		{
			int link = this.links.remove(this.links.size()-1);
			Contact contact1 = this.contacts[link >> 8];
			Contact contact2 = this.contacts[link & 255];
			contact1.removeLinkTo(contact2);
		}
		else if(event==257 && !this.links.isEmpty())
		{
			this.links.clear();
			for(Contact contact1:contacts)
			{
				contact1.removeConnections(contacts);
			}
		}
	}

	@Override
    public void writeToNBT(NBTTagCompound nbt)
    {
		super.writeToNBT(nbt);
        for(Contact contact1:contacts)
        {
        	contact1.writeToNBT(nbt);
        }
        if (!links.isEmpty())
        {
        	NBTTagList linkList1 = new NBTTagList();
        	Iterator<Integer> fli = links.iterator();
			while(fli.hasNext())
        	{
	        	Integer link = fli.next();
        		if(link!=null)
        		{
        			NBTTagCompound linkNBT1 = new NBTTagCompound();
        			linkNBT1.setInteger("link", link);
        			linkList1.appendTag(linkNBT1);
        		}
        	}
			nbt.setTag("links", linkList1);
        }
        else
        {
            nbt.setString("Empty", "");
        }
        NBTTagList cableNBTList = new NBTTagList();
        for(NBTTagCompound cable:this.cableList)
        {
        	cableNBTList.appendTag(cable);
        }
        nbt.setTag("cableList", cableNBTList);
        nbt.setBoolean("checkcableList", this.checkcableList);
    }
	
	@Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        NBTTagList cableNBTList=nbt.getTagList("cableList", 10);
        for(int i=0;i<cableNBTList.tagCount();i++)
        {
            this.cableList.add(cableNBTList.getCompoundTagAt(i));
        }
        if (!nbt.hasKey("Empty"))
        {
    		NBTTagList ll = nbt.getTagList("links", 10);
            for(int i=0;i<ll.tagCount();i++)
            {
            	NBTTagCompound llNBT1 = ll.getCompoundTagAt(i);
            	links.add(llNBT1.getInteger("link"));
            }
        }
        nbtread=nbt;
        this.checkcableList=nbt.getBoolean("checkcableList");
    }
	
	public double getDemandedEnergy()
	{
		return (RedstoneSignalConverterTileEntity.maxEnergy - this.energy)/256D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean flag) 
	{
		return new RedstoneSignalConverterGui(new RedstoneSignalConverterContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) 
	{
		return new RedstoneSignalConverterContainer(player, this);
	}
	
	public int getGUIEnergy(int i) 
	{
    	if(this.energy<Float.MAX_VALUE)
    	{
    		return Math.round((float)this.energy/(float)RedstoneSignalConverterTileEntity.maxEnergy*i);
    	}
    	else
    	{
    		return Math.round((float)(this.energy/(double)RedstoneSignalConverterTileEntity.maxEnergy)*i);
    	}
	}

	@Override
	public void onGuiClosed(EntityPlayer player) {/*Do nothing*/}

	public int isProvidingRedstonePower(int side) 
	{
		return this.sensorEmitterSlots.isSlotActivated(side)?15:0;
	}
	
	public void removeAttachedChains()
	{
		if(!cableList.isEmpty())
		{
			Iterator<NBTTagCompound> cli = cableList.iterator();
			while(cli.hasNext())
			{
				NBTTagCompound cl=cli.next();
				IHLMod.datanet.removeCableEntities(cl);
				ItemStack is = IHLUtils.getThisModItemStack("dataCable");
				is.stackTagCompound=cl;
				double[] pps = this.getPortPos(null);
				EntityItem eitem = new EntityItem(worldObj, pps[0], pps[1], pps[2], is);
				worldObj.spawnEntityInWorld(eitem);
				{
					int chainUID = cl.getInteger("chainUID");
					int x1 = cl.getInteger("connectorX1");
            		int y1 = cl.getInteger("connectorY1");
            		int z1 = cl.getInteger("connectorZ1");
            		int connectorDimensionId1 = cl.getInteger("connectorDimensionId1");
					int x = cl.getInteger("connectorX");
            		int y = cl.getInteger("connectorY");
            		int z = cl.getInteger("connectorZ");
            		int connectorDimensionId = cl.getInteger("connectorDimensionId");
            		if(x1!=xCoord || y1!=yCoord || z1!=zCoord)
            		{
            			x=x1;
            			y=y1;
            			z=z1;
            			connectorDimensionId=connectorDimensionId1;
            		}
					TileEntity te = MinecraftServer.getServer().worldServerForDimension(connectorDimensionId).getTileEntity(x, y, z);
					if(te instanceof IDataCableHolder)
					{
						IDataCableHolder dch = ((IDataCableHolder)te);
						Contact[] c = this.getContacts(chainUID);
						Contact[] c1 = dch.getContacts(chainUID);
						for(int i2=0;i2<c.length;i2++)
						{
							c[i2].removeLinkTo(c1[i2]);
						}
						dch.removeCable(chainUID);
					}
					cli.remove();
				}
			}
		}
	}

	@Override
	public Contact[] getContacts(int chainUID) 
	{
		for(int i=0;i<this.cableList.size();i++)
		{
			NBTTagCompound is = this.cableList.get(i);
			if(is.getInteger("chainUID")==chainUID)
			{
					Contact[] contactsOut = new Contact[8];
					for(int i1=0;i1<contactsOut.length;i1++)
					{
						contactsOut[i1]=contacts[i1+i*8+14];
					}
					return contactsOut;
			}
		}
		return null;
	}

	@Override
	public void removeCable(int chainUID) 
	{		
		Iterator<NBTTagCompound> cli = cableList.iterator();
		while(cli.hasNext())
		{
			NBTTagCompound cl=cli.next();
			int chainUID1 = cl.getInteger("chainUID");
			if(chainUID1==chainUID)
			{
					cli.remove();
					return;
			}
		}
	}

	@Override
	public Contact getContact(int innerId1) 
	{
		return this.contacts[innerId1];
	}
	

	@Override
	public boolean isCableRemoved(int chainUniqueID) {
		if(!checkcableList)
		{
			return false;
		}
		for(NBTTagCompound cl:cableList)
		{
			if(cl.getInteger("chainUID")==chainUniqueID)
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void setCableCheck(boolean b) 
	{
		this.checkcableList=b;
	}
}
