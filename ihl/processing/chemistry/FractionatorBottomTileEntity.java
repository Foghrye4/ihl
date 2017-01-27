package ihl.processing.chemistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import ic2.api.energy.tile.IHeatSource;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ihl.recipes.IRecipeInputFluid;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLFluidTank;
import ihl.utils.IHLUtils;
import ihl.worldgen.ores.IHLFluid;

public class FractionatorBottomTileEntity extends TileEntityInventory
{
	private final static UniversalRecipeManager recipeManager = new UniversalRecipeManager("fractionator");
    private static float kF = 24000F;
    private static float fluidC = 4F;
    private static float H = 2256F;
    private final IHLFluidTank waterTank = new IHLFluidTank(100);
    private final IHLFluidTank fluidTank = new IHLFluidTank(8000);
    private int amountOfGasCondensed=0;
    private int amountOfFluidEvaporated=0;
    private int systemHeat=0;
    private static final int maxSystemHeat=10000;
    private final List<FractionatorSectionTileEntity> listOfColumnSections = new ArrayList<FractionatorSectionTileEntity>();
    private FractionatorCoverTileEntity columnCover;
    private RefluxCondenserTileEntity refluxCondenser;
    private IHeatSource heatSource;
    private int fluxRecirculationProportion=10;
    
    public FractionatorBottomTileEntity()
    {
    	super();
    }
    
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
        this.waterTank.readFromNBT(nbttagcompound.getCompoundTag("waterTank"));
        this.amountOfGasCondensed=nbttagcompound.getInteger("amountOfGasCondensed");
        this.systemHeat=nbttagcompound.getInteger("systemHeat");
    }
    
    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        nbttagcompound.setTag("fluidTank", fluidTankTag);
        
        NBTTagCompound waterTankTag = new NBTTagCompound();
        this.waterTank.writeToNBT(waterTankTag);
        nbttagcompound.setTag("waterTank", waterTankTag);
        
        nbttagcompound.setInteger("amountOfGasCondensed",this.amountOfGasCondensed);
        nbttagcompound.setInteger("systemHeat",this.systemHeat);
        
    }

    public static void addRecipe(FluidStack fluidIn, FluidStack fluidOut1, FluidStack fluidOut2) 
	{
		recipeManager.addRecipe(new UniversalRecipeInput((new FluidStack[] {fluidIn}), null), new UniversalRecipeOutput((new FluidStack[] {fluidOut1,fluidOut2}),null,2));
	}

    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing()!=(short)side && side!=0 && side!=1;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("fractionatorBottom");
	}
    
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }

    @Override
    public void updateEntityServer()
    {
        super.updateEntityServer();
        if(!this.checkIntegrity())
        {
        	return;
        }
        this.fluxRecirculationProportion=100-10000/(100+this.listOfColumnSections.size()*3);
		ForgeDirection orientation = ForgeDirection.getOrientation(this.getFacing()).getRotation(ForgeDirection.UP);
        if(systemHeat < maxSystemHeat && this.checkHeatSource(orientation))
        {
        	systemHeat+=this.heatSource.requestHeat(orientation.getOpposite(), Integer.MAX_VALUE);
        }
        FluidStack fsCurrentInput = this.fluidTank.getFluid();
        if(fsCurrentInput!=null && fsCurrentInput.amount>100 && systemHeat>0)
        {
        	UniversalRecipeOutput rOutput = FractionatorBottomTileEntity.recipeManager.getOutputFor(Arrays.asList(new FluidStack [] {this.fluidTank.getFluid()}),null, false, false);
        	UniversalRecipeInput rInput = FractionatorBottomTileEntity.recipeManager.getRecipeInput(Arrays.asList(new FluidStack [] {this.fluidTank.getFluid()}),null);
        	if(rOutput!=null)
        	{
        		IRecipeInputFluid input = rInput.getFluidInputs().get(0);
        		int rInputAmount = input.getAmount();
        		FluidStack result1=rOutput.getFluidOutputs().get(0).copy();
        		FluidStack result2=rOutput.getFluidOutputs().get(1).copy();
        		//max heat per tick of electric heater is 100
        		int amountOfFluidToEvaporate = Math.min(fsCurrentInput.amount*result2.amount/rInputAmount,systemHeat/100);
        		systemHeat-=amountOfFluidToEvaporate*100;
        		amountOfFluidEvaporated+=amountOfFluidToEvaporate;
        		int amountOfVapours=amountOfFluidEvaporated*50;
        		FluidStack coolant = this.waterTank.getFluid();
        		if(coolant!=null && coolant.amount>0)
        		{
        			int t1 = this.waterTank.getTemperature();
        			float densityOfCoolant = IHLFluid.getRealDensity(coolant.getFluid());
        			float densityOfGas = 17.8f;
        			int boilingPointOfGas = IHLFluid.getBoilingPoint(result2.getFluid());
        			int amountOfGasToCondense = this.getAmountOfCondensedGas(coolant.amount, amountOfVapours, boilingPointOfGas, t1, densityOfGas, densityOfCoolant);
        			amountOfGasCondensed += amountOfGasToCondense;
        			amountOfFluidEvaporated -= amountOfGasToCondense/50;
        		}
            	if(amountOfGasCondensed>10000)
            	{
            		int amountToProcess=amountOfGasCondensed*this.fluxRecirculationProportion/5000;//only 10% of condensate will be extracted.
            		int amount = rInputAmount * amountToProcess / result2.amount;
            		result1.amount = result1.amount* amountToProcess / result2.amount;
            		result2.amount = amountToProcess;
            		this.fluidTank.drain(input,amount, true);
            		this.fillVatResidueOutputApparatus(orientation.getOpposite(), result1, true);
            		this.fillCondensateOutputApparatus(ForgeDirection.UP, result2, true);
            		amountOfGasCondensed=0;
            	}
        	}
    	}
    	if(this.waterTank.getFluid()!=null)
    	{
    		FluidStack coolant = this.waterTank.drain(5, true);
    		this.fillHeatTransferAgentOutputApparatus(ForgeDirection.UP, coolant, true);
    	}
    }


	private boolean checkHeatSource(ForgeDirection orientation) 
	{
		if(this.heatSource!=null)
		{
			return true;
		}
		else
		{
			TileEntity te = worldObj.getTileEntity(xCoord+orientation.offsetX, yCoord, zCoord+orientation.offsetZ); 
			if(te instanceof IHeatSource)
			{
				if(((IHeatSource)te).maxrequestHeatTick(orientation.getOpposite())>0)
				{
					this.heatSource=(IHeatSource)te;
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkIntegrity() 
	{
		boolean allright=true;
		if(!this.listOfColumnSections.isEmpty() && 
				this.columnCover!=null && 
				!this.columnCover.isInvalid() &&
				this.refluxCondenser!=null &&
				!this.refluxCondenser.isInvalid())
		{
			Iterator<FractionatorSectionTileEntity> sectionsIterator = this.listOfColumnSections.iterator();
			while(sectionsIterator.hasNext())
			{
				FractionatorSectionTileEntity section = sectionsIterator.next();
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
			this.listOfColumnSections.clear();
			this.columnCover=null;
			this.refluxCondenser=null;
			boolean checking = true;
			int height=0;
			while(checking)
			{
				height++;
				TileEntity te = worldObj.getTileEntity(xCoord, yCoord+height, zCoord);
				if(te instanceof FractionatorSectionTileEntity)
				{
					FractionatorSectionTileEntity section = (FractionatorSectionTileEntity)te;
					section.columnBottom=this;
					this.listOfColumnSections.add(section);
				}
				else if(te instanceof FractionatorCoverTileEntity)
				{
					if(this.listOfColumnSections.isEmpty())
					{
						return false;
					}
					FractionatorCoverTileEntity fcte = (FractionatorCoverTileEntity)te;
					this.columnCover=fcte;
					ForgeDirection orientation = ForgeDirection.getOrientation(fcte.getFacing()).getRotation(ForgeDirection.DOWN);
					te = worldObj.getTileEntity(xCoord+orientation.offsetX, yCoord+height, zCoord+orientation.offsetZ);
					if(te instanceof RefluxCondenserTileEntity)
					{
						if(((RefluxCondenserTileEntity)te).getFacing()==fcte.getFacing())
						{
							this.refluxCondenser=(RefluxCondenserTileEntity)te;
							this.refluxCondenser.columnBottom=this;
							return true;
						}
					}
				}
				else
				{
					checking=false;
				}
			}
		}
		return false;
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
			this.waterTank.setTemperature(t1_1);
		}
		else
		{
			this.waterTank.setTemperature(t1+1);
		}
    	return L;
    }
    
    @Override
	public String getInventoryName()
    {
        return "fractionator";
    }

    public void onGuiClosed(EntityPlayer entityPlayer) {}
    
    public int fill(ForgeDirection direction, FluidStack fluidStack, boolean doFill)
	{
			if(direction.equals(ForgeDirection.UP))
			{
				return waterTank.fill(fluidStack, doFill);
			}
			else
			{
				return fluidTank.fill(fluidStack, doFill);
			}
	}
	
	private int fillCondensateOutputApparatus(ForgeDirection direction,FluidStack fluidStack, boolean doFill) 
	{
		TileEntity te = worldObj.getTileEntity(this.refluxCondenser.xCoord,this.refluxCondenser.yCoord-1,this.refluxCondenser.zCoord);
		if(te instanceof IFluidHandler)
		{
			return ((IFluidHandler)te).fill(direction, fluidStack, doFill);
		}
		else
		{
			return 0;
		}
	}
	
    
    private int fillVatResidueOutputApparatus(ForgeDirection orientation, FluidStack fluidStack, boolean doFill) {
		TileEntity te = worldObj.getTileEntity(xCoord+orientation.offsetX,yCoord, zCoord+orientation.offsetZ);
		if(te instanceof IFluidHandler)
		{
			return ((IFluidHandler)te).fill(orientation, fluidStack, doFill);
		}
		else
		{
			return 0;
		}
	}
	
	private int fillHeatTransferAgentOutputApparatus(ForgeDirection direction,FluidStack fluidStack, boolean doFill) 
	{
		ForgeDirection orientation = ForgeDirection.getOrientation(this.refluxCondenser.getFacing()).getOpposite();
		TileEntity te = worldObj.getTileEntity(this.refluxCondenser.xCoord+orientation.offsetX,this.refluxCondenser.yCoord,this.refluxCondenser.zCoord+orientation.offsetZ);
		if(te instanceof IFluidHandler)
		{
			return ((IFluidHandler)te).fill(orientation, fluidStack, doFill);
		}
		else
		{
			return 0;
		}
	}

	@Override
	public void setFacing(short facing1)
	{
		super.setFacing((short) Math.max(facing1, 2));
	}
	
	public FluidTankInfo[] getTankInfo(ForgeDirection arg0) {
		return new FluidTankInfo[]{this.fluidTank.getInfo(), this.waterTank.getInfo()};
	}
	

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }
}