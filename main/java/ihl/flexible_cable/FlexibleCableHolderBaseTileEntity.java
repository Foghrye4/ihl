package ihl.flexible_cable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ic2.core.block.TileEntityInventory;
import ihl.IHLMod;
import ihl.interfaces.IEnergyNetNode;
import ihl.utils.IHLUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

public abstract class FlexibleCableHolderBaseTileEntity extends TileEntityInventory implements IEnergyNetNode{

	protected double connectionX;
	protected double connectionY;
	protected double connectionZ;
	protected int gridID=-1;
	protected final Set<IHLCable> cableList;
	public boolean checkCables=true;

	public FlexibleCableHolderBaseTileEntity()
	{
		super();
		cableList=new HashSet<IHLCable>();
	}
	
    @Override
	public void onLoaded()
    {
        super.onLoaded();
        if(gridID!=-1)
        {
        	IHLGrid grid = IHLMod.enet.getGrid(gridID);
        	grid.add(this);
        }
    }
    
    @Override
	public void onUnloaded()
    {
        super.onUnloaded();
        if(gridID!=-1)
        {
        	IHLGrid grid = IHLMod.enet.getGrid(gridID);
        	grid.remove(this);
        }
    }

    
	@SuppressWarnings("unchecked")
	@Override
	public void setFacing(short facing1)
	{
		short facing2 = (short) Math.max(facing1, 2);
		double range = 2D;
		AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(connectionX-range,connectionY-range,connectionZ-range,connectionX+range,connectionY+range,connectionZ+range);
		List<NodeEntity> nodeList = worldObj.getEntitiesWithinAABB(NodeEntity.class, searchArea);
		super.setFacing(facing2);
		setConnectionX(this.xCoord+0.5D);
		setConnectionY(this.yCoord+1.5D);
		setConnectionZ(this.zCoord+0.5D);
		if(!nodeList.isEmpty())
		{
			Iterator<NodeEntity> ei = nodeList.iterator();
        	while(ei.hasNext())
        	{
        		NodeEntity ne=(NodeEntity) ei.next();
        		if((ne.prevAnchorEntity==null||ne.nextAnchorEntity==null) && this.cableListContains(ne.getChainUniqueID()))
        		{
        			ne.setVirtualNodePos(connectionX, connectionY, connectionZ);
        		}
        	}
		}
	}
	
	protected boolean cableListContains(int chainUniqueID) {
		Iterator<IHLCable> cli = this.getCableList().iterator();
		while(cli.hasNext())
		{
			IHLCable c = cli.next();
			if(c.chainUID==chainUniqueID)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) 
	{
		return this.getFacing()!=(short)side;
	}
	

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList cableNBTList = new NBTTagList();
        for(IHLCable cable:this.cableList)
        {
        	cableNBTList.appendTag(cable.toNBT());
        }
        nbt.setTag("cableList", cableNBTList);
        nbt.setDouble("connectionX", this.connectionX);
        nbt.setDouble("connectionY", this.connectionY);
        nbt.setDouble("connectionZ", this.connectionZ);
        nbt.setInteger("gridID", this.gridID);
        nbt.setBoolean("checkCables", this.checkCables);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList cableNBTList=nbt.getTagList("cableList", 10);
        for(int i=0;i<cableNBTList.tagCount();i++)
        {
            this.cableList.add(IHLCable.fromNBT(cableNBTList.getCompoundTagAt(i)));
        }
        this.setConnectionX(nbt.getDouble("connectionX"));
        this.setConnectionY(nbt.getDouble("connectionY"));
        this.setConnectionZ(nbt.getDouble("connectionZ"));
        this.gridID=nbt.getInteger("gridID");
		this.checkCables=nbt.getBoolean("checkCables");
	}
	
	@Override
	public void removeAttachedChains()
	{
		IHLUtils.removeChains(this,this.worldObj);
	}

	public void setConnectionX(double connectionX) {
		this.connectionX = connectionX;
	}

	public void setConnectionY(double connectionY) {
		this.connectionY = connectionY;
	}

	public void setConnectionZ(double connectionZ) {
		this.connectionZ = connectionZ;
	}
	
	@Override
	public double[] getPortPos(EntityLivingBase player) 
	{
		return new double[] {this.connectionX,this.connectionY,this.connectionZ};
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
	public void setGrid(int newGridID)
	{
		if(newGridID!=-1)
		{
			this.gridID=newGridID;
			IHLMod.enet.getGrid(newGridID).add(this);
		}
		else
		{
			this.gridID=-1;
		}
	}
	
	@Override
	public int getGridID()
	{
		return this.gridID;
	}
	
	@Override
	public IHLGrid getGrid()
	{
		return IHLMod.enet.getGrid(gridID);
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }
	
	@Override
	public void remove(IHLCable cable) 
	{
		if(this.cableList.remove(cable))
		{
			IHLUtils.removeChain(cable, this);
		}
	}
	

	@Override
	public boolean isCableRemoved(int chainUniqueID) 
	{
		if(!checkCables)
		{
			return false;
		}
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
		this.checkCables=b;
	}
	
	@Override
	public boolean isTileEntityBaseInvalid(){
		return this.tileEntityInvalid;
	}
}
