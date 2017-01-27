package ihl.recipes;

import java.util.List;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipeInputFluid {
	public boolean matches(FluidStack subject);
	public int getAmount();
	public List<FluidStack> getInputs(); 
}
