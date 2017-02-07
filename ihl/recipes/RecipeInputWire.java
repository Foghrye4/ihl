package ihl.recipes;

import java.util.Arrays;
import java.util.List;

import ic2.api.recipe.IRecipeInput;
import ihl.interfaces.IWire;
import ihl.items_blocks.FlexibleCableItem;
import ihl.utils.IHLUtils;
import net.minecraft.item.ItemStack;

public class RecipeInputWire implements IRecipeInput
{
    public final ItemStack input;
    public final int amount;

    public RecipeInputWire(final ItemStack aInput)
    {
        this(aInput, IHLUtils.getWireLength(aInput));
    }

    public RecipeInputWire(final ItemStack aInput, int aAmount)
    {
        if (aInput.getItem() == null || !(aInput.getItem() instanceof IWire))
        {
            throw new IllegalArgumentException("Invalid item stack specfied");
        }
        else
        {
            this.input = aInput;
            this.amount = aAmount;
        }
    }

    public RecipeInputWire(String string, int i) 
    {
    	this(IHLUtils.getThisModWireItemStackWithLength(string, i),i);
	}
    
    public RecipeInputWire(String material, int length, int transverseSection) 
    {
    	this(IHLUtils.getUninsulatedWire(material, length, transverseSection),length);
	}
    
    public RecipeInputWire(String material, int length, int transverseSection, String insulationMaterial, int insulationThickness, int insulationBreakdownVoltage) 
    {
    	this(IHLUtils.getInsulatedWire(material, length, transverseSection, insulationMaterial, insulationThickness),length);
	}

	@Override
	public boolean matches(ItemStack subject)
    {
       if(subject.getItem() == this.input.getItem() && (subject.getItemDamage() == this.input.getItemDamage() || this.input.getItemDamage() == 32767))
       {
    	   if(subject.getItem() instanceof FlexibleCableItem)
    	   {
    		   FlexibleCableItem item = (FlexibleCableItem) subject.getItem();
    		   return item.isSameWire(this.input, subject);
    	   }
    	   else
    	   {
    		   return true;
    	   }
       }
       return false;
    }

    @Override
	public int getAmount()
    {
        return this.amount;
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

