package ihl.recipes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ic2.api.recipe.IRecipeInput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import ihl.utils.IHLUtils;

public class UniversalRecipeManager {

	public static Map<String, UniversalRecipeManager> machineRecipeManagers = new HashMap<String, UniversalRecipeManager>();
	public final String machine;

	public UniversalRecipeManager(String machine1) {
		machine = machine1;
		if (machineRecipeManagers.containsKey(machine1)) {
			throw new IllegalArgumentException("Recipe manager for " + machine1 + " already exist!");
		}
		machineRecipeManagers.put(machine1, this);
	}

	private final Map<UniversalRecipeInput, UniversalRecipeOutput> recipes = new HashMap<UniversalRecipeInput, UniversalRecipeOutput>();
	private final Map<String, UniversalRecipeInput> keywordMap = new HashMap<String, UniversalRecipeInput>();

	public void addRecipe(UniversalRecipeInput input, UniversalRecipeOutput output) {
		if (input == null) {
			throw new NullPointerException("The recipe input is null");
		} else {
			if (output.getFluidOutputs() == null || output.getItemOutputs() == null
					|| (output.getFluidOutputs().size() == 0 && output.getItemOutputs().size() == 0)) {
				throw new NullPointerException("The output is empty");
			}
		}

		Iterator<UniversalRecipeInput> var8 = this.recipes.keySet().iterator();

		while (var8.hasNext()) {
			UniversalRecipeInput existingInput = (UniversalRecipeInput) var8.next();
			if (existingInput.matches(input)) {
				StringBuffer ssError = new StringBuffer(255);
				ssError.append("Ambiguous recipe. \n");
				ssError.append("Existing input: \n");
				Iterator<IRecipeInput> iii1 = existingInput.getItemInputs().iterator();
				Iterator<IRecipeInputFluid> fii1 = existingInput.getFluidInputs().iterator();
				while (iii1 != null && iii1.hasNext()) {
					ssError.append(iii1.next().toString());
					ssError.append(" \n");
				}
				while (fii1 != null && fii1.hasNext()) {
					ssError.append(fii1.next().toString());
					ssError.append(" \n");
				}
				ssError.append("New input: \n");
				Iterator<IRecipeInput> iii2 = input.getItemInputs().iterator();
				Iterator<IRecipeInputFluid> fii2 = input.getFluidInputs().iterator();
				while (iii2 != null && iii2.hasNext()) {
					ssError.append(iii2.next().toString());
					ssError.append(" \n");
				}
				while (fii2 != null && fii2.hasNext()) {
					ssError.append(fii2.next().toString());
					ssError.append(" \n");
				}
				throw new RuntimeException(ssError.toString());
			}
		}

		this.recipes.put(input, output);
	}

	public void addRecipe(String keyword, UniversalRecipeInput input, UniversalRecipeOutput output) {
		this.addRecipe(input, output);
		this.keywordMap.put(keyword, input);
	}

	public UniversalRecipeOutput getOutputFor(List<FluidStack> fluidInputs, List<ItemStack> itemInputs) {
		if (fluidInputs == null && itemInputs == null) {
			return null;
		} else {
			Iterator<Entry<UniversalRecipeInput, UniversalRecipeOutput>> i$ = this.recipes.entrySet().iterator();

			while (true) {
				if (i$.hasNext()) {
					Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
					UniversalRecipeInput recipeInput = entry.getKey();

					if (!recipeInput.matches(fluidInputs, itemInputs)) {
						continue;
					}

					if (recipeInput.matches(fluidInputs, itemInputs, true)) {
						return entry.getValue();
					}
				}

				return null;
			}
		}
	}

	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return this.recipes;
	}

	public UniversalRecipeInput getRecipeInput(List<FluidStack> fluidInputs1, List<ItemStack> itemInputs1) {
		{
			Iterator<Entry<UniversalRecipeInput, UniversalRecipeOutput>> i$ = this.recipes.entrySet().iterator();

			while (true) {
				if (i$.hasNext()) {
					Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
					UniversalRecipeInput recipeInput = entry.getKey();

					if (!recipeInput.matches(fluidInputs1, itemInputs1)) {
						continue;
					}

					if (recipeInput.matches(fluidInputs1, itemInputs1, true)) {
						return recipeInput;
					}
				}

				return null;
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public UniversalRecipeOutput getOutputFor(List[] input) {
		return this.getOutputFor(input[0], input[1]);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public UniversalRecipeInput getRecipeInput(List[] input) {
		return this.getRecipeInput(input[0], input[1]);
	}

	public void removeRecipeByInput(UniversalRecipeInput uRecipeInput) {
		List<FluidStack> fluidInputs = IHLUtils.convertRecipeInputToFluidStackList(uRecipeInput.getFluidInputs());
		List<ItemStack> itemInputs = IHLUtils.convertRecipeInputToItemStackList(uRecipeInput.getItemInputs());
		{
			Iterator<Entry<UniversalRecipeInput, UniversalRecipeOutput>> i$ = this.recipes.entrySet().iterator();
			while (i$.hasNext()) {
				Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
				UniversalRecipeInput recipeInput = entry.getKey();
				if (recipeInput.matches(fluidInputs, itemInputs)) {
					i$.remove();
					break;
				}
			}
		}
	}

	public void removeRecipeByOutput(UniversalRecipeOutput uRecipeOutput) {
		Iterator<Entry<UniversalRecipeInput, UniversalRecipeOutput>> i$ = this.recipes.entrySet().iterator();
		while (i$.hasNext()) {
			Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
			UniversalRecipeOutput recipeOutput = entry.getValue();
			if (recipeOutputHasCommonEntries(recipeOutput, uRecipeOutput)) {
				i$.remove();
			}
		}
	}

	public boolean recipeOutputHasCommonEntries(UniversalRecipeOutput out, UniversalRecipeOutput out1) {
		List<FluidStack> fluidOutputs = out.getFluidOutputs();
		List<RecipeOutputItemStack> itemOutputs = out.getItemOutputs();
		if (!fluidOutputs.isEmpty() && !out1.getFluidOutputs().isEmpty()) {
			FluidStack fs1 = out1.getFluidOutputs().get(0);
			Iterator<FluidStack> fi = fluidOutputs.iterator();
			while (fi.hasNext()) {
				FluidStack fs = fi.next();
				if (fs.getFluid() == fs1.getFluid()) {
					return true;
				}
			}
		}
		if (!itemOutputs.isEmpty() && !out1.getItemOutputs().isEmpty()) {
			RecipeOutputItemStack is1 = out1.getItemOutputs().get(0);
			Iterator<RecipeOutputItemStack> ii = itemOutputs.iterator();
			while (ii.hasNext()) {
				RecipeOutputItemStack is = ii.next();
				if (is.matches(is1)) {
					return true;
				}
			}
		}
		return false;
	}
}
