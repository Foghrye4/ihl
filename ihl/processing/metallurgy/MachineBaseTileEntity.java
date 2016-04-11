package ihl.processing.metallurgy;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.AudioSource;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.network.NetworkManager;
import ihl.processing.chemistry.ApparatusProcessableInvSlot;
import ihl.recipes.UniversalRecipeOutput;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class MachineBaseTileEntity extends TileEntityInventory implements IHasGui, IEnergySink
{     
	public short progress;
	protected short operationLength=200;
	private AudioSource startAS;
    private AudioSource loopAS;
    private AudioSource stopAS;
    protected double energy;
    public int maxStorage;
    public int energyConsume;
    public final ApparatusProcessableInvSlot input;
	private boolean addedToEnergyNet=false;
	
    public MachineBaseTileEntity(int inputCount)
    {
    	input = new ApparatusProcessableInvSlot(this, "input", 0, Access.IO, inputCount, 64);
    	maxStorage=128;
    	energyConsume=5;
    }
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		return fields;
    }
	
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.progress = nbttagcompound.getShort("progress");
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("progress", this.progress);
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
	public void onUnloaded()
    {
        if (IC2.platform.isSimulating()&&this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        
        super.onUnloaded();
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing()!=(short)side;
	}
	
	@Override
	public void setFacing(short facing1)
	{
        if (IC2.platform.isSimulating()&&this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
		super.setFacing(facing1);
        if (IC2.platform.isSimulating()&&!this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
	}
	
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
    
    public abstract String getStartSoundFile();
    
    public abstract String getLoopSoundFile();

    public abstract String getStopSoundFile();

    @Override
	public void updateEntityServer()
    {
        if(this.energy>this.maxStorage)
        {
        	this.energy=this.maxStorage;
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
            
            if (this.progress >= this.operationLength)
            {
                this.operate();
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
    }

    protected int mX()
	{
		switch(this.getFacing())
		{
		case 4:
		return 1;
		case 5:
		return -1;
		default:
		return 0;
		}
	}
    
    protected int mY()
	{
		switch(this.getFacing())
		{
		case 0:
		return -1;
		case 1:
		return 1;
		default:
		return 0;
		}
	}
	
    protected int mZ()
	{
		switch(this.getFacing())
		{
		case 2:
		return 1;
		case 3:
		return -1;
		default:
		return 0;
		}
	}
	
    @Override
	public abstract String getInventoryName();
	
	@Override
	@SideOnly(Side.CLIENT)
	public abstract GuiScreen getGui(EntityPlayer arg0, boolean arg1);
   
	@Override
	public abstract ContainerBase<?> getGuiContainer(EntityPlayer arg0);
	
	@Override
	public void onGuiClosed(EntityPlayer arg0) {}

    public int gaugeProgressScaled(int i)
    {
        return this.progress * i / this.operationLength;
    }
    
    public boolean canOperate()
    {
    	return getOutput()!=null;
    }
    
    public abstract UniversalRecipeOutput getOutput();

	public List[] getInput()
	{
		return new List[]{null, this.input.getItemStackList()};
	}
	

	@Override
	public double getDemandedEnergy()
	{
		 return this.maxStorage - this.energy;
	}
	
	@Override
	public int getSinkTier()
	{
		return 4;
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
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) 
	{
		return true;
	}

	
    public abstract void operate();
	public short getEnergy() {
		return (short)this.energy;
	}
	public void setEnergy(int value)
	{
		this.energy=value;
	}
	public int getGUIEnergy(int i) {
    	if(this.energy<Float.MAX_VALUE)
    	{
    		return Math.round((float)this.energy/this.maxStorage*i);
    	}
    	else
    	{
    		return Math.round((float)(this.energy/this.maxStorage)*i);
    	}
	}
}
