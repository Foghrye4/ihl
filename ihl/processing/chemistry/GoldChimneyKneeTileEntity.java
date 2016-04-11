package ihl.processing.chemistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ic2.api.network.INetworkDataProvider;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.network.NetworkManager;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLFluidTank;
import ihl.utils.IHLUtils;
import ihl.worldgen.ores.IHLFluid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class GoldChimneyKneeTileEntity extends TileEntity implements IWrenchable, INetworkDataProvider, IFluidHandler {
	private short facing=2;
	private short lastFacing=2;
    private final IHLFluidTank gasBuffer = new IHLFluidTank(10000);
    private final IHLFluidTank fluidTank = new IHLFluidTank(10000);
	private static UniversalRecipeManager recipeManager = new UniversalRecipeManager("chimneyknee");

    
	public GoldChimneyKneeTileEntity()
	{
		super();
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.facing!=side;
	}
	
	@Override
	public short getFacing() 
	{
		return facing;
	}
	
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = new ArrayList();
		fields.add("facing");
		return fields;
    }
	
    @Override
	public void updateEntity()
    {
        super.updateEntity();
        if(IC2.platform.isSimulating())
        {
        	if(this.facing!=this.lastFacing)
        	{
        		IC2.network.get().updateTileEntityField(this, "facing");
        		this.lastFacing=this.facing;
        	}
        	if(this.fluidTank.getFluid()!=null && this.gasBuffer.getFluid()!=null)
        	{
        		if(this.getOutput()!=null)
        		{
        			List<FluidStack> output = GoldChimneyKneeTileEntity.recipeManager.getOutputFor(getInput(), true, true).getFluidOutputs();
        			if(output.size()>0)
        			{
        				this.fillOutputApparatus(getConnectDirection(),output.get(0), true);
        			}
        		}
        	}
        	if(this.fluidTank.getFluid()!=null)
        	{
        		if(this.fillOutputApparatus(getConnectDirection(),this.fluidTank.drain(this.fluidTank.getCapacity(), false), false)>0)
        		{
        			this.fillOutputApparatus(getConnectDirection(),this.fluidTank.drain(this.fluidTank.getCapacity(), true), true);
        		}
        	}
        	if(this.gasBuffer.getFluid()!=null)
        	{
        		if(this.fillOutputApparatus(getConnectDirection(),this.gasBuffer.drain(this.gasBuffer.getCapacity(), false), false)>0)
        		{
        			this.fillOutputApparatus(getConnectDirection(),this.gasBuffer.drain(this.gasBuffer.getCapacity(), true), true);
        		}
        	}

        }
    }
	
	private int fillOutputApparatus(ForgeDirection direction,FluidStack fluidStack, boolean doFill) 
	{
		TileEntity te = worldObj.getTileEntity(xCoord+mX(),yCoord, zCoord+mZ());
		if(te instanceof IFluidHandler)
		{
			return ((IFluidHandler)te).fill(direction, fluidStack, doFill);
		}
		else
		{
			return 0;
		}
	}

	@Override
	public void setFacing(short facing1) 
	{
		facing=(short) Math.max(2,facing1);
		if(IC2.platform.isSimulating())
		{
			IC2.network.get().updateTileEntityField(this, "facing");
		}
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) 
	{
		return true;
	}

	@Override
	public float getWrenchDropRate() 
	{
		return 1F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) 
	{
		return IHLUtils.getThisModItemStack("goldChimneyKnee");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) 
	{
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("facing",facing);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        nbttagcompound.setTag("fluidTank", fluidTankTag);
        
        NBTTagCompound gasBufferTag = new NBTTagCompound();
        this.gasBuffer.writeToNBT(gasBufferTag);
        nbttagcompound.setTag("gasBuffer", gasBufferTag);
        
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) 
	{
        super.readFromNBT(nbttagcompound);
        facing=nbttagcompound.getShort("facing");
        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
        this.gasBuffer.readFromNBT(nbttagcompound.getCompoundTag("gasBuffer"));
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
	    
	    public TileEntity getConnectedTileEntity()
	    {
	    	return worldObj.getTileEntity(xCoord+mX(), yCoord, zCoord+mZ());
	    }

		public ForgeDirection getConnectDirection() 
		{
			switch(this.getFacing())
			{				
				case 2:
					return ForgeDirection.NORTH;
				case 3:
					return ForgeDirection.SOUTH;
				case 4:
					return ForgeDirection.WEST;
				case 5:
					return ForgeDirection.EAST;
				default:
					return ForgeDirection.NORTH;
			}
		}

		@Override
		public boolean canDrain(ForgeDirection arg0, Fluid arg1) 
		{
			return false;
		}

		@Override
		public boolean canFill(ForgeDirection direction, Fluid fluid) 
		{
			return fluid.isGaseous() && (direction==ForgeDirection.DOWN || direction==getConnectDirection());
		}
		
		@Override
		public int fill(ForgeDirection direction, FluidStack fluidStack, boolean doFill)
		{
			if(direction==getConnectDirection() && fluidStack!=null && fluidStack.getFluid()!=null)
			{
				if(fluidStack.getFluid().isGaseous() && fluidStack.getFluid().getDensity()<IHLFluid.maxGaseousStateVapoursDensity)
				{
					return gasBuffer.fill(fluidStack, doFill);
				}
				else
				{
					return 0;
				}
			}
			else if(direction==ForgeDirection.DOWN)
			{
				if(fluidStack.getFluid().isGaseous() && fluidStack.getFluid().getDensity()<IHLFluid.maxGaseousStateVapoursDensity)
				{
					return fluidTank.fill(fluidStack, doFill);
				}
				else
				{
					return 0;
				}
			}
			else
			{
				return 0;
			}
		}

		@Override
		public FluidStack drain(ForgeDirection arg0, FluidStack arg1, boolean arg2) 
		{
			return null;
		}

		@Override
		public FluidStack drain(ForgeDirection arg0, int arg1, boolean arg2) 
		{
			return null;
		}

		@Override
		public FluidTankInfo[] getTankInfo(ForgeDirection arg0) 
		{
			return new FluidTankInfo[]{this.fluidTank.getInfo(), this.gasBuffer.getInfo()};
		}
		
		public static void addRecipe(UniversalRecipeInput input, FluidStack fluidStackWithSize) 
		{
			recipeManager.addRecipe(input, new UniversalRecipeOutput(Arrays.asList(new FluidStack[] {fluidStackWithSize}),null,20));
		}
		
	    public UniversalRecipeOutput getOutput()
	    {
	    	return GoldChimneyKneeTileEntity.recipeManager.getOutputFor(this.getInput(), false, false);
	    }
	    
		public List[] getInput()
		{
			return new List[] {Arrays.asList(new FluidStack[] {this.gasBuffer.getFluid(),this.fluidTank.getFluid()}),null};
		}

		public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() 
		{
			return recipeManager.getRecipes();
		}
}
