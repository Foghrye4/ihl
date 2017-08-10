package ihl.items_blocks;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ihl.utils.IHLUtils;

public class RecipeInputs 
{
	public static IRecipeInput cutter = new RecipeInputOreDict("craftingToolWireCutter");
	public static IRecipeInput saw = new RecipeInputOreDict("craftingToolSaw");
	public static IRecipeInput file = new RecipeInputOreDict("craftingToolFile");
	public static IRecipeInput vise = new RecipeInputItemStack(IHLUtils.getThisModItemStack("viseSteel"));
	public static IRecipeInput plateSteel = new RecipeInputOreDict("plateSteel");
	public static IRecipeInput hammer = new RecipeInputOreDict("craftingToolHardHammer");

	public static IRecipeInput get(String name, int amount)
	{
		return new RecipeInputItemStack(IHLUtils.getThisModItemStackWithSize(name,amount),amount);
	}
	public static IRecipeInput get(String name)
	{
		return new RecipeInputItemStack(IHLUtils.getThisModItemStack(name));
	}
}
