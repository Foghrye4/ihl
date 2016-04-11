package ihl.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ic2.api.recipe.IRecipeInput;
import ihl.interfaces.IWire;
import ihl.items_blocks.FlexibleCableItem;
import ihl.utils.IHLUtils;
import net.minecraft.item.ItemStack;

public class RecipeInputDie  implements IRecipeInput
{
    public final ItemStack input;
    public final int transverseSection;

    public RecipeInputDie(String string, int transverseSection) 
    {
    	this(IHLUtils.getItemStackWithTag(string, "transverseSection", transverseSection));
	}
    
	public RecipeInputDie(ItemStack itemStack) 
	{
		input=itemStack;
		transverseSection=itemStack.stackTagCompound.getInteger("transverseSection");
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
		List<ItemStack> newOutputs = new ArrayList();
		int misTS = matchedItemStack.stackTagCompound.getInteger("transverseSection");
		ItemStack material;
		for(ItemStack material1:outputs)
		{
			if(material1.getItem() instanceof IWire)
			{
				material=material1.copy();
				int length = material.stackTagCompound.getInteger("length");
				length = length * transverseSection / misTS;
				material.stackTagCompound.setInteger("length", length);
				material.stackTagCompound.setInteger("fullLength", length);
				material.stackTagCompound.setInteger("transverseSection", misTS);
				newOutputs.add(material);
			}
			else
			{
				newOutputs.add(material1);
			}
		}
		return newOutputs;
	}

	public int transformOutput(ItemStack matchedItemStack, ItemStack material) 
	{
		int consumeAmountMultiplier=1;
		int misTS = matchedItemStack.stackTagCompound.getInteger("transverseSection");
		if(misTS<=transverseSection)
		{
			int length = material.stackTagCompound.getInteger("length");
			length = length * transverseSection / misTS;
			material.stackTagCompound.setInteger("length", length);
			material.stackTagCompound.setInteger("fullLength", length);
		}
		else
		{
			consumeAmountMultiplier=misTS/transverseSection+1;
		}
		material.stackTagCompound.setInteger("transverseSection", misTS);
		return consumeAmountMultiplier;
	}
}

