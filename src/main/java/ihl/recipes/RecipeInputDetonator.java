package ihl.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ic2.api.recipe.IRecipeInput;
import ihl.utils.IHLUtils;
import net.minecraft.item.ItemStack;

public class RecipeInputDetonator  implements IRecipeInput
{
    public final ItemStack input;
    public final int detonator_delay;

    public RecipeInputDetonator(String string, int detonator_delay) 
    {
    	this(IHLUtils.getItemStackWithTag(string, "detonator_delay", detonator_delay));
	}
    
	public RecipeInputDetonator(ItemStack itemStack) 
	{
		input=itemStack;
		detonator_delay=itemStack.stackTagCompound.getInteger("detonator_delay");
	}

	@Override
	public boolean matches(ItemStack subject)
    {
		return subject.getItem() == this.input.getItem() && (subject.getItemDamage() == this.input.getItemDamage() || this.input.getItemDamage() == 32767);
    }

    @Override
	public int getAmount()
    {
        return 1;
    }

    @Override
	public List<ItemStack> getInputs()
    {
        return Arrays.asList(new ItemStack[] {this.input});
    }

    @Override
	public String toString()
    {
        ItemStack stack = this.input.copy();
        return "RInputDice<" + stack + ">";
    }

	public List<ItemStack> transformOutput(ItemStack matchedItemStack,	List<ItemStack> outputs) 
	{
		List<ItemStack> newOutputs = new ArrayList<ItemStack>();
		int misTS = matchedItemStack.stackTagCompound.getInteger("detonator_delay");
		ItemStack material;
		for(ItemStack material1:outputs)
		{
			if(IHLUtils.getFirstOreDictName(material1) == "blockExplosive")
			{
				material=material1.copy();
				material.stackTagCompound.setInteger("detonator_delay", misTS);
				newOutputs.add(material);
			}
			else
			{
				newOutputs.add(material1);
			}
		}
		return newOutputs;
	}
}

