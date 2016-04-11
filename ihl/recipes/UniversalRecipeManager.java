package ihl.recipes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import ic2.api.recipe.IRecipeInput;
import ihl.utils.IHLUtils;

public class UniversalRecipeManager {
	
	public static Map<String,UniversalRecipeManager> machineRecipeManagers = new HashMap<String,UniversalRecipeManager>();
	public final String machine;
	public UniversalRecipeManager(String machine1)
	{
		machine=machine1;
		if(machineRecipeManagers.containsKey(machine1))
		{
			throw new IllegalArgumentException("Recipe manager for "+machine1+" already exist!");
		}
		machineRecipeManagers.put(machine1, this);
	}

	private final Map<UniversalRecipeInput, UniversalRecipeOutput> recipes = new HashMap();
	
    public void addRecipe(UniversalRecipeInput input, UniversalRecipeOutput output)
    {
        if (input == null)
        {
            throw new NullPointerException("The recipe input is null");
        }
        else
        {
            if (output.getFluidOutputs() == null || output.getItemOutputs() == null ||(output.getFluidOutputs().size()==0 && output.getItemOutputs().size()==0))
            {
                    throw new NullPointerException("The output is empty");
            }
        }

            Iterator var8 = this.recipes.keySet().iterator();

            while (var8.hasNext())
            {
            	UniversalRecipeInput existingInput = (UniversalRecipeInput)var8.next();
                    if (existingInput.matches(input))
                    {
                    	Iterator<IRecipeInput> ilist1 = existingInput.getItemInputs().iterator();
                    	Iterator<IRecipeInput> ilist2 = input.getItemInputs().iterator();
                    	while(ilist1.hasNext())
                    	{
                    		IRecipeInput is = ilist1.next();
                    	}
                    	System.out.println("recipe 2:");
                    	while(ilist2.hasNext())
                    	{
                    		IRecipeInput is = ilist2.next();
                    	}
                        throw new RuntimeException("Ambiguous recipe.");
                    }
            }

            this.recipes.put(input, output);
     }
    

    public UniversalRecipeOutput getOutputFor(List<FluidStack> fluidInputs, List<ItemStack> itemInputs, boolean adjustInput, boolean inputAffectOutput)
    {
        if (fluidInputs == null && itemInputs == null)
        {
            return null;
        }
        else
        {
            Iterator i$ = this.recipes.entrySet().iterator();

            while (true)
            {
                if (i$.hasNext())
                {
                    Entry entry = (Entry)i$.next();
                    UniversalRecipeInput recipeInput = (UniversalRecipeInput)entry.getKey();

                    if (!recipeInput.matches(fluidInputs, itemInputs))
                    {
                        continue;
                    }

                    if (recipeInput.adjustAmounts(fluidInputs, itemInputs,true, false))
                    {
                        UniversalRecipeOutput output = (UniversalRecipeOutput)entry.getValue();
                        if (adjustInput)
                        {
                        	if(inputAffectOutput)
                        	{
                            	int multiplier = recipeInput.getMultiplierAndAdjustAmounts(fluidInputs, itemInputs);
                            	return output.copyWithMultiplier(multiplier);
                        	}
                        	else
                        	{
                            	recipeInput.adjustAmounts(fluidInputs, itemInputs,true, true);
                        	}
                        }
                        return output;
                    }
                }

                return null;
            }
        }
    }

    public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes()
    {
        return this.recipes;
    }


	public UniversalRecipeInput getRecipeInput(List<FluidStack> fluidInputs1, List<ItemStack> itemInputs1) {
        {
            Iterator i$ = this.recipes.entrySet().iterator();

            while (true)
            {
                if (i$.hasNext())
                {
                    Entry entry = (Entry)i$.next();
                    UniversalRecipeInput recipeInput = (UniversalRecipeInput)entry.getKey();

                    if (!recipeInput.matches(fluidInputs1,itemInputs1))
                    {
                        continue;
                    }

                    if (recipeInput.adjustAmounts(fluidInputs1,itemInputs1,true, false))
                    {
                        return recipeInput;
                    }
                }

                return null;
            }
        }
	}


	public UniversalRecipeOutput getOutputFor(List[] input,  boolean adjustInput, boolean inputAffectOutput) 
	{
		return this.getOutputFor(input[0], input[1], adjustInput, inputAffectOutput);
	}


	public UniversalRecipeInput getRecipeInput(List[] input) 
	{
		return this.getRecipeInput(input[0], input[1]);
	}


	public void removeRecipeByInput(UniversalRecipeInput uRecipeInput) 
	{
		Entry entryToRemove = null;
		List<FluidStack> fluidInputs = IHLUtils.convertRecipeInputToFluidStackList(uRecipeInput.getFluidInputs());
		List<ItemStack> itemInputs = IHLUtils.convertRecipeInputToItemStackList(uRecipeInput.getItemInputs());
        {
            Iterator i$ = this.recipes.entrySet().iterator();
            while (i$.hasNext())
            {
                    Entry entry = (Entry)i$.next();
                    UniversalRecipeInput recipeInput = (UniversalRecipeInput)entry.getKey();
                    if (recipeInput.matches(fluidInputs, itemInputs))
                    {
                    	i$.remove();
                    	break;
                    }
            }
        }
	}

	public void removeRecipeByOutput(UniversalRecipeOutput uRecipeOutput) 
	{
		Entry entryToRemove = null;
		Iterator i$ = this.recipes.entrySet().iterator();
        while (i$.hasNext())
        {
             Entry entry = (Entry)i$.next();
             UniversalRecipeOutput recipeOutput = (UniversalRecipeOutput)entry.getValue();
             if (recipeOutputHasCommonEntries(recipeOutput,uRecipeOutput))
             {
               	i$.remove();
             }
        }
	}
	
	public boolean recipeOutputHasCommonEntries(UniversalRecipeOutput out, UniversalRecipeOutput out1) 
	{
		List<FluidStack> fluidOutputs = out.getFluidOutputs();
		List<RecipeOutputItemStack> itemOutputs = out.getItemOutputs();
		if(!fluidOutputs.isEmpty() && !out1.getFluidOutputs().isEmpty())
		{
			FluidStack fs1 = out1.getFluidOutputs().get(0);
			Iterator<FluidStack> fi = fluidOutputs.iterator();
			while(fi.hasNext())
			{
				FluidStack fs = fi.next();
				if(fs.getFluid()==fs1.getFluid())
				{
					return true;
				}
			}
		}
		if(!itemOutputs.isEmpty() && !out1.getItemOutputs().isEmpty())
		{
			RecipeOutputItemStack is1 = out1.getItemOutputs().get(0);
			Iterator<RecipeOutputItemStack> ii = itemOutputs.iterator();
			while(ii.hasNext())
			{
				RecipeOutputItemStack is = ii.next();
				if(is.matches(is1))
				{
					return true;
				}
			}
		}
		return false;
	}
}
