package ihl.trans_dimensional_item_teleporter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.IC2Items;
import ic2.api.network.NetworkHelper;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ihl.IHLMod;
import ihl.utils.IHLInvSlotDischarge;

public class TDITTileEntity extends TileEntityInventory implements IEnergySink, IHasGui, IUpgradableBlock
{
    private int tier=4;
    private int defaultTier=4;
    public int maxStorage=12000;
    private int defaultMaxStorage=12000;
    private double energy=0D;
    private double energyConsume=12000D;//per full stack
    public boolean addedToEnergyNet = false;
    public final InvSlotUpgrade upgradeSlot;
    public final IHLInvSlotDischarge dischargeSlot;
    public final InvSlotOutput outputSlot;
    public final InvSlot inputSlot;

	public int updateChecksum=-1;
    
    private AudioSource startAS;
	public boolean targetSet=false;
	public int targetDimension;
	public int targetX;
	public int targetY;
	public int targetZ;
	/** 0 - Ready, 100%
	* 1 - Not enough energy 
	* 2 - Receiver not responding
	* 3 - Receiver not defined
	* 4 - Clean receiver chamber!
	*/
	public int currentStatus=0;
	public int lastStatus=0;
	private int timer=0;
	
	
    public TDITTileEntity()
    {
    	this.defaultTier=IHLMod.config.tditTier;
    	this.defaultMaxStorage=IHLMod.config.tditMaxEnergyStorage;
    	this.energyConsume=IHLMod.config.tditEnergyConsumePerStack;
        this.dischargeSlot = new IHLInvSlotDischarge(this, 0, Access.IO, this.tier, InvSlot.InvSide.SIDE);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 1, 4);
        this.outputSlot = new InvSlotOutput(this, "output", 2, 9);
        this.inputSlot = new InvSlot(this, "input", 2, InvSlot.Access.I, 9, InvSlot.InvSide.TOP);
    }
    
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("tier");
		fields.add("maxStorage");
		fields.add("currentStatus");
		return fields;
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
        this.targetSet = nbttagcompound.getBoolean("targetSet");
        this.targetDimension = nbttagcompound.getInteger("targetDimension");
        this.targetX = nbttagcompound.getInteger("targetX");
        this.targetY = nbttagcompound.getInteger("targetY");
        this.targetZ = nbttagcompound.getInteger("targetZ");
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", this.energy);
        nbttagcompound.setBoolean("targetSet", this.targetSet);
        nbttagcompound.setInteger("targetDimension", this.targetDimension);
        nbttagcompound.setInteger("targetX", this.targetX);
        nbttagcompound.setInteger("targetY", this.targetY);
        nbttagcompound.setInteger("targetZ", this.targetZ);
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
        if (IC2.platform.isRendering() && this.startAS==null)
        {
        	this.startAS = IC2.audioManager.createSource(this, PositionSpec.Center, this.getStartSoundFile(),false,false, 1F);
        }
    }
    
    @Override
	public void onUnloaded()
    {
        if (IC2.platform.isRendering() && this.startAS != null)
        {
            this.startAS.stop();
            this.startAS = null;
            IC2.audioManager.removeSources(this);
        }
        
        if (IC2.platform.isSimulating() && this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        super.onUnloaded();
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		if(this.dischargeSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.dischargeSlot.get()));
		if(this.upgradeSlot.get(0)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(0)));
		if(this.upgradeSlot.get(1)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(1)));
		if(this.upgradeSlot.get(2)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(2)));
		if(this.upgradeSlot.get(3)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(3)));
		return new ItemStack(IHLMod.tditBlock,1);
	}
	
	@Override
    public void setFacing(short facing1)
    {
    	super.setFacing(facing1);
    	this.updateChecksum=-1;
    }
    
    public boolean enableUpdateEntity()
    {
        return true;
    }
    
    public String getStartSoundFile()
    {
        return "Machines/IHL Industrial Fan/start.ogg";
    }
    
    public String getLoopSoundFile()
    {
        return "Machines/IHL Industrial Fan/loop.ogg";
    }

    public String getStopSoundFile()
    {
        return "Machines/IHL Industrial Fan/stop.ogg";
    }

    @Override
	public void updateEntityServer()
    {
    	
        if (IC2.platform.isSimulating())
        {
            this.setOverclockRates();
            if(this.timer>0)
            {
            		this.timer--;
            }
            else
            {
            	this.timer=60;
            	int energyToOperate=this.countEnergyToOperate();
           		if(energyToOperate>0 && energyToOperate<=this.energy)
            	{
            		this.sendItemStack();
            		this.energy-=energyToOperate;
            	}	
            	else if(energyToOperate==0 && this.energy>100D)
            	{
            		this.currentStatus=0;
            	}
            	else
            	{
            		this.currentStatus=1;
            	}
            	if(this.currentStatus!=this.lastStatus)
            	{
            		NetworkHelper.updateTileEntityField(this, "currentStatus");
            		this.lastStatus=this.currentStatus;
            	}

            }

        }
        if(this.dischargeSlot.tier!=this.tier)
        {
        	this.dischargeSlot.tier=this.tier;
        }
    	if(this.getDemandedEnergy() > 1.0D)
    	{
    		double amount = this.dischargeSlot.discharge(this.getDemandedEnergy(), false);
    		this.energy += amount;
    	}
        if(this.energy>this.maxStorage)
        {
        	this.energy=this.maxStorage;
        }
        
        
        if(IC2.platform.isRendering() && this.startAS!=null)
        {
        	if(this.getActive())
        	{
        			this.startAS.play();
        	}
        	else 
        	{
        		this.startAS.stop();
        	}
        }

    }
    
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
			ForgeDirection direction) {
			return true;
	}

	@Override
	public String getInventoryName() {
		return "tdit";
	}
	
	public int getStored() {
		return Math.round((float)this.energy);
	}
	
	public void setStored(int value) {
		this.energy=value;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer arg0, boolean arg1) {
		return new TDITGui(new TDITContainer(arg0, this));
	}
	
    public boolean getGui(EntityPlayer player)
    {
			return this instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(player, this) : true) : false;
    }

    @Override
	public ContainerBase<?> getGuiContainer(EntityPlayer arg0) {
		return new TDITContainer(arg0, this);
	}
	@Override
	public void onGuiClosed(EntityPlayer arg0) {}
	
	public void setOverclockRates()
    {
		int tierUp=0;
		int capacityUp=0;
		int checksum=0;
		for(int i=0;i<this.upgradeSlot.size();i++)
		{
			if(this.upgradeSlot.get(i)!=null)
			{
				if(this.upgradeSlot.get(i).getItemDamage()==IC2Items.getItem("transformerUpgrade").getItemDamage())
				{
					tierUp+=this.upgradeSlot.get(i).stackSize;
				}
				if(this.upgradeSlot.get(i).getItemDamage()==IC2Items.getItem("energyStorageUpgrade").getItemDamage())
				{
					capacityUp+=this.upgradeSlot.get(i).stackSize;
				}
			}
		}
		checksum=tierUp*64+capacityUp;
		if(this.updateChecksum!=checksum)
		{
			this.maxStorage=this.defaultMaxStorage + capacityUp*10000;
	        NetworkHelper.updateTileEntityField(this, "maxStorage");
			this.tier=this.defaultTier+tierUp;
			NetworkHelper.updateTileEntityField(this, "tier");
	        this.updateChecksum=checksum;
		}
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
	
	public void setTarget(int targetX2, int targetY2, int targetZ2, int dimesionID) {
		this.targetSet=true;
		this.targetX=targetX2;
		this.targetY=targetY2;
		this.targetZ=targetZ2;
		this.targetDimension=dimesionID;
	}
	
	public boolean canRecieve(List<ItemStack> itemStackList)
	{
		int countEmptySlots=0;
		for(int i=0;i<this.outputSlot.size();i++)
		{
			if(this.outputSlot.get(i)==null)
			{
				countEmptySlots++;
			}
		}
		return countEmptySlots>=itemStackList.size();
	}
	
	public void recieveItemStack(List<ItemStack> itemStackList)
	{
		this.outputSlot.add(itemStackList);
        for (int i = 0; i < this.upgradeSlot.size(); ++i)
        {
            ItemStack stack = this.upgradeSlot.get(i);

            if (stack != null && stack.getItem() instanceof ic2.core.upgrade.IUpgradeItem && ((IUpgradeItem)stack.getItem()).onTick(stack, this))
            {
                //needsInvUpdate = true;
            }
        }
	}
	
	private int countEnergyToOperate()
	{
		int energy = 0;
		for(int i=0;i<this.inputSlot.size();i++)
		{
			if(this.inputSlot.get(i)!=null)
			{
				energy+=this.energyConsume*this.inputSlot.get(i).stackSize/this.inputSlot.get(i).getMaxStackSize();
			}
		}
		return energy;
		
	}
	
	public void sendItemStack()
	{
		if(!this.inputSlot.isEmpty() && this.targetSet)
		{
			World targetWorld = MinecraftServer.getServer().worldServerForDimension(this.targetDimension);
			if(targetWorld!=null)
			{
				TileEntity te = targetWorld.getTileEntity(targetX, targetY, targetZ);
				if(te!=null && te instanceof TDITTileEntity)
				{
					TDITTileEntity tdit = (TDITTileEntity) te;
					List<ItemStack> itemStackList = new ArrayList<ItemStack>();
					for(int i=0;i<this.inputSlot.size();i++)
					{
						if(this.inputSlot.get(i)!=null)
						{
							itemStackList.add(this.inputSlot.get(i));
						}
					}
					if(tdit.canRecieve(itemStackList))
					{
						tdit.recieveItemStack(itemStackList);
						this.inputSlot.clear();
					}
					else
					{
						this.currentStatus=4;
					}
				}
				else
				{
					this.currentStatus=2;
				}
			}
		}
		else
		{
			this.currentStatus=3;
		}
	}

	public ItemStack getOutput(int arg0) 
	{
		return this.outputSlot.get(arg0);
	}

	public int getOutputSize() {
		return this.outputSlot.size();
	}

	public void setOutput(int arg0, ItemStack arg1) 
	{
		this.outputSlot.put(arg0, arg1);
	}

	@Override
	public Set<UpgradableProperty> getUpgradableProperties() 
	{
		Set<UpgradableProperty> properties = new HashSet<UpgradableProperty>();
		properties.add(UpgradableProperty.ItemProducing);
		properties.add(UpgradableProperty.EnergyStorage);
		return properties;
	}

	@Override
	public double getEnergy() {
		return this.energy;
	}

	@Override
	public boolean useEnergy(double arg0) {
		if(this.energy>=arg0)
		{
			this.energy-=arg0;
			return true;
		}
		else
		{
			return false;
		}
		
	}
}