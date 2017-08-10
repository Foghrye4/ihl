package ihl.flexible_cable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ihl.utils.IHLUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class BatterySwitchUnitTileEntity extends FlexibleCableHolderBaseTileEntity implements INetworkClientTileEntityEventListener, IEnergySource{

	public short progress;
	protected short operationLength=200;
	public boolean isGuiScreenOpened=false;
	protected final double energyConsume=128D;
    public double energy;
    public int maxStorage=65536;
	private boolean addedToEnergyNet=false;
	public byte mode = 0;
	private Set<BatterySwitchUnitTileEntity> batteryChain = new HashSet<BatterySwitchUnitTileEntity>();
	private BatterySwitchUnitTileEntity batteryChainMaster;
	private boolean chargingMode=true;
	
	public BatterySwitchUnitTileEntity()
	{
		super();
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
	public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("batterySwitchUnit");
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
	
    @Override
	public void updateEntityServer()
    {
    	{
            if(this.chargingMode && this.gridID!=-1 && this.energy<this.maxStorage && !this.batteryChain.isEmpty())
            {
            	if(this.getGrid().energy>0D)
            	{
            		if(this.getGrid().getSinkVoltage(this)/this.batteryChain.size()>410D)
            		{
                		this.energy+=energyConsume;
                		this.getGrid().drawEnergy(energyConsume, this);
                		if(this.getGrid().getSinkVoltage(this)/this.batteryChain.size()>500D)
                		{
                			this.createChainOfExplosions();
                		}
            		}
            	}
            }
            if(this.chargingMode && this.batteryChainMaster!=null && this.gridID==-1)
            {
                if(this.batteryChainMaster.gridID!=-1 && this.batteryChainMaster.energy>0D && this.energy<this.maxStorage && !this.batteryChainMaster.batteryChain.isEmpty())
                {
                	if(this.batteryChainMaster.getGrid().getSinkVoltage(this.batteryChainMaster)/this.batteryChainMaster.batteryChain.size()>410D)
                	{
                    	this.energy+=energyConsume;
                    	this.batteryChainMaster.drawEnergy(energyConsume);
                	}
                }
            }
            if(this.chargingMode && this.gridID!=-1 && MinecraftServer.getServer().getTickCounter() % 40==0)
            {
            	this.checkBatteryChain();
            }
        	if(this.energy>=this.maxStorage)
        	{
        		this.chargingMode=false;
        	}
            if(!this.chargingMode)
            {
            	this.energy-=0.01D;
            	if(this.energy<energyConsume)
            	{
            		this.chargingMode=true;
            	}
            }
    	}
    }
	
	private void createChainOfExplosions() 
	{
		Iterator<BatterySwitchUnitTileEntity> batteryChainIterator = this.batteryChain.iterator(); 
		while(batteryChainIterator.hasNext())
		{
			BatterySwitchUnitTileEntity bsu = batteryChainIterator.next();
	        ExplosionIC2 explosion = new ExplosionIC2(bsu.worldObj, null, bsu.xCoord+0.5D, bsu.yCoord+0.5D, bsu.zCoord+0.5D, 2F, 0.3F, ExplosionIC2.Type.Normal, null, 0);
	        explosion.doExplosion();
		}
	}

	private boolean checkBatteryChain() 
	{
		boolean allright=true;
		if(!this.batteryChain.isEmpty())
		{
			Iterator<BatterySwitchUnitTileEntity> sectionsIterator = this.batteryChain.iterator();
			while(sectionsIterator.hasNext())
			{
				BatterySwitchUnitTileEntity section = sectionsIterator.next();
				if(section==null || section.isInvalid())
				{
					allright=false;
				}
			}
		}
		else
		{
			allright=false;
		}
		if(allright)
		{
			return true;
		}
		else
		{
			this.batteryChain.clear();
			boolean checking = true;
			int x=xCoord;
			int z=zCoord;
			List<Integer> xs = new ArrayList<Integer>();
			List<Integer> zs = new ArrayList<Integer>();
			xs.add(xCoord);
			zs.add(zCoord);
			this.batteryChain.add(this);
			while(checking)
			{
				if(!xs.isEmpty() && !zs.isEmpty())
				{
					x=xs.remove(0);
					z=zs.remove(0);
				}
				else
				{
					checking=false;
					break;
				}
				int[] xz = new int[] {0,1,0,-1,0};
				for(int i=0;i<xz.length-1;i++)
				{
					TileEntity te = worldObj.getTileEntity(x+xz[i], yCoord, z+xz[i+1]);
					if(te instanceof BatterySwitchUnitTileEntity)
					{
						BatterySwitchUnitTileEntity section = (BatterySwitchUnitTileEntity)te;
						if(this.batteryChain.add(section))
						{
							xs.add(section.xCoord);
							zs.add(section.zCoord);
							section.batteryChain.add(this);
							section.batteryChainMaster=this;
						}
					}
				}
			}
			return true;
		}
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return true;
	}

	@Override
	public double getOfferedEnergy() 
	{
		return this.chargingMode? 0 : this.energy;
	}

	@Override
	public void drawEnergy(double amount) 
	{
		this.energy-=amount;
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
	
	public double injectEnergy(double amount)
	{
		this.energy+=amount;
		return 0d;
	}

	@Override
	public int getSourceTier() {
		return 4;
	}
	
	@Override
	public double getMaxAllowableVoltage() 
	{
		return 64000D;
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public void injectEnergyInThisNode(double amount, double voltage) 
	{
		this.energy+=amount;
		if(voltage/this.batteryChain.size()>500D)
		{
			this.createChainOfExplosions();
		}
	}

}
