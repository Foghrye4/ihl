package ihl.recipes;

import java.util.List;

import net.minecraftforge.fluids.FluidStack;
import ihl.IHLMod;

public class RecipeInputFluidDictionary implements IRecipeInputFluid 
{
	private final String input;
	private final int amount;
	public RecipeInputFluidDictionary(String input1, int amount1)
	{
		this.input=input1;
		this.amount=amount1;
	}

	@Override
	public boolean matches(FluidStack subject) 
	{
		if(subject==null)
		{
			return false;
		}
		String fName = IHLMod.fluidDictionary.getFluidName(subject.getFluid());
		return fName==null?false:fName.equals(input);
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public List<FluidStack> getInputs() {
		return IHLMod.fluidDictionary.getFluids(input);
	}
	
    @Override
	public String toString()
    {
        return "RInputFluidStackDictionary<" + this.amount + "x" + this.input;
    }


}
