package ihl.recipes;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import ic2.api.recipe.IRecipeInput;
import ihl.interfaces.IWire;
import ihl.items_blocks.FlexibleCableItem;
import ihl.utils.IHLUtils;

public class RecipeInputObjectInstance implements IRecipeInput
{
    public final ItemStack input;

    public RecipeInputObjectInstance(ItemStack aInput)
    {
            this.input = aInput;
    }

	@Override
	public boolean matches(ItemStack subject)
    {
       return this.input==subject;
    }

    @Override
	public int getAmount()
    {
   		return IHLUtils.getAmountOf(input);
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
        return "RInputWireItemStack<" + stack + ">";
    }

}
