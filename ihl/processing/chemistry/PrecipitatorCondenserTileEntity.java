package ihl.processing.chemistry;

import java.util.Arrays;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLFluidTank;
import ihl.utils.IHLUtils;
import ihl.worldgen.ores.IHLFluid;

public class PrecipitatorCondenserTileEntity extends TileEntityInventory implements IFluidHandler
{
	private final static UniversalRecipeManager recipeManager = new UniversalRecipeManager("precipitatorcondenser");
    private static float kF = 24000F;
    private static float fluidC = 4F;
    private static float H = 2256F;
    private final IHLFluidTank gasBuffer = new IHLFluidTank(10000);
    private final IHLFluidTank fluidTank = new IHLFluidTank(100);
    private ForgeDirection gasInputDirection = ForgeDirection.WEST;
    private int amountOfGasCondensed=0;
    
    public PrecipitatorCondenserTileEntity()
    {
    	super();
    }
    
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
        this.gasBuffer.readFromNBT(nbttagcompound.getCompoundTag("gasBuffer"));
        this.amountOfGasCondensed=nbttagcompound.getInteger("amountOfGasCondensed");
    }
    
	public static void addRecipe(String fluidNameIn, String fluidNameOut, int conversionRateToGas) 
	{
		recipeManager.addRecipe(new UniversalRecipeInput((new FluidStack[] {IHLUtils.getFluidStackWithSize(fluidNameIn, conversionRateToGas)}), null), new UniversalRecipeOutput((new FluidStack[] {IHLUtils.getFluidStackWithSize(fluidNameOut, 1)}),null,2));
	}

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        nbttagcompound.setTag("fluidTank", fluidTankTag);
        
        NBTTagCompound gasBufferTag = new NBTTagCompound();
        this.gasBuffer.writeToNBT(gasBufferTag);
        nbttagcompound.setTag("gasBuffer", gasBufferTag);
        
        nbttagcompound.setInteger("amountOfGasCondensed",this.amountOfGasCondensed);
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing()!=(short)side && side!=0 && side!=1;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("goldPrecipitatorCondenser");
	}
    
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }

    @Override
	public void updateEntityServer()
    {
        super.updateEntityServer();
    	FluidStack fluidStack = this.fluidTank.getFluid();
    	FluidStack gasStack = this.gasBuffer.getFluid();
        if(fluidStack!=null && gasStack!=null && fluidStack.amount>0 && gasStack.amount>0)
        {
        	int t1 = this.fluidTank.getTemperature();
        	float densityOfCoolant = IHLFluid.getRealDensity(fluidStack.getFluid());
        	float densityOfGas = IHLFluid.getRealDensity(gasStack.getFluid());
        	int boilingPointOfGas = IHLFluid.getBoilingPoint(gasStack.getFluid());
        	amountOfGasCondensed += this.getAmountOfCondensedGas(fluidStack.amount, this.gasBuffer.getFluidAmount(), boilingPointOfGas, t1, densityOfGas, densityOfCoolant);
        }
    	if(amountOfGasCondensed>10000)
    	{
    		UniversalRecipeOutput rOutput = null;
    		UniversalRecipeInput rInput = null;
    		FluidStack fs = null;
    		for(int i=0;i<this.gasBuffer.getNumberOfFluids();i++)
    		{
    			FluidStack fs1 = this.gasBuffer.getFluid(i);
    			//System.out.println("#"+i+":"+fs1.getLocalizedName()+" amount "+fs1.amount);
    			UniversalRecipeOutput rOutput1 = PrecipitatorCondenserTileEntity.recipeManager.getOutputFor(Arrays.asList(new FluidStack[]{fs1}),null, false, false);
        		if(rOutput1==null)
        		{
        			this.gasBuffer.getFluidList().remove(i);
        			//System.out.println("output is null");
        		}
        		else
        		{
        			//System.out.println("output is not null");
        			rOutput=rOutput1;
        			rInput = PrecipitatorCondenserTileEntity.recipeManager.getRecipeInput(Arrays.asList(new FluidStack[]{fs1}),null);
        			fs=fs1;
        		}
    		}
    		if(rOutput!=null)
    		{
    			this.gasBuffer.drain(fs, true);
    			FluidStack outputFluid = rOutput.getFluidOutputs().get(0).copy();
    			outputFluid.amount=amountOfGasCondensed/rInput.getFluidInputs().get(0).getAmount();
    			this.fillCondensateOutputApparatus(ForgeDirection.UP, outputFluid, true);
    		}
    		amountOfGasCondensed=0;
    	}
    	if(this.gasBuffer.getFluidAmount()*3>this.gasBuffer.getCapacity()*2)
    	{
    		FluidStack passingGas = this.gasBuffer.drainLightest(this.gasBuffer.getCapacity()/2, true);
    		this.fillGaseousOutputApparatus(gasInputDirection, passingGas, true);
    	}
    	if(this.fluidTank.getFluid()!=null)
    	{
    		FluidStack coolant = this.fluidTank.drain(5, true);
    		this.fillHeatTransferAgentOutputApparatus(ForgeDirection.UP, coolant, true);
    	}
    }
    
    private int getAmountOfCondensedGas(int amountOfCoolant, int amountOfGas, int boilingPointOfGas, int temperatureOfCoolant, float densityOfGas, float densityOfCoolant)
    {
    	if(amountOfCoolant<=0 || amountOfGas<=0 || boilingPointOfGas-20<temperatureOfCoolant)
    	{
    		return 0;
    	}
		int L = amountOfGas;
		int t2 = boilingPointOfGas;
		int t1 = temperatureOfCoolant;
		int dt1=t2-t1;
    	float maxQ=H*L*densityOfGas;
    	int t1_1 = t1;
    	for(int i=0;i<10;i++)
    	{
    		float Q2=H*L*densityOfGas;
    		t1_1 = t1+(int)(Q2/fluidC/amountOfCoolant/densityOfCoolant);
			int dt2=t2-t1_1;
			float deltaT;
			if(dt1>2*dt2)
			{
				deltaT = (float) ((dt1-dt2)/Math.log((double)dt2/(double)dt1));
			}
			else
			{
				deltaT = (dt1+dt2)*0.5F;
			}
    		float Q1 = kF*deltaT;
    		if(Q1>=maxQ)
    		{
   				break;
    		}
    		else
    		{
    			int next_L =  (int)(Q1/H/densityOfGas);
    			if(Math.abs(next_L-L)<4)
    			{
    				L=next_L;
    				break;
    			}
				L=next_L;
    		}
    	}
		if(t1_1>t1)
		{
			this.fluidTank.setTemperature(t1_1);
		}
		else
		{
			this.fluidTank.setTemperature(t1+1);
		}
    	return L;
    }
    
    @Override
	public String getInventoryName()
    {
        return "precipitatorCondenser";
    }

    public void onGuiClosed(EntityPlayer entityPlayer) {}
    
    public int mX()
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
    
    public int mZ()
	{
		switch(this.getFacing())
		{
		case 2:
		return -1;
		case 3:
		return 1;
		default:
		return 0;
		}
	}
    
    public int mLZ()
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
    
    public int mLX()
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
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return false;
	}

	@Override
	public boolean canFill(ForgeDirection direction, Fluid fluid) {
		return direction==ForgeDirection.UP || (gasCanPass(direction) && fluid.isGaseous());
	}
	
	@Override
	public int fill(ForgeDirection direction, FluidStack fluidStack, boolean doFill)
	{
		if(fluidStack!=null && fluidStack.getFluid()!=null && fluidStack.amount>0)
		{
			if(gasCanPass(direction) && fluidStack!=null && fluidStack.getFluid()!=null && fluidStack.amount>0)
			{
				this.gasInputDirection=direction;
				if(fluidStack.getFluid().isGaseous() && fluidStack.getFluid().getDensity()<IHLFluid.maxGaseousStateVapoursDensity)
				{
					return gasBuffer.fill(fluidStack, doFill);
				}
				else
				{
					return this.fillCondensateOutputApparatus(ForgeDirection.UP, fluidStack, doFill);
				}
			}
			else if(direction==ForgeDirection.UP)
			{
				return fluidTank.fill(fluidStack, doFill);
			}
		}
		return 0;
	}
	
	private int fillCondensateOutputApparatus(ForgeDirection direction,FluidStack fluidStack, boolean doFill) 
	{
		TileEntity te = worldObj.getTileEntity(xCoord+mLX(),yCoord-1, zCoord+mLZ());
		if(te instanceof IFluidHandler)
		{
			return ((IFluidHandler)te).fill(direction, fluidStack, doFill);
		}
		else
		{
			return 0;
		}
	}
	
	private int fillHeatTransferAgentOutputApparatus(ForgeDirection direction,FluidStack fluidStack, boolean doFill) 
	{
		TileEntity te = worldObj.getTileEntity(xCoord,yCoord-1,zCoord);
		if(te instanceof IFluidHandler)
		{
			return ((IFluidHandler)te).fill(direction, fluidStack, doFill);
		}
		else
		{
			return 0;
		}
	}
	
	
	private int fillGaseousOutputApparatus(ForgeDirection direction,FluidStack fluidStack, boolean doFill) 
	{
		TileEntity te = worldObj.getTileEntity(xCoord-direction.offsetX,yCoord, zCoord-direction.offsetZ);
		if(te instanceof IFluidHandler)
		{
			int amountFilled = ((IFluidHandler)te).fill(direction, fluidStack, doFill);
			return amountFilled;
		}
		else
		{
			return 0;
		}
	}

	public boolean gasCanPass(ForgeDirection direction)
	{
		switch(this.getFacing())
		{				
			case 4:
				return direction == ForgeDirection.WEST ||  direction == ForgeDirection.EAST;
			case 5:
				return direction == ForgeDirection.WEST ||  direction == ForgeDirection.EAST;
			case 2:
				return direction == ForgeDirection.NORTH ||  direction == ForgeDirection.SOUTH;
			case 3:
				return direction == ForgeDirection.NORTH ||  direction == ForgeDirection.SOUTH;
			default:
				return direction == ForgeDirection.NORTH ||  direction == ForgeDirection.SOUTH;
		}
	}
	
	@Override
	public void setFacing(short facing1)
	{
		super.setFacing((short) Math.max(facing1, 2));
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, FluidStack arg1, boolean arg2) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, int arg1, boolean arg2) {
		return null;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection arg0) {
		return new FluidTankInfo[]{this.fluidTank.getInfo(), this.gasBuffer.getInfo()};
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() 
	{
		return recipeManager.getRecipes();
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }
}