package ihl.crop_harvestors;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ic2.core.network.NetworkManager;
import ihl.IHLMod;
import ihl.utils.IHLFluidTank;

public class SackTileEntity extends TileEntityInventory implements IFluidHandler
{
    private final int maxLeavesHeight=12;
    private final int maxLeavesWidth=5;
    public float leavesCounter=0F;
    private int updateCounter=1100;
    private Random rand=new Random();
    private int blobTimer=20;
    public byte currentTree = RUBBERTREE;
    public final int blobCapacity = 1;
    private final static byte RUBBERTREE=0;
    private final static byte SPRUCE=1;
	public final IHLFluidTank fluidTank = new IHLFluidTank(8000);
    public int visibleFluidId = -1;
    public int visibleFluidAmount = 1;
   
	public SackTileEntity() {
		super();
	}

	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("visibleFluidId");
		fields.add("visibleFluidAmount");
		fields.add("leavesCounter");
		fields.add("currentTree");
		return fields;
    }
	
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        nbttagcompound.setTag("fluidTank", fluidTankTag);
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(IHLMod.sackBlock,1);
	}
	
    public boolean enableUpdateEntity()
    {
        return true;
    }

    
    @Override
	public void updateEntityClient()
    {
    	super.updateEntityClient();
    	if(IHLMod.ic2Leaves!=null && IHLMod.ic2Wood!=null)
    	{
			if(this.blobTimer<=0)
			{
				if(IC2.platform.isRendering())
				{
	   				switch(this.currentTree)
	   				{
	   					case RUBBERTREE:
	   						IHLMod.proxy.spawnParticle(1,worldObj, xCoord+0.5D+mX()*0.5D,yCoord+1.05D,zCoord+0.5D+mZ()*0.5D,-(double)mX()*0.1D,-0.03D,-(double)mZ()*0.1D,0.1F);
	   						break;
	   					case SPRUCE:
	   						IHLMod.proxy.spawnParticle(2,worldObj, xCoord+0.5D+mX()*0.5D,yCoord+1.05D,zCoord+0.5D+mZ()*0.5D,-(double)mX()*0.1D,-0.03D,-(double)mZ()*0.1D,0.1F);
	   						break;
	   				}
    	    	}
	   			if(this.fluidTank.getFluidAmount()>=this.fluidTank.getCapacity())
	    		{
	   				if(IC2.platform.isRendering())
	   				{
		   				switch(this.currentTree)
		   				{
		   					case RUBBERTREE:
		   						IHLMod.proxy.spawnParticle(1,worldObj, xCoord+0.5D-mX()*0.3D+(rand.nextDouble()-0.5D)*mZ()*0.6D,yCoord+0.8D,zCoord+0.5D-mZ()*0.3D+(rand.nextDouble()-0.5D)*mX()*0.6D,0D,-0.05D,0D,0.1F);
		   						break;
		   					case SPRUCE:
		   						IHLMod.proxy.spawnParticle(2,worldObj, xCoord+0.5D-mX()*0.3D+(rand.nextDouble()-0.5D)*mZ()*0.6D,yCoord+0.8D,zCoord+0.5D-mZ()*0.3D+(rand.nextDouble()-0.5D)*mX()*0.6D,0D,-0.05D,0D,0.1F);
		   						break;
		   				}
	   				}
	    		}
				if(this.leavesCounter>1F)
				{
					this.blobTimer=Math.round(4000F/this.leavesCounter);
				}
				else
				{
					this.blobTimer=200;
				}
			}
			else
			{
				if(this.leavesCounter>1F)
				{
					this.blobTimer--;
				}
			}
    		if(this.updateCounter<1200)
    		{
    			this.updateCounter++;
    		}
    	}
    	
    }
    
    
    @Override
	public void updateEntityServer()
    {
    	super.updateEntityServer();
    	if(IHLMod.ic2Leaves!=null && IHLMod.ic2Wood!=null)
    	{
			if(this.blobTimer<=0)
			{
	   			{
					if(IC2.platform.isSimulating())
					{
						switch(this.currentTree)
						{
	   					case RUBBERTREE:
		   						this.fluidTank.fill(new FluidStack(FluidRegistry.getFluid("fluidrubbertreesap"), blobCapacity), true);
		   						break;
	   					case SPRUCE:
	   							this.fluidTank.fill(new FluidStack(FluidRegistry.getFluid("spruceresin"), blobCapacity), true);
	   							break;
	   					}
	   				}
	   			}
				if(this.leavesCounter>1F)
				{
					this.blobTimer=Math.round(4000F/this.leavesCounter);
				}
				else
				{
					this.blobTimer=200;
				}
			}
			else
			{
				if(this.leavesCounter>1F)
				{
					this.blobTimer--;
				}
			}
    		if(this.updateCounter<1200)
    		{
    			this.updateCounter++;
    		}
    		else
    		{
    			if(IC2.platform.isSimulating())
    			{
    				this.updateCounter=rand.nextInt(600);
    				if(checkCorrectPlacing())
    				{
    					countRubberTreeLeaves();
    				}
    				else
    				{
    					leavesCounter=0F;
    				}
    				IC2.network.get().updateTileEntityField(this, "currentTree");	   							
    				IC2.network.get().updateTileEntityField(this, "leavesCounter");
    				if(this.fluidTank.getFluid()!=null)
    				{

    						TileEntity te = worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
    						if(te!=null && te instanceof IFluidHandler)
    						{
    							IFluidHandler fte = (IFluidHandler) te;
    							FluidStack fStack = this.fluidTank.drain(Integer.MAX_VALUE, false);
    							if(fte.canFill(ForgeDirection.UP, fStack.getFluid()))
    							{
    								if(fte.fill(ForgeDirection.UP, fStack, false)>0)
    								{
    									int amount = fte.fill(ForgeDirection.UP, fStack, true);
    									this.fluidTank.drain(amount, true);
    								}
    							}
    						}
    				}
    			}
    		}
			if(this.fluidTank.getFluid()!=null && (visibleFluidId!=this.fluidTank.getFluid().getFluid().getID() || Math.abs(visibleFluidAmount-this.fluidTank.getFluidAmount())>20))
			{
				visibleFluidId = this.fluidTank.getFluid().getFluid().getID();
				visibleFluidAmount = this.fluidTank.getFluidAmount();
	       		IC2.network.get().updateTileEntityField(this, "visibleFluidId");
	       		IC2.network.get().updateTileEntityField(this, "visibleFluidAmount");
	       	}
			else if(this.fluidTank.getFluid()==null && visibleFluidId!=-1)
	       	{
				visibleFluidId=-1;
	       		IC2.network.get().updateTileEntityField(this, "visibleFluidId");
	       	}
    	}
    }
    
    private boolean checkCorrectPlacing() 
    {
    	int xz[]={0,1,0,-1,0};
    	Block block, block2;
    	int meta,meta2;
    	for(int i =0;i<=3;i++)
    	{
    		block=worldObj.getBlock(xCoord+xz[i], yCoord, zCoord+xz[i+1]);
    		meta=worldObj.getBlockMetadata(xCoord+xz[i], yCoord, zCoord+xz[i+1]);
    		block2=worldObj.getBlock(xCoord+xz[i], yCoord+1, zCoord+xz[i+1]);
    		meta2=worldObj.getBlockMetadata(xCoord+xz[i], yCoord+1, zCoord+xz[i+1]);
    		if(
    			meta>0 && 
    			meta2>0 && 
    			(block==IHLMod.ic2Wood||block==IHLMod.rubberTreeBlock) &&
    			(block2==IHLMod.ic2Wood||block2==IHLMod.rubberTreeBlock)
    		  )
    		{
    			short newFacing = this.getFacingFromXZ(xz[i], xz[i+1]);
    			this.setFacing(newFacing);
    			if(block2==IHLMod.ic2Wood)
    			{
    				worldObj.setBlock(xCoord+xz[i], yCoord+1, zCoord+xz[i+1],IHLMod.rubberTreeBlock,newFacing,3);
    			}
    			else
    			{
    				if(newFacing!=meta2)
    				{
    					return false;
    				}
    			}
    			this.currentTree=RUBBERTREE;
    			return checkGround();
    		}
    		else if(
    				((block==Blocks.log && meta==1) || 
    				(block==IHLMod.spruceTreeBlock && meta>0)) &&
    				((block2==Blocks.log && meta2==1) || 
    				(block2==IHLMod.spruceTreeBlock && meta2>0))
    				)
    		{
    			short newFacing = this.getFacingFromXZ(xz[i], xz[i+1]);
    			this.setFacing(newFacing);
    			if(block2==Blocks.log)
    			{
    				worldObj.setBlock(xCoord+xz[i], yCoord+1, zCoord+xz[i+1],IHLMod.spruceTreeBlock,newFacing,3);
    			}
    			else
    			{
    				if(newFacing!=meta2)
    				{
    					return false;
    				}
    			}
    			this.currentTree=SPRUCE;
    			return checkGround();
    		}
    	}
		return false;
	}

	private boolean checkGround()
    {
	   	Block block;
    	int meta;
    	for(int h=0; h<=this.maxLeavesHeight; h++)
    	{
    		block=worldObj.getBlock(xCoord+mX(), yCoord-h, zCoord+mZ());
    		meta=worldObj.getBlockMetadata(xCoord+mX(), yCoord-h, zCoord+mZ());
    		if(!isLogBlock(block, meta))
    		{
    			if(block==Blocks.dirt)
    			{
    				return true;
    			}
    			else
    			{
    				return false;
    			}
    		}
    		else if(meta<=0)
    		{
				return false;
    		}
    	}
		return false;
    }
	
	private boolean isLogBlock(Block block, int meta)
	{
		switch(this.currentTree)
		{
			case RUBBERTREE:
				return block==IHLMod.ic2Wood || block==IHLMod.rubberTreeBlock;
			case SPRUCE:
				return (block==Blocks.log && meta==1) || (block==IHLMod.spruceTreeBlock && meta>0);
		}
		return false;
	}
	
	private boolean isLeavesBlock(Block block)
	{
		switch(this.currentTree)
		{
			case RUBBERTREE:
				return block==IHLMod.ic2Leaves;
			case SPRUCE:
				return block==Blocks.leaves;
		}
		return false;
	}
	
	private boolean isIncisedLog(Block block)
	{
		switch(this.currentTree)
		{
			case RUBBERTREE:
				return block==IHLMod.rubberTreeBlock;
			case SPRUCE:
				return block==IHLMod.spruceTreeBlock;
		}
		return false;
	}
    
	private void countRubberTreeLeaves()
    {
    	this.leavesCounter=0;
    	int[][][] leavesMatrix=new int[this.maxLeavesWidth][this.maxLeavesWidth][this.maxLeavesHeight];
    	for(int iy=1;iy<this.maxLeavesHeight;iy++)
    	{
    		Block block=worldObj.getBlock(xCoord+mX(), yCoord+iy, zCoord+mZ());
    		int meta=worldObj.getBlockMetadata(xCoord+mX(), yCoord+iy, zCoord+mZ());
    		if(isLogBlock(block, meta)||isLeavesBlock(block))
    		{
    			if(isLeavesBlock(block))
    			{
    				this.leavesCounter+=worldObj.getLightBrightness(xCoord+mX(), yCoord+iy, zCoord+mZ());
    			}
    			else if(isIncisedLog(block) && iy>=2)
    			{
    				break;
    			}
				for(int sign=1;sign>=-1;sign-=2)
				{
					for(int ix=1;ix<=2;ix++)
					{

    					block=worldObj.getBlock(xCoord+mX()+ix*sign, yCoord+iy, zCoord+mZ());
    					if(isLeavesBlock(block))
    					{
    						this.leavesCounter+=worldObj.getLightBrightness(xCoord+mX()+ix*sign, yCoord+iy, zCoord+mZ());
    						leavesMatrix[2+ix*sign][2][iy-1]=1;
    					}
    					else
    					{
    						break;
    					}
    				}
					for(int iz=1;iz<=2;iz++)
    				{
    					block=worldObj.getBlock(xCoord+mX(), yCoord+iy, zCoord+mZ()+iz*sign);
        				if(isLeavesBlock(block))
        				{
        					this.leavesCounter+=worldObj.getLightBrightness(xCoord+mX(), yCoord+iy, zCoord+mZ()+iz*sign);
        					leavesMatrix[2][2+iz*sign][iy-1]=1;
        				}
        				else
        				{
        					break;
        				}
    				}
				}
				for(int signx=1;signx>=-1;signx-=2)
				{
					for(int signz=1;signz>=-1;signz-=2)
					{
						if(leavesMatrix[2][2+signz][iy-1]==1||leavesMatrix[2+signx][2][iy-1]==1)
						{
							block=worldObj.getBlock(xCoord+mX()+signx, yCoord+iy, zCoord+mZ()+signz);
	        				if(isLeavesBlock(block))
	        				{
	        					this.leavesCounter+=worldObj.getLightBrightness(xCoord+mX()+signx, yCoord+iy, zCoord+mZ()+signz);
	        					leavesMatrix[2+signx][2+signz][iy-1]=1;
	        				}
						}
						if(leavesMatrix[2+signx*2][2][iy-1]==1||leavesMatrix[2+signx][2+signz][iy-1]==1)
						{
							block=worldObj.getBlock(xCoord+mX()+signx*2, yCoord+iy, zCoord+mZ()+signz);
	        				if(block==IHLMod.ic2Leaves)
	        				{
	        					this.leavesCounter+=worldObj.getLightBrightness(xCoord+mX()+signx*2, yCoord+iy, zCoord+mZ()+signz);
								leavesMatrix[2+signx*2][2+signz][iy-1]=1;
	        				}
						}
						if(leavesMatrix[2][2+signz*2][iy-1]==1||leavesMatrix[2+signx][2+signz][iy-1]==1)
						{
							block=worldObj.getBlock(xCoord+mX()+signx, yCoord+iy, zCoord+mZ()+signz*2);
	        				if(isLeavesBlock(block))
	        				{
	        					this.leavesCounter+=worldObj.getLightBrightness(xCoord+mX()+signx, yCoord+iy, zCoord+mZ()+signz*2);
	        					leavesMatrix[2+signx][2+signz*2][iy-1]=1;
	        				}
						}
						if(leavesMatrix[2+signx*2][2+signz][iy-1]==1||leavesMatrix[2+signx][2+signz*2][iy-1]==1)
						{
							block=worldObj.getBlock(xCoord+mX()+signx*2, yCoord+iy, zCoord+mZ()+signz*2);
	        				if(isLeavesBlock(block))
	        				{
	        					this.leavesCounter+=worldObj.getLightBrightness(xCoord+mX()+signx*2, yCoord+iy, zCoord+mZ()+signz*2);
	        				}
						}
					}
				}
    		}
    		else
    		{
    			break;
    		}
    	}
    }
	
    //1.7.10 API
	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return true;
	}

	@Override
	public boolean canFill(ForgeDirection arg0, Fluid arg1) {
		return false;
	}

	@Override
	public String getInventoryName() {
		return "sack";
	}
	
    private int mX()
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
	
	private int mZ()
	{
		switch(this.getFacing())
		{
		case 3:
		return 1;
		case 2:
		return -1;
		case 4:
		return 0;
		case 5:
		return 0;
		default:
		return -1;
		}
	}
	
	private short getFacingFromXZ(int x, int z)
	{
		switch(x)
		{
			case -1:
				return (short)4;
			case 1:
				return (short)5;
			default:
				switch(z)
				{
				case 1:
					return (short)3;
				case -1:
					return (short)2;
				default:
					return (short)2;
				}
		}
	}
	
	public float getRenderLiquidLevel()
	{
		return (float)this.visibleFluidAmount/(float)this.fluidTank.getCapacity();
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }

	@Override
	public FluidStack drain(ForgeDirection arg0, FluidStack arg1, boolean arg2) 
	{
		if(this.canDrain(arg0, arg1.getFluid()))
		{
			FluidStack fStack = fluidTank.drain(arg1, arg2);
			return fStack;
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, int arg1, boolean arg2) 
	{
		if(this.canDrain(arg0, null))
		{
			FluidStack fStack = fluidTank.drain(arg1, arg2);
			return fStack;
		}
		return null;
	}

	@Override
	public int fill(ForgeDirection arg0, FluidStack arg1, boolean arg2) {
		return 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection arg0) {
		return new FluidTankInfo[]{this.fluidTank.getInfo()};
	}
}