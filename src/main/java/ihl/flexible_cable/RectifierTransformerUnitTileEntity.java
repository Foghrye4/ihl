package ihl.flexible_cable;

import java.util.List;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ihl.interfaces.IEnergyNetNode;
import ihl.interfaces.IMultiPowerCableHolder;
import ihl.utils.IHLUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class RectifierTransformerUnitTileEntity extends TileEntityInventory implements INetworkClientTileEntityEventListener, IEnergySink, IMultiPowerCableHolder {

	public boolean addedToEnergyNet=false;
	public SubRTUEnergyNetNode[] energyNetNodes = new SubRTUEnergyNetNode[2];
	public float mode = 1f;
	public static float modeMultiplier = 2f;
	public static float modeMax = 128f;
	public static float modeMin = 1/modeMax;
	public AxisAlignedBB aabb1;// Input zone "0"
	public AxisAlignedBB aabb2;// Output zone "1"
	public AxisAlignedBB aabb1_1;
	public AxisAlignedBB aabb2_1;
	public boolean checkCables=true;
	
	public RectifierTransformerUnitTileEntity()
	{
		super();
		for(short i=0;i<2;i++)
		{
			energyNetNodes[i] = new SubRTUEnergyNetNode(this, i);
		}
		aabb1 = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord+1d, this.zCoord, this.xCoord+0.5d, this.yCoord+1.1d, this.zCoord+1d);
		aabb2 = AxisAlignedBB.getBoundingBox(this.xCoord+0.5d, this.yCoord+1d, this.zCoord, this.xCoord+1d, this.yCoord+1.1d, this.zCoord+1d);
		aabb1_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.2d, this.yCoord+1.4d, this.zCoord+0.45d, this.xCoord+0.3d, this.yCoord+1.5d, this.zCoord+0.55d);
		aabb2_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.7d, this.yCoord+1.4d, this.zCoord+0.45d, this.xCoord+0.8d, this.yCoord+1.5d, this.zCoord+0.55d);
	}

    @Override
	public void onLoaded()
    {
        super.onLoaded();
		for(short i=0;i<2;i++)
		{
			energyNetNodes[i].onLoaded();
		}
        if (IC2.platform.isSimulating()&&!this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
		this.setupInteractingSpots(this.getFacing());
    }
    
    @Override
	public void onUnloaded()
    {
		for(short i=0;i<2;i++)
		{
			energyNetNodes[i].onUnloaded();
		}
        if (IC2.platform.isSimulating() && this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        super.onUnloaded();
    }
    
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("mode");
		return fields;
    }
    
	@Override
	public void setFacing(short facing1)
	{
		this.removeAttachedChains();
		short facing2 = (short) Math.max(facing1, 2);
		super.setFacing(facing2);
		this.setupInteractingSpots(facing2);
	}
	
	public void setupInteractingSpots(short facing2)
	{
		double yStart=this.yCoord+0.98d;
		switch (facing2)
		{
		case 2:
			aabb1 = AxisAlignedBB.getBoundingBox(this.xCoord, yStart, this.zCoord, this.xCoord+0.5d, this.yCoord+1.1d, this.zCoord+1d);
			aabb2 = AxisAlignedBB.getBoundingBox(this.xCoord+0.5d, yStart, this.zCoord, this.xCoord+1d, this.yCoord+1.1d, this.zCoord+1d);
			aabb1_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.2d, this.yCoord+1.4d, this.zCoord+0.45d, this.xCoord+0.3d, this.yCoord+1.5d, this.zCoord+0.55d);
			aabb2_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.7d, this.yCoord+1.4d, this.zCoord+0.45d, this.xCoord+0.8d, this.yCoord+1.5d, this.zCoord+0.55d);
			break;
		case 5:
			aabb1 = AxisAlignedBB.getBoundingBox(this.xCoord, yStart, this.zCoord, this.xCoord+1d, this.yCoord+1.1d, this.zCoord+0.5d);
			aabb2 = AxisAlignedBB.getBoundingBox(this.xCoord, yStart, this.zCoord+0.5d, this.xCoord+1d, this.yCoord+1.1d, this.zCoord+1d);
			aabb1_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.45d, this.yCoord+1.4d, this.zCoord+0.2d, this.xCoord+0.55d, this.yCoord+1.5d, this.zCoord+0.3d);
			aabb2_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.45d, this.yCoord+1.4d, this.zCoord+0.7d, this.xCoord+0.55d, this.yCoord+1.5d, this.zCoord+0.8d);
			break;
		case 3:
			aabb2 = AxisAlignedBB.getBoundingBox(this.xCoord, yStart, this.zCoord, this.xCoord+0.5d, this.yCoord+1.1d, this.zCoord+1d);
			aabb1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.5d, yStart, this.zCoord, this.xCoord+1d, this.yCoord+1.1d, this.zCoord+1d);
			aabb2_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.2d, this.yCoord+1.4d, this.zCoord+0.45d, this.xCoord+0.3d, this.yCoord+1.5d, this.zCoord+0.55d);
			aabb1_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.7d, this.yCoord+1.4d, this.zCoord+0.45d, this.xCoord+0.8d, this.yCoord+1.5d, this.zCoord+0.55d);
			break;
		case 4:
			aabb2 = AxisAlignedBB.getBoundingBox(this.xCoord, yStart, this.zCoord, this.xCoord+1d, this.yCoord+1.1d, this.zCoord+0.5d);
			aabb1 = AxisAlignedBB.getBoundingBox(this.xCoord, yStart, this.zCoord+0.5d, this.xCoord+1d, this.yCoord+1.1d, this.zCoord+1d);
			aabb2_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.45d, this.yCoord+1.4d, this.zCoord+0.2d, this.xCoord+0.55d, this.yCoord+1.5d, this.zCoord+0.3d);
			aabb1_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.45d, this.yCoord+1.4d, this.zCoord+0.7d, this.xCoord+0.55d, this.yCoord+1.5d, this.zCoord+0.8d);
			break;
		default:
			aabb1 = AxisAlignedBB.getBoundingBox(this.xCoord, yStart, this.zCoord, this.xCoord+0.5d, this.yCoord+1.1d, this.zCoord+1d);
			aabb2 = AxisAlignedBB.getBoundingBox(this.xCoord+0.5d, yStart, this.zCoord, this.xCoord+1d, this.yCoord+1.1d, this.zCoord+1d);
			aabb1_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.2d, this.yCoord+1.4d, this.zCoord+0.45d, this.xCoord+0.3d, this.yCoord+1.5d, this.zCoord+0.55d);
			aabb2_1 = AxisAlignedBB.getBoundingBox(this.xCoord+0.7d, this.yCoord+1.4d, this.zCoord+0.45d, this.xCoord+0.8d, this.yCoord+1.5d, this.zCoord+0.55d);
			break;
		}
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("rectifierTransformerUnit");
	}
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) 
	{
		return this.getFacing()!=(short)side;
	}
	

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList energyNetNodeNBTList = new NBTTagList();
        for(SubRTUEnergyNetNode node:this.energyNetNodes)
        {
        	energyNetNodeNBTList.appendTag(node.writeToNBT());
        }
        nbt.setTag("energyNetNodes", energyNetNodeNBTList);
        nbt.setFloat("mode",this.mode);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList energyNetNodeNBTList=nbt.getTagList("energyNetNodes", 10);
        for(int i=0;i<2;i++)
        {
            this.energyNetNodes[i].readFromNBT(energyNetNodeNBTList.getCompoundTagAt(i));
        }
        this.mode=nbt.getFloat("mode");
        this.mode=this.mode==0f?1f:this.mode;
	}
	
	@Override
	public void onNetworkEvent(EntityPlayer player, int event) 
	{
		switch(event)
		{
		case 0:
			break;
		}	
	}
	
	public void switchModeUp()
	{
		if(this.mode<modeMax)
		{
			this.mode*=modeMultiplier;
			if(this.energyNetNodes[1].getGridID()!=-1)
			{
				if(this.energyNetNodes[0].getGridID()!=-1)
				{
					this.energyNetNodes[1].getGrid().injectEnergy(0, this.energyNetNodes[0].getGrid().getSinkVoltage(this.energyNetNodes[0])*this.mode, this.energyNetNodes[1]);
				}
				else if(this.energyNetNodes[1].getGrid().energy>=1d)
				{
					this.energyNetNodes[1].getGrid().injectEnergy(0, 400d*this.mode, this.energyNetNodes[1]);
				}
			}
			IC2.network.get().updateTileEntityField(this, "mode");
		}
	}
	
	public void switchModeDown()
	{
		if(this.mode>modeMin)
		{
			this.mode/=modeMultiplier;;
			if(this.energyNetNodes[1].getGridID()!=-1)
			{
				if(this.energyNetNodes[0].getGridID()!=-1)
				{
					this.energyNetNodes[1].getGrid().injectEnergy(0, this.energyNetNodes[0].getGrid().getSinkVoltage(this.energyNetNodes[0])*this.mode, this.energyNetNodes[1]);
				}
				else if(this.energyNetNodes[1].getGrid().energy>=1d)
				{
					this.energyNetNodes[1].getGrid().injectEnergy(0, 400d*this.mode, this.energyNetNodes[1]);
				}
			}
			IC2.network.get().updateTileEntityField(this, "mode");
		}
	}
	
    @Override
    public void onNetworkUpdate(String field)
    {
        if (field.equals("facing") && this.prevFacing != this.getFacing())
    	{
    		this.setupInteractingSpots(this.getFacing());
    	}
        super.onNetworkUpdate(field);
    }

	
    @Override
	public void updateEntityServer()
    {
    	
    }
	
	@Override
	public String getInventoryName() 
	{
		return "RTU";
	}
	
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,ForgeDirection direction) 
	{
		return !direction.equals(ForgeDirection.UP);
	}

	@Override
	public double getDemandedEnergy() 
	{
        if(this.energyNetNodes[1].getGridID()!=-1 && this.energyNetNodes[1].getGrid().energy<1D)
        {
        	return Integer.MAX_VALUE;
        }
        else
        {
        	return 0d;
        }
	}

	@Override
	public int getSinkTier() 
	{
		return 4;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount,	double voltage) 
	{
		if(getDemandedEnergy()>0d)
		{
			this.energyNetNodes[1].getGrid().injectEnergy(amount, 400d*this.mode, this.energyNetNodes[1]);
			return 0d;
		}
		return amount;
	}
	
	@Override
	public boolean isCableRemoved(int chainUniqueID) 
	{
		if(!checkCables)
		{
			return false;
		}
		for(SubRTUEnergyNetNode sen:energyNetNodes)
		{
			if(!sen.isCableRemoved(chainUniqueID))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public IEnergyNetNode getEnergyNetNode(short facing) 
	{
		return this.energyNetNodes[facing];
	}

	@Override
	public short getSide(EntityPlayer player) 
	{
        if(IHLUtils.isPlayerLookingAt(player, aabb1))
        {
    		return 0;
        }
        else if(IHLUtils.isPlayerLookingAt(player, aabb2))
        {
    		return 1;
        }
        else
        {
        	return -1;
        }
	}
	
	@Override
	public void removeAttachedChains() 
	{
		for(short i=0;i<2;i++)
		{
			energyNetNodes[i].removeAttachedChains();
		}

	}

	public boolean isTileEntityInvalid() {
		return this.tileEntityInvalid;
	}
	
	
}
