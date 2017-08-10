package ihl.flexible_cable;

import java.util.Iterator;
import java.util.List;

import ic2.api.recipe.IRecipeInput;
import ic2.core.IC2;
import ihl.recipes.IronWorkbenchRecipe;
import ihl.utils.IHLUtils;
import net.minecraft.item.ItemStack;

public class InvSlotTool extends IronWorkbenchInvSlot {

	public InvSlotTool(IronWorkbenchTileEntity base1, String name1, int oldStartIndex1, Access access1, int count) {
		super(base1, name1, oldStartIndex1, access1, count);
		// TODO Auto-generated constructor stub
	}

	public void damage(List<IRecipeInput> tools) {
		if (tools != null && !tools.isEmpty()) {
			for (int i = 0; i < this.size(); i++) {
				ItemStack is = this.get(i);
				Iterator<IRecipeInput> i1 = tools.iterator();
				while (i1.hasNext()) {
					IRecipeInput is1 = i1.next();
					if (is != null && (is1.matches(is))) {
						if (!is.attemptDamageItem(1, IC2.random)) {
							if (is.stackTagCompound != null && is.stackTagCompound.hasKey("GT.ToolStats")) {
								IHLUtils.damageItemViaNBTTag(is, 1);
							}
						}
						if (is.stackSize <= 0) {
							this.put(i, null);
						}
					}
				}
			}
		}
	}

	@Override
	public void put(int index, ItemStack content) {
		super.put(index, content);
		if (IC2.platform.isSimulating() && ((IronWorkbenchTileEntity) this.base).container != null) {
			((IronWorkbenchTileEntity) this.base).resetOutput();
			((IronWorkbenchTileEntity) this.base).container.detectAndSendChanges();
		}
	}

	@Override
	public boolean accepts(ItemStack itemStack) {
		for (IronWorkbenchRecipe recipe : IronWorkbenchTileEntity.recipes) {
			if (recipe.isTool(itemStack)) {
				return true;
			}
		}
		return false;
	}

	public boolean contain(ItemStack is1) {
		for (int i = 0; i < this.size(); i++) {
			ItemStack is = this.get(i);
			if (is != null && (is.getItem() == is1.getItem() || IHLUtils.isItemsHaveSameOreDictionaryEntry(is, is1))) {
				return true;
			}
		}
		return false;
	}
}
