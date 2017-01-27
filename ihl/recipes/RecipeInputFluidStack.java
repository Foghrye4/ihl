package ihl.recipes;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class RecipeInputFluidStack implements IRecipeInputFluid 
{
	private final Fluid fluid;
	private final int amount;
	public RecipeInputFluidStack(FluidStack fstack)
	{
		this.fluid=fstack.getFluid();
		this.amount=fstack.amount;
	}

	@Override
	public boolean matches(FluidStack subject) 
	{
		if(subject==null || subject.getFluid()==null)
		{
			return false;
		}
		return fluid.getName().equals(subject.getFluid().getName());
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public List<FluidStack> getInputs() {
		return Arrays.asList(new FluidStack[] {new FluidStack(fluid,amount)});
	}
	
    @Override
	public String toString()
    {
        return "RInputFluidStack<" + this.amount + "x" + this.fluid.getName();
    }

}
