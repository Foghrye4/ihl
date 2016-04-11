package ihl.collector;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.ElectricItem;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.network.NetworkManager;
import ihl.IHLMod;
import ihl.utils.IHLInvSlotDischarge;

public class ChargerEjectorTileEntity extends TileEntityInventory implements IEnergySink, IHasGui, INetworkClientTileEntityEventListener {

    private int tier;
    public int maxStorage=10000000;
    private double energy=0D;
    public final IHLInvSlotDischarge dischargeSlot;
    public final InvSlotMultiCharge chargeSlot;
    public boolean addedToEnergyNet = false;
    public int[] chargeSlotStatus = new int[9];
    public int[] prevChargeSlotStatus = new int[9];
    public ItemStack chargeItemStack0;
    public ItemStack chargeItemStack1;
    public ItemStack chargeItemStack2;
    public ItemStack chargeItemStack3;
    public ItemStack chargeItemStack4;
    public ItemStack chargeItemStack5;
    public ItemStack chargeItemStack6;
    public ItemStack chargeItemStack7;
    public ItemStack chargeItemStack8;
	private int slotNumNet=0;
	public boolean isGuiScreenOpened=false;
	public boolean autoEject=true;
	public boolean firstTickUpdate=true;

	public ChargerEjectorTileEntity()
	{
        super();
        this.tier=4;
        this.dischargeSlot = new IHLInvSlotDischarge(this, 0, Access.IO, this.tier, InvSlot.InvSide.BOTTOM);
        this.chargeSlot = new InvSlotMultiCharge(this, 1, this.tier,9);
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) 
	{
		if(this.dischargeSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.dischargeSlot.get()));
		for(int i=0;i<this.chargeSlot.size();i++)
		{
			if(this.chargeSlot.get(i)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.chargeSlot.get(i)));
		}
		ItemStack stack = new ItemStack(IHLMod.chargerEjectorBlock,1);
		stack.stackTagCompound = new NBTTagCompound();
		stack.stackTagCompound.setDouble("energy", this.energy);
		return stack;
	}
	
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		for(int i=0;i<this.chargeSlot.size();i++)
		{
			fields.add("chargeItemStack"+i);
		}
		return fields;
    }
	

    @Override
	public void onLoaded()
    {
        super.onLoaded();
        if (IC2.platform.isSimulating())
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;

        }
    }
    
    @Override
	public void onUnloaded()
    {
        if (IC2.platform.isSimulating() && this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        super.onUnloaded();
    }
    
    public boolean enableUpdateEntity()
    {
        return true;
    }
	
    @Override
	public void updateEntityClient()
    {
    	updateChargerSlotStatus();
		if(!this.isGuiScreenOpened)
		{
			for(int i=0;i<this.chargeSlot.size();i++)
			{
				if(this.chargeSlot.get(i)!=this.getChargeISByNum(i))
				{
					this.chargeSlot.put(i,this.getChargeISByNum(i));
				}
			}
		}
    }
    
    public void updateChargerSlotStatus()
    {
 		for(int i=0;i<this.chargeSlotStatus.length;i++)
    	{
    		if(!this.getActive() && this.chargeSlot.get(i)==null)
    		{
    			this.chargeSlotStatus[i]=0;
    		}
    		else if(!this.getActive() && this.chargeSlot.get(i)!=null)
    		{
    			this.chargeSlotStatus[i]=-1;
    		}
    		else if(this.getActive() && this.chargeSlot.get(i)==null)
    		{
    			this.chargeSlotStatus[i]=-2;
    		}
    		else if(this.getActive() && this.chargeSlot.get(i)!=null)
    		{
    			if(ElectricItem.manager.charge(this.chargeSlot.get(i), 1, this.tier, false, true)>0)
    			{
    				this.chargeSlotStatus[i]=1;
    			}
    			else
    			{
    				this.chargeSlotStatus[i]=2;
    			}
    		}
    	}

    }
    
    @Override
	public void updateEntityServer()
    {
    	if(this.firstTickUpdate && this.isGuiScreenOpened)
    	{
    		IC2.network.get().updateTileEntityField(this, "autoEject");
    		this.firstTickUpdate=false;
    	}
    	if(this.energy < 1.0D && this.getActive())
    	{
    		this.setActive(false);
    	}
    	else if(this.energy >= 1.0D && !this.getActive())
    	{
    		this.setActive(true);
    	}
    	updateChargerSlotStatus();
    	{
    		List<EntityItem> eItemsList = this.getEItemsList();
            if(eItemsList!=null && eItemsList.size()>0)
            {
            	Iterator ei = eItemsList.iterator();
            	while(ei.hasNext())
            	{
            		EntityItem entity=(EntityItem) ei.next();
            		if(!entity.isDead && entity.getEntityItem()!=null && entity.getEntityItem().stackSize>0)
            		{
            			if(this.chargeSlot.addItemStackToFirstEmptyStack(entity.getEntityItem()))
            			{
            				entity.setDead();
            			}
            		}
            		else
            		{
            			entity.setDead();
            		}
            	}
            }
            EntityPlayer player = this.worldObj.getClosestPlayer(this.xCoord+0.5D, this.yCoord+0.5D, this.zCoord+0.5D, 1.1D);
            if(player!=null && player instanceof EntityPlayerMP)
            {
            	for(int i=0;i<this.chargeSlot.size();i++)
            	{
            		if(this.chargeSlotStatus[i]==2)
            		{
            			if(player.inventory.getFirstEmptyStack()>=0 && player.inventory.addItemStackToInventory(this.chargeSlot.get(i)))
            			{
            				this.chargeSlot.put(i, null);
            			}
            		}
            	}
            }
            
    		if(this.slotNumNet<this.chargeSlotStatus.length-1)
    		{
    			this.slotNumNet++;
    		}
    		else
    		{
    			this.slotNumNet=0;
    		}
    			if(this.chargeSlotStatus[slotNumNet]!=this.prevChargeSlotStatus[slotNumNet])
    			{
    				ItemStack stack1 = this.chargeSlot.get(slotNumNet);
    				if(stack1!=null)
    				{
    					if(this.chargeSlotStatus[slotNumNet]==2 && this.chargeSlot.get(slotNumNet).getItem() instanceof CollectorItem)
    					{
    						if(this.autoEject)
    						{
    							CollectorItem item = (CollectorItem) this.chargeSlot.get(slotNumNet).getItem();
    							if(item.spawnEntityInWorld(this.worldObj, this.chargeSlot.get(slotNumNet), this.xCoord, this.yCoord, this.zCoord))
    							{
            						this.setChargeISByNum(slotNumNet, null);
            						this.chargeSlot.put(slotNumNet, null);
    							}
    							else
    							{
            						this.setChargeISByNum(slotNumNet, this.chargeSlot.get(slotNumNet).copy());
    							}
    						}
    						else
    						{
            					this.setChargeISByNum(slotNumNet, this.chargeSlot.get(slotNumNet).copy());
    						}
    					}
    					else
    					{
        					this.setChargeISByNum(slotNumNet, this.chargeSlot.get(slotNumNet).copy());
    					}
    				}
    				else
    				{
    					this.setChargeISByNum(slotNumNet, null);
    				}
    					IC2.network.get().updateTileEntityField(this, "chargeItemStack"+slotNumNet);
    					this.prevChargeSlotStatus[slotNumNet]=this.chargeSlotStatus[slotNumNet];
    			}
        	double amount;
        	if (this.energy >= 1.0D)
        	{
        		for(int i=0;i<this.chargeSlotStatus.length;i++)
        		{
                	amount = this.chargeSlot.charge((int)this.energy,i);
                	this.energy -= amount;
        		}
        	}
        	if (this.getDemandedEnergy() > 1.0D)
        	{
            	amount = this.dischargeSlot.discharge(this.getDemandedEnergy(), false);
            	this.energy += amount;
        	}
    	}

    }
    
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        try
        {
            this.energy = nbttagcompound.getDouble("energy");
        }
        catch (Exception var3)
        {
            this.energy = nbttagcompound.getInteger("energy");

            if (this.maxStorage > Integer.MAX_VALUE)
            {
                this.energy *= 10.0D;
            }
        }
        this.autoEject=nbttagcompound.getBoolean("autoeject");
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", this.energy);
        nbttagcompound.setBoolean("autoeject", this.autoEject);
    }
	
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
			ForgeDirection direction) {
		return direction==ForgeDirection.DOWN;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer arg0, boolean arg1) 
	{
		return new ChargerEjectorGui(new ChargerEjectorContainer(arg0, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer arg0) {
		return new ChargerEjectorContainer(arg0, this);
	}

	@Override
	public void onGuiClosed(EntityPlayer arg0) 	
	{
		this.isGuiScreenOpened=false;
	}

	@Override
	public String getInventoryName() {
		return "charger_ejector";
	}
	
	public int getStored() {
		return Math.round((float)this.energy);
	}

	public void setStored(double value) {
		this.energy=value;
	}
	
    public boolean getGui(EntityPlayer player)
    {
    		this.isGuiScreenOpened = this instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(player, this) : true) : false;
			return this.isGuiScreenOpened;
    }
    
    private ItemStack getChargeISByNum(int num)
    {
    	switch(num)
    	{
    	case 0:
    		return this.chargeItemStack0;
    	case 1:
    		return this.chargeItemStack1;
    	case 2:
    		return this.chargeItemStack2;
    	case 3:
    		return this.chargeItemStack3;
    	case 4:
    		return this.chargeItemStack4;
    	case 5:
    		return this.chargeItemStack5;
    	case 6:
    		return this.chargeItemStack6;
    	case 7:
    		return this.chargeItemStack7;
    	case 8:
    		return this.chargeItemStack8;
    	default:
    		return this.chargeItemStack0;
    	}
    }
    
    private void setChargeISByNum(int num, ItemStack stack)
    {
    	switch(num)
    	{
    	case 0:
    		this.chargeItemStack0=stack;
    		break;
    	case 1:
    		this.chargeItemStack1=stack;
    		break;
    	case 2:
    		this.chargeItemStack2=stack;
    		break;
    	case 3:
    		this.chargeItemStack3=stack;
    		break;
    	case 4:
    		this.chargeItemStack4=stack;
    		break;
    	case 5:
    		this.chargeItemStack5=stack;
    		break;
    	case 6:
    		this.chargeItemStack6=stack;
    		break;
    	case 7:
    		this.chargeItemStack7=stack;
    		break;
    	case 8:
    		this.chargeItemStack8=stack;
    		break;
    	default:
    		this.chargeItemStack0=stack;
    	}
    }
    
    protected List<EntityItem> getEItemsList()
    {
        double range = 0.2D;
        AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(this.xCoord-range,this.yCoord,this.zCoord-range,this.xCoord+1.0D+range,this.yCoord+1.0D+range,this.zCoord+1.0D+range);
        List<EntityItem> eItemsList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, searchArea);
    	return eItemsList;
    }
    //1.7.10 API
	@Override
	public double getDemandedEnergy()
	{
		 return this.maxStorage - this.energy;
	}
	
	@Override
	public int getSinkTier()
	{
		return this.tier;
	}
	
	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage)
	{
        if (this.energy  >= this.maxStorage)
        {
            return amount;
        }
        else
        {
            this.energy += amount;
            return 0.0D;
        }
	}
	
	@Override
	public void onNetworkEvent(EntityPlayer player, int event) 
	{
		switch(event)
		{
		case 0:
			if(!this.autoEject)
			{
				this.autoEject=true;
            	IC2.network.get().updateTileEntityField(this, "autoEject");
			}
			break;
		case 1:
			if(this.autoEject)
			{
				this.autoEject=false;
            	IC2.network.get().updateTileEntityField(this, "autoEject");
			}
			break;
		case 2:
			callCollectors();
			break;
		case 3:
			eraseMemory();
			break;
		default:
			break;
		}
	}
	
	private void callCollectors()
	{
        double range = 64D;
        AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(this.xCoord-range,this.yCoord,this.zCoord-range,this.xCoord+1.0D+range,this.yCoord+1.0D+range,this.zCoord+1.0D+range);
        List<CollectorEntity> eList = this.worldObj.getEntitiesWithinAABB(CollectorEntity.class, searchArea);
        if(!eList.isEmpty())
        {
        	Iterator<CollectorEntity> eListIterator=eList.iterator();
        	while(eListIterator.hasNext())
        	{
        		CollectorEntity fbbe = eListIterator.next();
        		fbbe.currentTask=CollectorEntity.Tasks.RECHARGE;
        		fbbe.charger=this;
        	}
        }
	}
	
	private void eraseMemory()
	{
		for(int i=0;i<this.chargeSlot.size();i++)
		{
			if(this.chargeSlot.get(i)!=null)
			{
				ItemStack item = this.chargeSlot.get(i);
				if(item.getItem() instanceof CollectorItem)
				{
					if(item.stackTagCompound!=null && item.stackTagCompound.hasKey("hopperxyz"))
					{
						int[] clearCoord={0,0,0};
						item.stackTagCompound.setIntArray("hopperxyz",clearCoord);
					}
				}
			}
		}
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }
}
