package ihl.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

public interface ISelectionBoxSpecialRenderer
{
    public void drawSelectionBox(EntityPlayer player, ItemStack currentItem, MovingObjectPosition movingObjectPosition, float partialTick);
}
