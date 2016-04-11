package ihl.recipes;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ihl.interfaces.IWire;
import ihl.utils.IHLUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class UniversalRecipeInput{

    private final List<IRecipeInputFluid> fluidInputs=new ArrayList();
    private final List<IRecipeInput> itemInputs=new ArrayList();
    private boolean sharp=true;
    private int temperatureMin = 273;
    private int temperatureMax = Integer.MAX_VALUE;
    private float speedFactor=-1.0f;//=-Ea/R (k=k0*exp(-Ea/(RT)))
	private int multiplier=Integer.MAX_VALUE;
    
    
    public UniversalRecipeInput(List fluidInputs1, List itemInputs1, boolean sharp1)
    {
    	this(fluidInputs1, itemInputs1);
    	this.sharp=false;
    }
    
    public UniversalRecipeInput(List fluidInputs1, List itemInputs1)
    {
    	if(fluidInputs1!=null)
    	{
    		Iterator ifluidInputs1 = fluidInputs1.iterator();
    		while(ifluidInputs1.hasNext())
    		{
    			Object material = ifluidInputs1.next();
    			if(material==null)
    			{
    				throw new NullPointerException("Recipe input cannot be null!");
    			}
    			if(material instanceof FluidStack)
    			{
   					fluidInputs.add(new RecipeInputFluidStack((FluidStack)material));
    			}
    			else
    			{
    				fluidInputs.add((IRecipeInputFluid) material);
    			}
    		}
    	}
    	if(itemInputs1!=null)
    	{
    		Iterator iitemInputs1 = itemInputs1.iterator();
    		while(iitemInputs1.hasNext())
    		{
    			Object material = iitemInputs1.next();
    			if(material==null)
    			{
    				throw new NullPointerException("Recipe input cannot be null!");
    			}
    			if(material instanceof ItemStack)
    			{
    				ItemStack stack = (ItemStack)material;
    				String oreDictName = IHLUtils.getFirstOreDictNameExcludingTagAny(stack);
    				if(stack.getItem() instanceof IWire)
    				{
    					itemInputs.add(new RecipeInputWire(stack));
    				}
    				else if(!oreDictName.isEmpty() && oreDictName.length()>3)
    				{
    					itemInputs.add(new RecipeInputOreDict(oreDictName,stack.stackSize));
    				}
    				else
    				{
    					itemInputs.add(new RecipeInputItemStack(stack));
    				}
    			}
    			else
    			{
    				itemInputs.add((IRecipeInput) material);
    			}
    		}
    	}
    }

	public boolean matches(List<FluidStack> fluidInputs1, List<ItemStack> itemInputs1) 
	{
		return this.adjustAmounts(fluidInputs1, itemInputs1, false, false);
	}

	public List<IRecipeInputFluid> getFluidInputs() {
		return fluidInputs;
	}
	
	public List<IRecipeInput> getItemInputs() {
		return itemInputs;
	}

	public boolean matches(UniversalRecipeInput input) {
		List<IRecipeInput> rInputs = input.getItemInputs();
		Iterator<IRecipeInput> ii = rInputs.iterator();
		List<ItemStack> rInputsItems = new ArrayList();
		while(ii.hasNext())
		{
			IRecipeInput is = ii.next();
			rInputsItems.add(is.getInputs().get(0));
		}
		List<FluidStack> rInputsFluids = new ArrayList();
		List<IRecipeInputFluid> rInputsF = input.getFluidInputs();
		Iterator<IRecipeInputFluid> iiF = rInputsF.iterator();
		while(iiF.hasNext())
		{
			IRecipeInputFluid is = iiF.next();
			rInputsFluids.add(is.getInputs().get(0));
		}
		return this.matches(rInputsFluids, rInputsItems);
	}
	
	public boolean adjustAmounts(List<FluidStack> fluidInputs1, List<ItemStack> itemInputs1, boolean doCheckAmounts, boolean doAdjustAmounts) 
	{
		this.multiplier=Integer.MAX_VALUE;
		if(incorrectInputAmount(fluidInputs1, itemInputs1))
		{
			return false;
		}
		if(fluidInputs1!=null)
		{
		Iterator<IRecipeInputFluid> fi = fluidInputs.iterator();
		while(fi.hasNext())
		{
			IRecipeInputFluid fs = fi.next();
			FluidStack fs1 = getMatchedFluidStack(fs,fluidInputs1);
			if(fs1==null || !fs.matches(fs1))
			{
				multiplier=0;
				return false;
			}
			else if(doCheckAmounts && fs1.amount<fs.getAmount())
			{				
				multiplier=0;
				return false;
			}
			else if(doAdjustAmounts)
			{
				if(fs.getAmount()>0)
				{
					int multiplier1=fs1.amount/fs.getAmount();
					if(multiplier1<multiplier)
					{
						multiplier=multiplier1;
					}
				}
				fs1.amount-=fs.getAmount();
				if(fs1.amount<=0)fs1=null;
			}
		}
		}
		if(itemInputs1!=null)
		{
		Iterator<IRecipeInput> ii = itemInputs.iterator();
		while(ii.hasNext())
		{
			IRecipeInput is = ii.next();
			ItemStack is1 = getMatchedItemStack(is, itemInputs1);
			if(is1==null || !is.matches(is1))
			{
				multiplier=0;
				return false;
			}
			else if(doCheckAmounts && is1.stackSize<is.getAmount())
			{
				multiplier=0;
				return false;
			}
			else if(doAdjustAmounts)
			{
				if(is.getAmount()>0)
				{
					int multiplier1=is1.stackSize/is.getAmount();
					if(multiplier1<multiplier)
					{
						multiplier=multiplier1;
					}
				}
				if(IHLUtils.reduceItemStackAmountUsingIRecipeInput(is, is1))
				{
					is1=null;
				}
			}
		}
		}
		return true;
	}

	private ItemStack getMatchedItemStack(IRecipeInput is, List<ItemStack> itemInputs1) 
	{
		for(ItemStack is1:itemInputs1)
		{
			if(is1!=null)
			{
				if(is.matches(is1))
				{
					return is1;
				}
			} 
		}
		return null;
	}

	private FluidStack getMatchedFluidStack(IRecipeInputFluid fs, List<FluidStack> fluidInputs1) 
	{
		for(FluidStack fs1:fluidInputs1)
		{
			if(fs.matches(fs1))
			{
				return fs1;
			}
		}
		return null;
	}

	public boolean adjustAmounts(UniversalRecipeInput input, boolean doAdjustAmounts) {
		List<IRecipeInput> rInputs = input.getItemInputs();
		Iterator<IRecipeInput> ii = rInputs.iterator();
		List<ItemStack> rInputsItems = new ArrayList();
		while(ii.hasNext())
		{
			IRecipeInput is = ii.next();
			rInputsItems.add(is.getInputs().get(0));
		}
		List<FluidStack> rInputsFluids = new ArrayList();
		List<IRecipeInputFluid> rInputsF = input.getFluidInputs();
		Iterator<IRecipeInputFluid> iiF = rInputsF.iterator();
		while(iiF.hasNext())
		{
			IRecipeInputFluid is = iiF.next();
			rInputsFluids.add(is.getInputs().get(0));
		}
		return this.adjustAmounts(rInputsFluids, rInputsItems, true, doAdjustAmounts);
	}

	public int getMultiplierAndAdjustAmounts(List<FluidStack> fluidInputs1, List<ItemStack> itemInputs1) 
	{
		if(this.adjustAmounts(fluidInputs1, itemInputs1, true, true))
		{
			if(multiplier<Integer.MAX_VALUE)
			{
				return multiplier;
			}
			else return 1;
		}
		else
		{
			return 0;
		}
	}

	public boolean containItemStack(ItemStack ingredient) 
	{
		if(itemInputs==null || itemInputs.isEmpty())
		{
			return false;
		}
		Iterator<IRecipeInput> ii = itemInputs.iterator();
		while(ii.hasNext())
		{
			IRecipeInput is = ii.next();
			if(is.matches(ingredient))
			{
				return true;
			}
		}
		return false;
	}

	public boolean containFluidStack(FluidStack fluidStack) 
	{
		if(fluidInputs==null || fluidInputs.isEmpty())
		{
			return false;
		}
		Iterator<IRecipeInputFluid> ii = fluidInputs.iterator();
		while(ii.hasNext())
		{
			IRecipeInputFluid is = ii.next();
			if(is!=null && is.matches(fluidStack))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean incorrectInputAmount(List<FluidStack> fluidInputs1, List<ItemStack> itemInputs1)
	{
		return (fluidInputs.size()>0 && fluidInputs1==null)||
				(itemInputs.size()>0 && itemInputs1==null)||
				(fluidInputs1!=null && fluidInputs.size()>fluidInputs1.size())||
				(itemInputs1!=null && itemInputs.size()>itemInputs1.size());
	}
}
