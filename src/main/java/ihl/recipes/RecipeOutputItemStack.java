package ihl.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeOutputItemStack {
	public final ItemStack itemStack;
	public final float quantity;

	public RecipeOutputItemStack(ItemStack itemStack1, float quantity1) {
		itemStack = itemStack1;
		quantity = quantity1;
		itemStack.stackSize = 1;
	}

	public RecipeOutputItemStack(ItemStack itemStack1) {
		this(itemStack1, itemStack1.stackSize);
	}

	public boolean matches(RecipeOutputItemStack is1) {
		if (is1 == null || (itemStack.getItem() != is1.itemStack.getItem())) {
			return false;
		} else if (is1.itemStack.getItemDamage() != OreDictionary.WILDCARD_VALUE
				&& itemStack.getItemDamage() != is1.itemStack.getItemDamage()) {
			return false;
		}
		return true;
	}

	public RecipeOutputItemStack copy(int mulipier) {
		return new RecipeOutputItemStack(itemStack, quantity * mulipier);
	}

	public RecipeOutputItemStack copy() {
		return new RecipeOutputItemStack(itemStack, quantity);
	}

	@Override
	public String toString() {
		return this.itemStack.getUnlocalizedName() + ":" + this.quantity;
	}

	public boolean matches(ItemStack is1) {
		if (is1 == null || (itemStack.getItem() != is1.getItem())) {
			return false;
		} else if (is1.getItemDamage() != OreDictionary.WILDCARD_VALUE
				&& itemStack.getItemDamage() != is1.getItemDamage()) {
			return false;
		}
		return true;
	}
}
