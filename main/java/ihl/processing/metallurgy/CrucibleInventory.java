package ihl.processing.metallurgy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.item.tool.HandHeldInventory;
import ic2.core.util.StackUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CrucibleInventory extends HandHeldInventory {

	ItemStack thisItemStack;
	IInventory inventoryContainer;

	public CrucibleInventory(EntityPlayer player, ItemStack stack) {
		super(player, stack, 1);
		thisItemStack = stack;
		inventoryContainer = player.inventory;
	}

	public CrucibleInventory(IInventory inventoryContainer1, ItemStack stack) {
		super(null, stack, 1);
		thisItemStack = stack;
		inventoryContainer = inventoryContainer1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new CrucibleGui(new CrucibleContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new CrucibleContainer(player, this);
	}

	@Override
	public String getInventoryName() {
		return "crucible";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int arg0, ItemStack stack) {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 6;
	}

	@Override
	public void save() {
		if (IC2.platform.isSimulating()) {
			NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(this.thisItemStack);

			for (int nbtTagList = 0; nbtTagList < this.getSizeInventory(); ++nbtTagList) {
				if (this.inventory[nbtTagList] != null) {
					NBTTagCompound i = StackUtil.getOrCreateNbtData(this.inventory[nbtTagList]);

					if (nbtTagCompound.getInteger("uid") == i.getInteger("uid")) {
						this.thisItemStack.stackSize = 1;
						this.inventory[nbtTagList] = null;
						break;
					}
				}
			}

			NBTTagList var7 = new NBTTagList();
			int var8;

			for (var8 = 0; var8 < this.inventory.length; ++var8) {
				if (this.inventory[var8] != null) {
					NBTTagCompound itemStackSlot = new NBTTagCompound();
					itemStackSlot.setByte("Slot", (byte) var8);
					this.inventory[var8].writeToNBT(itemStackSlot);
					var7.appendTag(itemStackSlot);
				}
			}

			nbtTagCompound.setTag("Items", var7);

			for (var8 = -1; var8 < inventoryContainer.getSizeInventory(); ++var8) {
				ItemStack var9 = null;

				if (var8 == -1 && inventoryContainer instanceof InventoryPlayer) {
					var9 = ((InventoryPlayer) inventoryContainer).getItemStack();
				} else if (var8 >= 0) {
					var9 = inventoryContainer.getStackInSlot(var8);
				}

				if (var9 != null) {
					NBTTagCompound nbtTagCompoundSlot = var9.getTagCompound();

					if (nbtTagCompoundSlot != null
							&& nbtTagCompound.getInteger("uid") == nbtTagCompoundSlot.getInteger("uid")) {
						this.thisItemStack.stackSize = 1;

						if (var8 == -1 && inventoryContainer instanceof InventoryPlayer) {
							((InventoryPlayer) inventoryContainer).setItemStack(this.thisItemStack);
						} else {
							inventoryContainer.setInventorySlotContents(var8, this.thisItemStack);
						}

						break;
					}
				}
			}

		}
	}

}
