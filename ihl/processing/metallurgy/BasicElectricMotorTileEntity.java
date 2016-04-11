package ihl.processing.metallurgy;

import java.util.Iterator;
import java.util.List;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.network.NetworkManager;
import ihl.IHLMod;
import ihl.flexible_cable.FlexibleCableHolderBaseTileEntity;
import ihl.flexible_cable.IHLGrid;
import ihl.flexible_cable.NodeEntity;
import ihl.interfaces.IEnergyNetNode;
import ihl.utils.IHLUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BasicElectricMotorTileEntity extends FlexibleCableHolderBaseTileEntity implements IHasGui, INetworkClientTileEntityEventListener, IEnergySink{

    public final ElectricEngineInvSlot engine;
	public short progress;
	protected short operationLength=600;
	public boolean isGuiScreenOpened=false;
	protected final double energyConsume=0.1D;
    public double energy;
    public int maxStorage=128;
	private boolean addedToEnergyNet=false;
	
	public BasicElectricMotorTileEntity()
	{
		super();
		engine = new ElectricEngineInvSlot(this, "engine", 0, Access.IO, 1, 1);
	}
	

	@Override
	public String getInventoryName() {
		return "Lathe";
	}
	
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
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
	public void setFacing(short facing1)
	{
		short facing2 = (short) Math.max(facing1, 2);
		double d=0.3D;
		double f=-0.1D;
        if (IC2.platform.isSimulating()&&this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
		double range = 2D;
		AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(connectionX-range,connectionY-range,connectionZ-range,connectionX+range,connectionY+range,connectionZ+range);
		List<NodeEntity> nodeList = worldObj.getEntitiesWithinAABB(NodeEntity.class, searchArea);
		super.setFacing(facing2);
		switch(getFacing())
		{
			case 4:
				setConnectionX(this.xCoord+0.5D);
				setConnectionY(this.yCoord+d);
				setConnectionZ(this.zCoord+1D-f);
				break;
			case 5:
				setConnectionX(this.xCoord+0.5D);
				setConnectionY(this.yCoord+d);
				setConnectionZ(this.zCoord+f);
				break;
			case 2:
				setConnectionX(this.xCoord+1D-f);
				setConnectionY(this.yCoord+d);
				setConnectionZ(this.zCoord+0.5D);
				break;
			case 3:
				setConnectionX(this.xCoord+f);
				setConnectionY(this.yCoord+d);
				setConnectionZ(this.zCoord+0.5D);
				break;
			default:
				setConnectionX(this.xCoord+1D-f);
				setConnectionY(this.yCoord+d);
				setConnectionZ(this.zCoord+0.5D);
				break;
				
		}
		if(!nodeList.isEmpty())
		{
			Iterator ei = nodeList.iterator();
        	while(ei.hasNext())
        	{
        		NodeEntity ne=(NodeEntity) ei.next();
        		if((ne.prevAnchorEntity==null||ne.nextAnchorEntity==null) && this.cableListContains(ne.getChainUniqueID()))
        		{
        			ne.setVirtualNodePos(connectionX,connectionY,connectionZ);
        		}
        	}
		}
        if (IC2.platform.isSimulating()&&!this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
	}
	
	@Override
	public double getDemandedEnergy() 
	{
		return this.maxStorage-this.energy;
	}

	@Override
	public int getSinkTier() {
		return 4;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount,
			double voltage) {
			if(this.energy<this.maxStorage)
			{
				this.energy+=amount;
	            return 0.0D;
			}
	        else
	        {
	            return amount;
	        }
	}
	
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) 
	{
		return this.getFacing()!=(short)side;
	}
	

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("progress", this.progress);
        nbt.setDouble("energy", this.energy);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.progress = nbt.getShort("progress");
        this.energy=nbt.getDouble("energy");
	}

	@Override
	public void onNetworkEvent(EntityPlayer player, int event) 
	{
		switch(event)
		{
		case 0:
			this.isGuiScreenOpened=false;
			break;
		}	
	}
	
	public abstract void operate();
	
    @Override
	public void updateEntityServer()
    {
            if(this.gridID!=-1 && this.getGrid().energy>0D && this.energy<this.maxStorage)
            {
            	this.energy+=energyConsume*10D;
            	this.getGrid().drawEnergy(energyConsume*10D, this);
            }
        if (this.canOperate() && this.engine.correctContent() && this.isGuiScreenOpened && this.energy>=this.energyConsume/this.engine.getEfficiency())
        {
            this.energy-=this.energyConsume/this.engine.getEfficiency();
            if (this.progress == 0)
            {
                IC2.network.get().initiateTileEntityEvent(this, 0, true);
            }
            ++this.progress;
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
        }
    	
    }
	
	public abstract List[] getInput();
	public abstract boolean canOperate();
	@Override
	public void onGuiClosed(EntityPlayer arg0) {}

	public int getEnergy() 
	{
		return (int)this.energy;
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
	
    public int gaugeProgressScaled(int i)
    {
        return this.progress * i / this.operationLength;
    }


	public void setEnergy(int value) 
	{
		this.energy=value;
	}


	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return true;
	}
	
	@Override
	public double getMaxAllowableVoltage() 
	{
		return 64000D;
	}
	
	@Override
	public double getEnergyAmountThisNodeWant()
	{
		return this.energy-this.maxStorage;
	}
	
	public double drawEnergyToGrid(double amount)
	{
		return 0d;
	}
	
	public void injectEnergyInThisNode(double amount, double voltage) 
	{
		this.energy+=amount;
	}

}
