package ihl.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class UniversalRecipeOutput{

    private final List<FluidStack> fluidOutputs=new ArrayList<FluidStack>();
    private final List<RecipeOutputItemStack> itemOutputs=new ArrayList<RecipeOutputItemStack>();
    private final int time;
    public final boolean specialConditions;
    
    public UniversalRecipeOutput(FluidStack[] fluidOutputs1, Object[] itemOutputs1, int time1)
    {
    	this(fluidOutputs1, itemOutputs1, time1,false);
    }
    
    public UniversalRecipeOutput(FluidStack[] fluidStacks, Object[] recipeOutputItemStacks, int time1, boolean specialConditions1)
    {
    	if(fluidStacks!=null)
    	{
        	for(FluidStack fStack:fluidStacks)
        	{
        		if(fStack==null)
        		{
        			throw new NullPointerException("Recipe cannot contain null elements!");
        		}
        		fluidOutputs.add(fStack);
        	}
    	}
    	if(recipeOutputItemStacks!=null)
    	{
        	for(Object io:recipeOutputItemStacks)
        	{
        		if(io==null)
        		{
        			throw new NullPointerException("Recipe output cannot be null!");
        		}
        		if(io instanceof ItemStack)
        		{
            		this.itemOutputs.add(new RecipeOutputItemStack((ItemStack) io));
        		}
        		else
        		{
            		this.itemOutputs.add((RecipeOutputItemStack) io);
        		}
        	}
    	}
    	specialConditions=specialConditions1;
    	time=time1;
    }

	public UniversalRecipeOutput(List<FluidStack> recipeOutputsFluids,
			List<RecipeOutputItemStack> recipeOutputsRecipeOut, int time1) {
		this.fluidOutputs.addAll(recipeOutputsFluids);
		this.itemOutputs.addAll(recipeOutputsRecipeOut);
		this.time=time1;
		this.specialConditions=false;
	}

	public boolean matches(List<FluidStack> fluidOutputs1, List<ItemStack> itemOutputs1) 
	{
		if(fluidOutputs.size()!=fluidOutputs1.size()||itemOutputs.size()!=itemOutputs.size())
		{
			return false;
		}
		Iterator<FluidStack> fi1 = fluidOutputs1.iterator();
		Iterator<ItemStack> ii1 = itemOutputs1.iterator();
		Iterator<FluidStack> fi = fluidOutputs.iterator();
		Iterator<RecipeOutputItemStack> ii = itemOutputs.iterator();
		while(fi.hasNext())
		{
			FluidStack fs = fi.next();
			FluidStack fs1 = fi1.next();
			if(fs.getFluid()!=fs1.getFluid())
			{
				return false;
			}
		}
		while(ii.hasNext())
		{
			RecipeOutputItemStack is = ii.next();
			ItemStack is1 = ii1.next();
			if(!is.matches(is1))
			{
				return false;
			}
		}
		return true;
	}

	public List<FluidStack> getFluidOutputs() {
		return fluidOutputs;
	}
	
	public List<RecipeOutputItemStack> getItemOutputs() {
		return itemOutputs;
	}

	public UniversalRecipeOutput copyWithMultiplier(int mulipier) {
		FluidStack[] fluidStacks = null;
		RecipeOutputItemStack[] itemStacks = null;
		if(fluidOutputs!=null && !fluidOutputs.isEmpty())
		{
			fluidStacks = new FluidStack[fluidOutputs.size()];
			for(int i=0;i<fluidOutputs.size();i++)
			{
				FluidStack fs = fluidOutputs.get(i);
				FluidStack newFs = fs.copy();
				newFs.amount*=mulipier;
				fluidStacks[i]=fs;
			}
		}
		if(itemOutputs!=null && !itemOutputs.isEmpty())
		{
			itemStacks = new RecipeOutputItemStack[itemOutputs.size()];
			for(int i=0;i<itemOutputs.size();i++)
			{
				RecipeOutputItemStack is = itemOutputs.get(i);
				RecipeOutputItemStack newIs = is.copy(mulipier);
				itemStacks[i]=newIs;
			}	
		}
		return new UniversalRecipeOutput(fluidStacks,itemStacks, getTime(),false);
	}

	public int getTime() {
		return time;
	}
	
	@Override
	public String toString()
	{
		StringBuffer out = new StringBuffer();
		for(FluidStack fluid: this.fluidOutputs)
		{
			out.append(fluid.getLocalizedName()+": "+fluid.amount+"/n");
		}
		for(RecipeOutputItemStack stack: this.itemOutputs)
		{
			out.append(stack.itemStack.getDisplayName()+": "+stack.quantity+"/n");
		}
		return out.toString(); 
	}
}
