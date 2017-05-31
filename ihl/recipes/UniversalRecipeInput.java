package ihl.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ihl.interfaces.IWire;
import ihl.utils.IHLUtils;
import ihl.worldgen.ores.IHLFluid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class UniversalRecipeInput {

	private final List<IRecipeInputFluid> fluidInputs = new ArrayList<IRecipeInputFluid>();
	private final List<IRecipeInput> itemInputs = new ArrayList<IRecipeInput>();

	public UniversalRecipeInput(Object[] fluidStacks, Object[] iRecipeInputs) {
		if (fluidStacks != null) {
			for (Object material : fluidStacks) {
				if (material == null) {
					throw new NullPointerException("Recipe input cannot be null!");
				}
				if (material instanceof FluidStack) {
					fluidInputs.add(new RecipeInputFluidStack((FluidStack) material));
				} else {
					fluidInputs.add((IRecipeInputFluid) material);
				}
			}
		}
		sortFluidsByDensity();
		if (iRecipeInputs != null) {
			for (Object material : iRecipeInputs) {
				if (material == null) {
					throw new NullPointerException("Recipe input cannot be null!");
				}
				if (material instanceof ItemStack) {
					ItemStack stack = (ItemStack) material;
					String oreDictName = IHLUtils.getFirstOreDictNameExcludingTagAny(stack);
					if (stack.getItem() instanceof IWire) {
						itemInputs.add(new RecipeInputWire(stack));
					} else if (!oreDictName.isEmpty() && oreDictName.length() > 3) {
						itemInputs.add(new RecipeInputOreDict(oreDictName, stack.stackSize));
					} else {
						itemInputs.add(new RecipeInputItemStack(stack));
					}
				} else {
					itemInputs.add((IRecipeInput) material);
				}
			}
		}
	}
	
	public void sortFluidsByDensity()
	{
		Map<Integer, IRecipeInputFluid> sortMap = new HashMap<Integer, IRecipeInputFluid>();
		int[] keysArray = new int[fluidInputs.size()];
    	Iterator<IRecipeInputFluid> fli = fluidInputs.iterator();
		while(fli.hasNext())
    	{
			IRecipeInputFluid rinput = fli.next();
        	FluidStack fluid=rinput.getInputs().get(0);
        	if(fluid==null)
        	{
        		return;
        	}
        		int key = Math.round(IHLFluid.getRealDensity(fluid.getFluid())*100F);
        		while(sortMap.containsKey(key))
        		{
        			key++;
        		}
        		sortMap.put(key, rinput);
        		keysArray[fluidInputs.indexOf(rinput)]=key;
    	}
		Arrays.sort(keysArray);
		List<IRecipeInputFluid> newFluidList = new ArrayList<IRecipeInputFluid>();
		for(int i=keysArray.length-1;i>=0;i--)
		{
			newFluidList.add(sortMap.get(keysArray[i]));
		}
		this.fluidInputs.clear();
		this.fluidInputs.addAll(newFluidList);
	}

	public boolean matches(List<FluidStack> fluidInputs1, List<ItemStack> itemInputs1) {
		return this.matches(fluidInputs1, itemInputs1, false);
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
		List<ItemStack> rInputsItems = new ArrayList<ItemStack>();
		while (ii.hasNext()) {
			IRecipeInput is = ii.next();
			rInputsItems.add(is.getInputs().get(0));
		}
		List<FluidStack> rInputsFluids = new ArrayList<FluidStack>();
		List<IRecipeInputFluid> rInputsF = input.getFluidInputs();
		Iterator<IRecipeInputFluid> iiF = rInputsF.iterator();
		while (iiF.hasNext()) {
			IRecipeInputFluid is = iiF.next();
			rInputsFluids.add(is.getInputs().get(0));
		}
		return this.matches(rInputsFluids, rInputsItems);
	}

	public boolean matches(List<FluidStack> fluidInputs1, List<ItemStack> itemInputs1, boolean doCheckAmounts) {
		if (incorrectInputAmount(fluidInputs1, itemInputs1)) {
			return false;
		}
		if (fluidInputs1 != null) {
			Iterator<IRecipeInputFluid> fi = fluidInputs.iterator();
			while (fi.hasNext()) {
				IRecipeInputFluid fs = fi.next();
				FluidStack fs1 = getMatchedFluidStack(fs, fluidInputs1);
				if (fs1 == null || !fs.matches(fs1)) {
					return false;
				} else if (doCheckAmounts && fs1.amount < fs.getAmount()) {
					return false;
				}
			}
		}
		if (itemInputs1 != null) {
			Iterator<IRecipeInput> ii = itemInputs.iterator();
			while (ii.hasNext()) {
				IRecipeInput is = ii.next();
				ItemStack is1 = getMatchedItemStack(is, itemInputs1);
				if (is1 == null || !is.matches(is1)) {
					return false;
				} else if (doCheckAmounts && IHLUtils.getAmountOf(is1) < is.getAmount()) {
					return false;
				}
			}
		}
		return true;
	}

	private ItemStack getMatchedItemStack(IRecipeInput is, List<ItemStack> itemInputs1) {
		for (ItemStack is1 : itemInputs1) {
			if (is1 != null) {
				if (is.matches(is1)) {
					return is1;
				}
			}
		}
		return null;
	}

	private FluidStack getMatchedFluidStack(IRecipeInputFluid fs, List<FluidStack> fluidInputs1) {
		for (FluidStack fs1 : fluidInputs1) {
			if (fs.matches(fs1)) {
				return fs1;
			}
		}
		return null;
	}

	public boolean containItemStack(ItemStack ingredient) {
		if (itemInputs == null || itemInputs.isEmpty()) {
			return false;
		}
		Iterator<IRecipeInput> ii = itemInputs.iterator();
		while (ii.hasNext()) {
			IRecipeInput is = ii.next();
			if (is.matches(ingredient)) {
				return true;
			}
		}
		return false;
	}

	public boolean containFluidStack(FluidStack fluidStack) {
		if (fluidInputs == null || fluidInputs.isEmpty()) {
			return false;
		}
		Iterator<IRecipeInputFluid> ii = fluidInputs.iterator();
		while (ii.hasNext()) {
			IRecipeInputFluid is = ii.next();
			if (is != null && is.matches(fluidStack)) {
				return true;
			}
		}
		return false;
	}

	private boolean incorrectInputAmount(List<FluidStack> fluidInputs1, List<ItemStack> itemInputs1) {
		return (fluidInputs.size() > 0 && fluidInputs1 == null) || (itemInputs.size() > 0 && itemInputs1 == null)
				|| (fluidInputs1 != null && fluidInputs.size() > fluidInputs1.size())
				|| (itemInputs1 != null && itemInputs.size() > itemInputs1.size());
	}
}
