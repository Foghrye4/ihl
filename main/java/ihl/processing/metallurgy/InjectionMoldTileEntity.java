package ihl.processing.metallurgy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ic2.core.IC2;
import ihl.metallurgy.constants.Details;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;


public class InjectionMoldTileEntity extends TileEntity implements IFluidHandler
{
    public short timer = 100;
    public String resultSuffix;
    public boolean isContainStearin = false;
    public short maxAmount=Short.MAX_VALUE;
    public ItemStack result;
    public ItemStack result2;
    private final FluidTank fluidTank;
    protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("injectionmold");
    
    public InjectionMoldTileEntity()
    {
    	this.fluidTank = new FluidTank(864);//144 mB per ingot 1300 EU with 100% efficiency
    }
	
    public static void addRecipe(FluidStack input, ItemStack input1, ItemStack output) 
	{
		recipeManager.addRecipe(new UniversalRecipeInput((new FluidStack[] {input}),(new ItemStack[] {input1})), new UniversalRecipeOutput(null,(new ItemStack[]{output}),20));
	}
	
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
        resultSuffix=nbttagcompound.getString("resultSuffix");
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        nbttagcompound.setTag("fluidTank", fluidTankTag);
        nbttagcompound.setString("resultSuffix",resultSuffix);
    }
    
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
    
    @Override
	public void updateEntity()
    {
    	if(this.getFluidStackfromTank()!=null && this.getFluidStackfromTank().amount>16)
    	{
    		if(this.timer--==0)
    		{
    			String formattedFluidName=this.getFormattedMaterialName(this.getFluidfromTank().getName());
    			if(formattedFluidName!="")
    			{
    				int fluidAmountPerItem = Details.getMeltingFluidAmount(this.resultSuffix);
    				ArrayList<ItemStack> resultList = OreDictionary.getOres(this.resultSuffix+formattedFluidName);
    				if(resultList.isEmpty() || fluidAmountPerItem<=0)
    				{
    					resultList=new ArrayList<ItemStack>();
    					List<FluidStack> fi = Arrays.asList(new FluidStack[] {this.fluidTank.getFluid()});
    					List<ItemStack> ii = Arrays.asList(new ItemStack[] {IHLUtils.getThisModItemStackWithDamage("injectionMold", this.resultSuffix.hashCode() & Integer.MAX_VALUE)});
    					UniversalRecipeOutput ro = recipeManager.getOutputFor(fi, ii);
    					if(ro!=null)
    					{
    						ItemStack stack = ro.getItemOutputs().get(0).itemStack.copy();
    						stack.stackSize=Math.round(ro.getItemOutputs().get(0).quantity);
        					resultList.add(stack);
        					UniversalRecipeInput ri = recipeManager.getRecipeInput(fi, ii);
        					fluidAmountPerItem = ri.getFluidInputs().get(0).getAmount();
    					}
    				}
    				int fluidAmountPerItem2 = Details.getMeltingFluidAmount("nugget");
    				ArrayList<ItemStack> resultList2 = OreDictionary.getOres("nugget"+formattedFluidName);
    				if(!resultList.isEmpty())
    				{
    					if(fluidAmountPerItem>0)
    					{
        					int stackSize1=Math.min(this.maxAmount,this.getFluidStackfromTank().amount/fluidAmountPerItem);
        					if(stackSize1>0)
        					{
        						result=resultList.get(0).copy();
        						result.stackSize=stackSize1;
        					}
    					}
    				}
    				if(!resultList2.isEmpty())
    				{
						int stackSize1=this.getFluidStackfromTank().amount/fluidAmountPerItem2;
    					if(result!=null)
    					{
    						stackSize1=(this.getFluidStackfromTank().amount % fluidAmountPerItem)/fluidAmountPerItem2;
    					}
    					if(stackSize1>0)
    					{
    						result2=resultList2.get(0).copy();
    						result2.stackSize=stackSize1;
    					}
    				}
    			}
    		}
    	}
    }
    
	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return false;
	}

	@Override
	public boolean canFill(ForgeDirection direction, Fluid fluid1) {
		return direction==ForgeDirection.UP && !isContainStearin;
	}
    
	public String getInventoryName() {
		return "injectionMold";
	}
	
	private String getFormattedMaterialName(String input)
	{
   		String input2 = input.replace("molten.", "").replace(".molten", "");
   		String firstletter = String.valueOf(input2.charAt(0)).toUpperCase();
   		input2 = input2.replaceFirst(".", firstletter);
   		return input2;
	}
	
    public FluidTank getFluidTank()
    {
        return this.fluidTank;
    }

    public FluidStack getFluidStackfromTank()
    {
        return this.getFluidTank().getFluid();
    }

    public Fluid getFluidfromTank()
    {
        return this.getFluidStackfromTank().getFluid();
    }

    @Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return this.canFill(from, resource.getFluid()) ? this.getFluidTank().fill(resource, doFill) : 0;
    }

    @Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return null;
    }

    @Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return this.getFluidTank().drain(maxDrain, doDrain);
    }

    @Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[] {this.getFluidTank().getInfo()};
    }

	public void dropContents() 
	{
    	if(result!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, result));
    	if(result2!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, result2));
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() 
	{
		return recipeManager.getRecipes();
	}
}