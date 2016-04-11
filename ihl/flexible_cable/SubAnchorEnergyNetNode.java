package ihl.flexible_cable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.IC2;
import ihl.IHLMod;
import ihl.interfaces.IEnergyNetNode;
import ihl.utils.IHLUtils;

public class SubAnchorEnergyNetNode implements IEnergyNetNode{

	private AnchorTileEntity base;
	private short facing;
	private int gridID=-1;
	private Set<NBTTagCompound> cableList = new HashSet();
	
	public SubAnchorEnergyNetNode(AnchorTileEntity base1, short facing1)
	{
		base=base1;
		facing=facing1;
	}
	
	@Override
	public double[] getPortPos(EntityLivingBase player) 
	{
		double d=0.5D;
		double f=-0.05D;
		switch(facing)
		{
			case 0:
				return new double[]{
				(base.xCoord+d),
				(base.yCoord+1D-f),
				(base.zCoord+0.5D)};
			case 1:
				return new double[]{
				(base.xCoord+d),
				(base.yCoord+f),
				(base.zCoord+0.5D)};
			case 2:
				return new double[]{
				(base.xCoord+0.5D),
				(base.yCoord+d),
				(base.zCoord+1D-f)};
			case 3:
				return new double[]{
				(base.xCoord+0.5D),
				(base.yCoord+d),
				(base.zCoord+f)};
			case 4:
				return new double[]{
				(base.xCoord+1D-f),
				(base.yCoord+d),
				(base.zCoord+0.5D)};
			case 5:
				return new double[]{
				(base.xCoord+f),
				(base.yCoord+d),
				(base.zCoord+0.5D)};
			default:
				return new double[]{
				(base.xCoord+f),
				(base.yCoord+d),
				(base.zCoord+0.5D)};
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
		base.hasCableOnSide[this.facing]=true;
		return this.cableList.add(cable);
	}

	@Override
	public Set<NBTTagCompound> getCableList() {
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
        for(NBTTagCompound cable:this.cableList)
        {
        	cableNBTList.appendTag(cable);
        }
        nbt.setTag("cableList", cableNBTList);
        nbt.setInteger("gridID", this.gridID);
        return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList cableNBTList=nbt.getTagList("cableList", 10);
        for(int i=0;i<cableNBTList.tagCount();i++)
        {
            this.cableList.add(cableNBTList.getCompoundTagAt(i));
        }
        this.gridID=nbt.getInteger("gridID");
		if(this.gridID!=-1)
		{
			base.hasCableOnSide[this.facing]=true;
		}
	}

	public double getDemandedEnergy()
	{
		if(this.gridID==-1)
		{
			return 0D;
		}
		else
		{
			double dEnergy = this.getGrid().energy;
			if(dEnergy<1d)
			{
				return Integer.MAX_VALUE;
			}
			else
			{
				return 0D;
			}
		}
	}

	public double drawEnergyFromGrid(double amount) 
	{
		if(this.gridID!=-1 && this.getGrid().energy>0d)
		{
			double drainedEnergy = Math.min(amount, this.getGrid().energy);
			this.getGrid().drawEnergy(drainedEnergy, this);
			return drainedEnergy;
		}
		return 0D;
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
	public void remove(NBTTagCompound cable) 
	{
		if(this.cableList.remove(cable))
		{
			IHLUtils.removeChain(cable, this);
		}
		if(this.cableList.isEmpty())
		{
			base.hasCableOnSide[this.facing]=false;
			base.checkIfNoCablesLeft();
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
		for(NBTTagCompound cable:this.cableList)
		{
			if(cable.getInteger("chainUID")==chainUniqueID)
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
		Set<TileEntity> teset = new HashSet<TileEntity>(); 
		ForgeDirection direction = ForgeDirection.getOrientation(this.facing).getOpposite();
		TileEntity te = EnergyNet.instance.getNeighbor(this.base, direction);
		teset.add(te);
		TileEntity te1 = this.base.getSink(te, teset);
		if(te1==this.base)
		{
			return 0d;
		}
		if(te instanceof IEnergySink)
		{
			return ((IEnergySink)te).getDemandedEnergy();
		}
		return 0d;
	}

	public double getEnergyOfferedByGrid() 
	{
		if(this.gridID==-1)
		{
			return 0D;
		}
		else
		{
			return Math.max(this.getGrid().energy,0d);
		}
	}

	@Override
	public void injectEnergyInThisNode(double amount, double voltage) 
	{
		Set<TileEntity> teset = new HashSet<TileEntity>(); 
		ForgeDirection direction = ForgeDirection.getOrientation(this.facing).getOpposite();
		TileEntity te = EnergyNet.instance.getNeighbor(this.base, direction);
		teset.add(te);
		TileEntity te1 = this.base.getSink(te, teset);
		if(te1==this.base)
		{
			return;
		}
		if(te1!=null && voltage>500D)
		{
			if(te1 instanceof IEnergyNetNode)
			{
				if(((IEnergyNetNode)te1).getMaxAllowableVoltage()<voltage)
				{
					base.sacrifices.add(te1);
				}
			}
			else if(te1 instanceof IEnergySink)
			{
				base.sacrifices.add(te1);
			}
		}
		if(te1 instanceof IEnergySink)
		{
			amount=((IEnergySink)te1).injectEnergy(direction, amount, voltage);
		}
	}
}
