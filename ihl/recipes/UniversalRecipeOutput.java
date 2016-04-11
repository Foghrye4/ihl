package ihl.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class UniversalRecipeOutput{

    private final List<FluidStack> fluidOutputs=new ArrayList();
    private final List<RecipeOutputItemStack> itemOutputs=new ArrayList();
    private final int time;
    public final boolean specialConditions;
    
    public UniversalRecipeOutput(List<FluidStack> fluidOutputs1, List itemOutputs1, int time1)
    {
    	this(fluidOutputs1, itemOutputs1, time1,false);
    }
    
    public UniversalRecipeOutput(List<FluidStack> fluidOutputs1, List itemOutputs1, int time1, boolean specialConditions1)
    {
    	if(fluidOutputs1!=null)
    	{
        	Iterator<FluidStack> ioi = fluidOutputs1.iterator();
        	while(ioi.hasNext())
        	{
        		FluidStack fStack = ioi.next();
        		if(fStack==null)
        		{
        			throw new NullPointerException("Recipe cannot contain null elements!");
        		}
        		fluidOutputs.add(fStack);
        	}
    	}
    	if(itemOutputs1!=null)
    	{
        	Iterator ioi = itemOutputs1.iterator();
        	while(ioi.hasNext())
        	{
        		Object io = ioi.next();
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
		ArrayList<FluidStack> fluidStacks = new ArrayList<FluidStack>();
		ArrayList<RecipeOutputItemStack> itemStacks = new ArrayList<RecipeOutputItemStack>();
		if(fluidOutputs!=null && !fluidOutputs.isEmpty())
		{
			Iterator<FluidStack> fi = fluidOutputs.iterator();
			while(fi.hasNext())
			{
				FluidStack fs = fi.next();
				FluidStack newFs = fs.copy();
				newFs.amount*=mulipier;
				fluidStacks.add(newFs);
			}
		}
		if(itemOutputs!=null && !itemOutputs.isEmpty())
		{
			Iterator<RecipeOutputItemStack> ii = itemOutputs.iterator();
			while(ii.hasNext())
			{
				RecipeOutputItemStack is = ii.next();
				RecipeOutputItemStack newIs = is.copy(mulipier);
				itemStacks.add(newIs);
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
