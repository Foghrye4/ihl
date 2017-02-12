package ihl.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ic2.api.recipe.IRecipeInput;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeInputOreDictionaryList implements IRecipeInput {

	public final String[] input;
	public final int amount;
	public final Integer meta;
	private List<ItemStack> ores;

	public RecipeInputOreDictionaryList(String[] input1) {
		this(input1, 1);
	}

	public RecipeInputOreDictionaryList(String[] input1, int amount1) {
		this(input1, amount1, (Integer) null);
	}

	public RecipeInputOreDictionaryList(String[] input1, int amount1, Integer meta) {
		this.input = input1;
		this.amount = amount1;
		this.meta = meta;
	}

	@Override
	public boolean matches(ItemStack subject) {
		List<ItemStack> inputs = this.getOres();
		boolean useOreStackMeta = this.meta == null;
		Item subjectItem = subject.getItem();
		int subjectMeta = subject.getItemDamage();
		Iterator<ItemStack> i$ = inputs.iterator();
		Item oreItem;
		int metaRequired;

		do {
			do {
				ItemStack oreStack;

				do {
					if (!i$.hasNext()) {
						return false;
					}

					oreStack = i$.next();
					oreItem = oreStack.getItem();
				} while (oreItem == null);

				metaRequired = useOreStackMeta ? oreStack.getItemDamage() : this.meta.intValue();
			} while (subjectItem != oreItem);
		} while (subjectMeta != metaRequired && metaRequired != 32767);

		return true;
	}

	@Override
	public int getAmount() {
		return this.amount;
	}

	@Override
	public List<ItemStack> getInputs() {
		List<ItemStack> ores = this.getOres();
		boolean hasInvalidEntries = false;
		Iterator<ItemStack> ret = ores.iterator();

		while (ret.hasNext()) {
			ItemStack i$ = ret.next();

			if (i$.getItem() == null) {
				hasInvalidEntries = true;
				break;
			}
		}

		if (!hasInvalidEntries) {
			return ores;
		} else {
			ArrayList<ItemStack> ret1 = new ArrayList<ItemStack>(ores.size());
			Iterator<ItemStack> i$1 = ores.iterator();

			while (i$1.hasNext()) {
				ItemStack stack = i$1.next();

				if (stack.getItem() != null) {
					ret1.add(stack);
				}
			}

			return Collections.unmodifiableList(ret1);
		}
	}

	@Override
	public String toString() {
		return this.meta == null ? "RInputOreDict<" + this.amount + "x" + this.input + ">"
				: "RInputOreDict<" + this.amount + "x" + this.input + "@" + this.meta + ">";
	}

	private List<ItemStack> getOres() {
		if (this.ores != null) {
			return this.ores;
		} else {
			this.ores = new ArrayList<ItemStack>();
			for (int i = 0; i < this.input.length; i++) {
				ArrayList<ItemStack> ret = OreDictionary.getOres(this.input[i]);
				if (ret != OreDictionary.EMPTY_LIST) {
					this.ores.addAll(ret);
				}
			}
			return this.ores;
		}
	}
}
