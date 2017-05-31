package ihl.flexible_cable;

import java.util.HashSet;
import java.util.Set;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.core.IC2;
import ihl.IHLMod;
import ihl.interfaces.IEnergyNetNode;
import ihl.utils.IHLUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;

public class SubRTUEnergyNetNode implements IEnergyNetNode{

	private RectifierTransformerUnitTileEntity base;
	private short side;
	private int gridID=-1;
	private Set<IHLCable> cableList = new HashSet<IHLCable>();
	
	public SubRTUEnergyNetNode(RectifierTransformerUnitTileEntity base1, short facing1)
	{
		base=base1;
		side=facing1;
	}
	
	@Override
	public double[] getPortPos(EntityLivingBase player) 
	{
		double d=0.5D;
		double f=0.25D;
		double h=1.45d;
		switch(base.getFacing())
		{
			case 0:
			case 1:
			case 2:
				switch(side)
				{
					case 0:
						return new double[]{
						(base.xCoord+f),
						(base.yCoord+h),
						(base.zCoord+d)};
					case 1:
						return new double[]{
						(base.xCoord+1d-f),
						(base.yCoord+h),
						(base.zCoord+d)};
				}
			case 3:
				switch(side)
				{
					case 1:
						return new double[]{
						(base.xCoord+f),
						(base.yCoord+h),
						(base.zCoord+d)};
					case 0:
						return new double[]{
						(base.xCoord+1d-f),
						(base.yCoord+h),
						(base.zCoord+d)};
				}
			case 4:
				switch(side)
				{
					case 1:
						return new double[]{
						(base.xCoord+d),
						(base.yCoord+h),
						(base.zCoord+f)};
					case 0:
						return new double[]{
						(base.xCoord+d),
						(base.yCoord+h),
						(base.zCoord+1d-f)};
				}
			case 5:
				switch(side)
				{
					case 0:
						return new double[]{
						(base.xCoord+d),
						(base.yCoord+h),
						(base.zCoord+f)};
					case 1:
						return new double[]{
						(base.xCoord+d),
						(base.yCoord+h),
						(base.zCoord+1d-f)};
				}
			default:
				return new double[]{
				(base.xCoord+1d-f),
				(base.yCoord+h),
				(base.zCoord+d)};
		}
	}

	@Override
	public IHLGrid getGrid() 
	{
		if(gridID!=-1)
		{
			return IHLMod.enet.getGrid(gridID);
		}
		else
		{
			return null;
		}
	}

	@Override
	public int getGridID() 
	{
		return gridID;
	}

	@Override
	public void setGrid(int newgridID)
	{
    	if(IC2.platform.isSimulating()&& base.addedToEnergyNet && base.getWorldObj()!=null)
    	{
    		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(base));
    		base.addedToEnergyNet = false;
    	}
		if(newgridID!=-1)
		{
			this.gridID=newgridID;
			IHLMod.enet.getGrid(newgridID).add(this);
		}
		else
		{
			this.gridID=-1;
		}
        if (IC2.platform.isSimulating()&& !base.addedToEnergyNet && base.getWorldObj()!=null)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(base));
            base.addedToEnergyNet = true;
        }
	}

	@Override
	public double getMaxAllowableVoltage() 
	{
		return 64000d;
	}

	@Override
	public boolean addCable(NBTTagCompound cable) 
	{
		return this.cableList.add(IHLCable.fromNBT(cable));
	}

	@Override
	public Set<IHLCable> getCableList() {
		return cableList;
	}

	@Override
	public void removeAttachedChains() 
	{
		IHLUtils.removeChains(this,this.base.getWorldObj());
	}

	public void onLoaded() 
	{
        if(gridID!=-1)
        {
        	IHLGrid grid = IHLMod.enet.getGrid(gridID);
        	grid.add(this);
        }
	}
	
	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList cableNBTList = new NBTTagList();
        for(IHLCable cable:this.cableList)
        {
        	cableNBTList.appendTag(cable.toNBT());
        }
        nbt.setTag("cableList", cableNBTList);
        nbt.setInteger("gridID", this.gridID);
        return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList cableNBTList=nbt.getTagList("cableList", 10);
        for(int i=0;i<cableNBTList.tagCount();i++)
        {
            this.cableList.add(IHLCable.fromNBT(cableNBTList.getCompoundTagAt(i)));
        }
        this.gridID=nbt.getInteger("gridID");
	}

	public double getOfferedEnergy() 
	{
		if(this.gridID==-1)
		{
			return 0D;
		}
		else
		{
			return Math.max(this.getGrid().energy, 0D);
		}
	}
	
	public double drawEnergy(double amount) 
	{
		if(this.gridID==-1)
		{
			return 0D;
		}
		else
		{
			double dEnergy = Math.min(this.getGrid().energy, amount);
			this.getGrid().energy-=dEnergy;
			return dEnergy;
		}
	}

	public double getVoltage() 
	{
		if(this.gridID==-1)
		{
			return 0D;
		}
		else
		{
			return this.getGrid().getSinkVoltage(this);
		}
	}

	@Override
	public void remove(IHLCable cable) 
	{
		if(this.cableList.remove(cable))
		{
			IHLUtils.removeChain(cable, this);
		}
	}

	public double injectEnergyToGrid(double amount) 
	{
		if(this.gridID==-1)
		{
			return amount;
		}
		else
		{
			this.getGrid().injectEnergy(amount, 400d, this);
			return 0d;
		}
	}

	@Override
	public boolean isCableRemoved(int chainUniqueID) {
		for(IHLCable cable:this.cableList)
		{
			if(cable.chainUID==chainUniqueID)
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void setCableCheck(boolean b) 
	{
		base.checkCables=b;
	}

	@Override
	public double getEnergyAmountThisNodeWant() 
	{
		if(this.side==0)
		{
			IEnergyNetNode eNode1 = this.base.getEnergyNetNode((short)1);
			if(eNode1.getGridID()!=-1 && eNode1.getGrid().energy<1d)
			{
				return Integer.MAX_VALUE;
			}
		}
		return 0;
	}

	@Override
	public void injectEnergyInThisNode(double amount, double voltage) 
	{
		if(this.side==0)
		{
			IEnergyNetNode eNode1 = this.base.getEnergyNetNode((short)1);
			if(eNode1.getGridID()!=-1)
			{
				eNode1.getGrid().injectEnergy(amount, voltage*this.base.mode, eNode1);
			}
		}
	}
}
