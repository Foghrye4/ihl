package ihl.interfaces;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IItemHasMiniGUI 
{
	@SideOnly(Side.CLIENT)
	ItemMiniGUI getMiniGUI(GuiContainer gui, Slot slot);
}
