package ihl.processing.chemistry;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.IC2Items;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.AudioSource;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.network.NetworkManager;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ihl.IHLMod;
import ihl.utils.IHLInvSlotDischarge;
import ihl.utils.IHLUtils;

public class ElectricEvaporatorTileEntity extends EvaporatorTileEntity implements IEnergySink, IUpgradableBlock, INetworkTileEntityEventListener
{
    public final InvSlotUpgrade upgradeSlot;
    private int tier;
    private int lastTier;
    public int maxStorage;
    private int defaultMaxStorage;
    private double energy;
    public final int defaultEnergyConsume;
    public final int defaultOperationLength;
    public final int defaultTier;
    public int energyConsume;
    public AudioSource audioSource;
    private static final int EventStart = 0;
    private static final int EventInterrupt = 1;
    private static final int EventStop = 2;
	private int updateChecksum=0;
	private boolean addedToEnergyNet=false;

	public ElectricEvaporatorTileEntity()
    {
    	super();
        this.defaultEnergyConsume = this.energyConsume = 8;
        this.defaultOperationLength = this.maxProgress = 400;
        this.energy=0D;
        this.lastTier = this.tier = this.defaultTier = 1;
        this.maxStorage = this.defaultMaxStorage = defaultEnergyConsume * defaultOperationLength;
    	this.fuelSlot = new IHLInvSlotDischarge(this, 1, Access.IO, this.tier, InvSlot.InvSide.BOTTOM);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 4, 4);
    }
    
    @Override
	public void onLoaded()
    {
        super.onLoaded();
        if (IC2.platform.isSimulating()&&!this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
    }
    
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("tier");
		fields.add("maxStorage");
		return fields;
    }
    
	public void setOverclockRates()
    {
		int speedUp=0;
		int tierUp=0;
		int capacityUp=0;
		int checksum=0;
		for(int i=0;i<this.upgradeSlot.size();i++)
		{
			if(this.upgradeSlot.get(i)!=null)
			{
				if(this.upgradeSlot.get(i).getItemDamage()==IC2Items.getItem("overclockerUpgrade").getItemDamage())
				{
					speedUp+=this.upgradeSlot.get(i).stackSize;
				}
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
		checksum=speedUp*4096+tierUp*64+capacityUp;
		if(this.updateChecksum!=checksum)
		{
			this.maxProgress=(short)Math.max(this.defaultOperationLength*Math.pow(0.7D, speedUp),2D);
			this.maxStorage=this.defaultMaxStorage + capacityUp*10000;
	        IC2.network.get().updateTileEntityField(this, "maxStorage");
			this.energyConsume=(int) Math.min(this.defaultEnergyConsume*Math.pow(1.6D, speedUp),this.maxStorage);
			this.tier=this.defaultTier+tierUp;
			IC2.network.get().updateTileEntityField(this, "tier");
	        this.updateChecksum=checksum;
		};
    }
	
	@Override
	public void onUnloaded()
    {
        super.onUnloaded();

        if (IC2.platform.isRendering() && this.audioSource != null)
        {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        
        if (IC2.platform.isSimulating()&&this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
    }
    
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.energy=nbttagcompound.getDouble("energy");
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", this.energy);
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing()!=(short)side && side!=0 && side!=1;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		if(this.outputSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.outputSlot.get()));
		if(this.fuelSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.fuelSlot.get()));
		if(this.emptyFluidItemsSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.emptyFluidItemsSlot.get()));
		if(this.upgradeSlot.get(0)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(0)));
		if(this.upgradeSlot.get(1)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(1)));
		if(this.upgradeSlot.get(2)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(2)));
		if(this.upgradeSlot.get(3)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(3)));
		return new ItemStack(IHLMod.electricEvaporatorBlock,1);
	}
    
    public int getGUIEnergy(int i)
    {
    	if(this.energy<Float.MAX_VALUE)
    	{
    		return Math.round((float)this.energy/this.maxStorage*i);
    	}
    	else
    	{
    		return Math.round((float)(this.energy/this.maxStorage)*i);
    	}
    }

    @Override
	public boolean enableUpdateEntity()
    {
        return true;
    }
    @Override
	public void updateEntityClient()
    {
    if(((IHLInvSlotDischarge)this.fuelSlot).tier!=this.tier)
    {
    	((IHLInvSlotDischarge)this.fuelSlot).tier=this.tier;
    }
    }
    @Override
	public void updateEntityServer()
    {
        if(((IHLInvSlotDischarge)this.fuelSlot).tier!=this.tier)
        {
        	((IHLInvSlotDischarge)this.fuelSlot).tier=this.tier;
        }
    	if(IC2.platform.isSimulating())
    	{
        boolean needsInvUpdate = false;
        this.setOverclockRates();

    	if(this.getDemandedEnergy() > 1.0D)
    	{
    		double amount = ((IHLInvSlotDischarge)this.fuelSlot).discharge(this.getDemandedEnergy(), false);
    		this.energy += amount;
    	}
        if (this.needsFluid())
        {
       		IHLUtils.handleFluidSlotsBehaviour(null, fluidItemsSlot, emptyFluidItemsSlot, fluidTank);
        }
        
        if (this.canOperate() && this.energy >= this.energyConsume)
        {
            this.setActive(true);

            if (this.progress == 0)
            {
                IC2.network.get().initiateTileEntityEvent(this, 0, true);
            }
            ++this.progress;
            this.energy -= this.energyConsume;
            if (this.progress >= this.maxProgress)
            {
                this.operate();
                needsInvUpdate = true;
                this.progress = 0;
                IC2.network.get().initiateTileEntityEvent(this, 2, true);
            }
        }
        else
        {
            if (this.progress != 0 && this.getActive())
            {
                IC2.network.get().initiateTileEntityEvent(this, 1, true);
            }
            if (!this.canOperate())
            {
                this.progress = 0;
            }
            this.setActive(false);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i)
        {
            ItemStack stack = this.upgradeSlot.get(i);

            if (stack != null && stack.getItem() instanceof IUpgradeItem && ((IUpgradeItem)stack.getItem()).onTick(stack, this))
            {
                needsInvUpdate = true;
            }
        }
    	}
    }
    
    private static int applyModifier(int base, int extra, double multiplier)
    {
        double ret = Math.round(((double)base + (double)extra) * multiplier);
        return ret > 2.147483647E9D ? Integer.MAX_VALUE : (int)ret;
    }
    
    @Override
	public void onNetworkEvent(int event)
    {
        if (this.audioSource == null && this.getStartSoundFile() != null)
        {
            this.audioSource = IC2.audioManager.createSource(this, this.getStartSoundFile());
        }

        switch (event)
        {
            case 0:
                if (this.audioSource != null)
                {
                    this.audioSource.play();
                }

                break;

            case 1:
                if (this.audioSource != null)
                {
                    this.audioSource.stop();
                }

                break;

            case 2:
                if (this.audioSource != null)
                {
                    this.audioSource.stop();
                }
                
        }
    }

    public String getStartSoundFile()
    {
        return "Machines/Electro Furnace/ElectroFurnaceLoop.ogg";
    }

    /**
     * Returns the name of the inventory
     */
    @Override
	public String getInventoryName()
    {
        return "Electric evaporator";
    }

    @Override
	public ContainerBase getGuiContainer(EntityPlayer entityPlayer)
    {
        return new ElectricEvaporatorContainer(entityPlayer, this);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin)
    {
        return new ElectricEvaporatorGui(new ElectricEvaporatorContainer(entityPlayer, this));
    }

    @Override
    public boolean getGui(EntityPlayer player)
    {
			return this instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(player, this) : true) : false;
    }
    
    @Override
	public int mX()
	{
		switch(this.getFacing())
		{
		case 4:
		return -1;
		case 5:
		return 1;
		default:
		return 0;
		}
	}
    
    @Override
	public int mZ()
	{
		switch(this.getFacing())
		{
		case 2:
		return -1;
		case 3:
		return 1;
		default:
		return 0;
		}
	}
    
	@Override
	public double getEnergy() 
	{
		return this.energy;
	}

	@Override
	public boolean useEnergy(double amount) {
        if (this.energy >= amount)
        {
            this.energy -= amount;
            return true;
        }
        else
        {
            return false;
        }
	}

	public void setEnergy(double i) {
		this.energy=i;
	}

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
	public boolean acceptsEnergyFrom(TileEntity emitter,
			ForgeDirection direction) {
		return true;
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
		properties.add(UpgradableProperty.Transformer);
		return properties;
	}
}