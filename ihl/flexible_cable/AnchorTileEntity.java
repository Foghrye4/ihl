package ihl.flexible_cable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ihl.interfaces.IEnergyNetNode;
import ihl.interfaces.IMultiPowerCableHolder;
import ihl.utils.IHLUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class AnchorTileEntity extends TileEntityBlock implements IEnergySink, IEnergySource, IMultiPowerCableHolder 
{
	public boolean addedToEnergyNet=false;
	public final Set<TileEntity> sacrifices = new HashSet<TileEntity>();
	public SubAnchorEnergyNetNode[] energyNetNodes = new SubAnchorEnergyNetNode[6];
	public boolean[] hasCableOnSide=new boolean [] {false,false,false,false,false,false};
	public int checksum=-1;
	public boolean checkCables=true;
	
	public AnchorTileEntity()
	{
		super();
		for(short i=0;i<6;i++)
		{
			energyNetNodes[i] = new SubAnchorEnergyNetNode(this, i);
		}
	}
	
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("hasCableOnSide");
		return fields;
    }
	
    @Override
	public void onLoaded()
    {
        super.onLoaded();
		for(short i=0;i<6;i++)
		{
			energyNetNodes[i].onLoaded();
		}
        if (IC2.platform.isSimulating()&&!this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
    }
    
    @Override
    public void onUnloaded()
    {
        if (IC2.platform.isSimulating())
        {
        	if(this.addedToEnergyNet)
        	{
        		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            	this.addedToEnergyNet = false;
        	}
    		for(SubAnchorEnergyNetNode sen:energyNetNodes)
    		{
    			sen.onUnloaded();
    		}

        }
    }
    
    @Override
    public boolean wrenchCanRemove(EntityPlayer var1)
    {
    	return false;
    }
    
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
    
    @Override
	public void updateEntityServer()
    {
    	if(!sacrifices.isEmpty())
    	{
    		Iterator<TileEntity> victimIterator = sacrifices.iterator();
    		while(victimIterator.hasNext())
    		{
    			TileEntity sacrifice = victimIterator.next();
            	ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, sacrifice.xCoord+0.5D, sacrifice.yCoord+0.5D, sacrifice.zCoord+0.5D, 3F, 0.3F, ExplosionIC2.Type.Normal, null, 0);
            	explosion.doExplosion();
            	sacrifices.remove(sacrifice);
    		}

    	}
		int newchecksum=0;
		for(short i=0;i<6;i++)
		{
			if(this.hasCableOnSide[i])
			{
				newchecksum++;
			}
		}
		if(newchecksum!=checksum)
		{
			IC2.network.get().updateTileEntityField(this, "hasCableOnSide");	   							
			checksum=newchecksum;
		}
    }

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList energyNetNodeNBTList = new NBTTagList();
        for(SubAnchorEnergyNetNode node:this.energyNetNodes)
        {
        	energyNetNodeNBTList.appendTag(node.writeToNBT());
        }
        nbt.setTag("energyNetNodes", energyNetNodeNBTList);
        nbt.setBoolean("checkCables", this.checkCables);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
		if(!nbt.hasKey("energyNetNodes"))
        {
        	short facing = nbt.getShort("facing");
            int gridID=nbt.getInteger("gridID");
            this.energyNetNodes[facing].setGrid(gridID);
            NBTTagList cableNBTList=nbt.getTagList("cableList", 10);
            for(int i=0;i<cableNBTList.tagCount();i++)
            {
            	this.energyNetNodes[facing].addCable(cableNBTList.getCompoundTagAt(i));
            }
        }
		else
		{
	        NBTTagList energyNetNodeNBTList=nbt.getTagList("energyNetNodes", 10);
	        for(int i=0;i<6;i++)
	        {
	            this.energyNetNodes[i].readFromNBT(energyNetNodeNBTList.getCompoundTagAt(i));
	        }
		}
		this.checkCables=nbt.getBoolean("checkCables");
	}

	@Override
	public void setFacing(short facing)
	{
		this.energyNetNodes[facing].removeAttachedChains();
    	boolean removeTE=true;
		for(short i=0;i<6;i++)
		{
			if(!energyNetNodes[i].getCableList().isEmpty())
			{
				removeTE=false;
			}
		}
		if(removeTE)
		{
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) 
	{
		switch(direction)
		{
		case UP:
			return this.energyNetNodes[0].getGridID()!=-1;
		case DOWN:
			return this.energyNetNodes[1].getGridID()!=-1;
		case SOUTH:
			return this.energyNetNodes[2].getGridID()!=-1;
		case NORTH:
			return this.energyNetNodes[3].getGridID()!=-1;
		case EAST:
			return this.energyNetNodes[4].getGridID()!=-1;
		case WEST:
			return this.energyNetNodes[5].getGridID()!=-1;
		default:
			return false;
		}
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) 
	{
		return this.acceptsEnergyFrom(receiver, direction);
	}

	@Override
	public double getOfferedEnergy() 
	{
		double offeredEnergy=0d;
		for(short i=0;i<6;i++)
		{
			offeredEnergy+=energyNetNodes[i].getEnergyOfferedByGrid();
		}
		return offeredEnergy;
	}

	@Override
	public void drawEnergy(double amount) 
	{
		for(short i=0;i<6;i++)
		{
			amount-=energyNetNodes[i].drawEnergyFromGrid(amount);
			double voltage = energyNetNodes[i].getVoltage();
			if(voltage>500D)
			{
				Set<TileEntity> teset = new HashSet<TileEntity>(); 
				ForgeDirection direction = ForgeDirection.getOrientation(i).getOpposite();
				TileEntity te = EnergyNet.instance.getNeighbor(this, direction);
				teset.add(te);
				TileEntity te1 = this.getSink(te, teset);
				if(te1 instanceof IEnergyNetNode)
				{
					if(((IEnergyNetNode)te1).getMaxAllowableVoltage()>=voltage)
					{
						return;
					}
				}
				if(te1!=null)
				{
					sacrifices.add(te1);
				}
			}
			if(amount<=0d)
			{
				break;
			}
		}
	}

	@Override
	public int getSourceTier() {
		return 4;
	}

	@Override
	public double getDemandedEnergy() 
	{
		double amount=0d;
		for(short i=0;i<6;i++)
		{
			amount=energyNetNodes[i].getDemandedEnergy();
		}
		return amount;
	}

	@Override
	public int getSinkTier() {
		return 4;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount,	double voltage) 
	{
		switch(directionFrom)
		{
		case UP:
			return this.energyNetNodes[0].injectEnergyToGrid(amount);
		case DOWN:
			return this.energyNetNodes[1].injectEnergyToGrid(amount);
		case SOUTH:
			return this.energyNetNodes[2].injectEnergyToGrid(amount);
		case NORTH:
			return this.energyNetNodes[3].injectEnergyToGrid(amount);
		case EAST:
			return this.energyNetNodes[4].injectEnergyToGrid(amount);
		case WEST:
			return this.energyNetNodes[5].injectEnergyToGrid(amount);
		default:
			return amount;
		}
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer)
	{
		return null;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) 
	{
		return !this.wrenchCanRemove(entityPlayer);
	}
	
	public TileEntity getSink(TileEntity te, Set<TileEntity> teset)
	{
		teset.add(te);
		while(te!=null && !(te instanceof IEnergySink))
		{
			TileEntity te1 = EnergyNet.instance.getNeighbor(te, ForgeDirection.NORTH);
			if(te1==null || teset.contains(te1))
			{
				te1 = EnergyNet.instance.getNeighbor(te, ForgeDirection.SOUTH);
			}
			else
			{
				TileEntity te2 = this.getSink(te1, teset);
				if(te2!=null)
				{
					return te2;
				}
			}
			if(te1==null || teset.contains(te1))
			{
				te1 = EnergyNet.instance.getNeighbor(te, ForgeDirection.UP);
			}
			else
			{
				TileEntity te2 = this.getSink(te1, teset);
				if(te2!=null)
				{
					return te2;
				}
			}
			if(te1==null || teset.contains(te1))
			{
				te1 = EnergyNet.instance.getNeighbor(te, ForgeDirection.DOWN);
			}
			else
			{
				TileEntity te2 = this.getSink(te1, teset);
				if(te2!=null)
				{
					return te2;
				}
			}
			if(te1==null || teset.contains(te1))
			{
				te1 = EnergyNet.instance.getNeighbor(te, ForgeDirection.WEST);
			}
			else
			{
				TileEntity te2 = this.getSink(te1, teset);
				if(te2!=null)
				{
					return te2;
				}
			}
			if(te1==null || teset.contains(te1))
			{
				te1 = EnergyNet.instance.getNeighbor(te, ForgeDirection.EAST);
			}
			else
			{
				TileEntity te2 = this.getSink(te1, teset);
				if(te2!=null)
				{
					return te2;
				}
			}
			if(te1!=null)
			{
				teset.add(te1);
			}
			te=te1;
		}
		if(te instanceof IEnergySink)
		{
			return te;
		}
		else
		{
			return null;
		}
	}

	public void checkIfNoCablesLeft() 
	{
		boolean noCables=true;
		for(SubAnchorEnergyNetNode sen:energyNetNodes)
		{
			if(!sen.getCableList().isEmpty())
			{
				noCables=false;
			}
		}
		if(noCables)
		{
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public boolean isCableRemoved(int chainUniqueID) 
	{
		if(!checkCables)
		{
			return false;
		}
		for(SubAnchorEnergyNetNode sen:energyNetNodes)
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
		return energyNetNodes[facing];
	}

	@Override
	public short getSide(EntityPlayer player) 
	{
		return IHLUtils.getFacingFromPlayerView(player, true);
	}

	@Override
	public void removeAttachedChains() 
	{
		for(short i=0;i<6;i++)
		{
			energyNetNodes[i].removeAttachedChains();
		}

	}

	public boolean isTileEntityInvalid() {
		return this.tileEntityInvalid;
	}
}
