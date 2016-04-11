package ihl.processing.metallurgy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.util.StackUtil;

public class CrucibleContainer extends ContainerBase<CrucibleInventory>
{
    public CrucibleInventory box;
    public CrucibleContainer(EntityPlayer entityPlayer, CrucibleInventory box)
    {
        super(box);
        this.box = box;
        int col;
		if(((Crucible)this.box.thisItemStack.getItem()).isEmpty(this.box.thisItemStack))
		{
	        this.addSlotToContainer(new CrucibleSlot(this.box, 0, 81, 42));
		}
        for (col = 0; col < 3; ++col)
        {
            for (int col1 = 0; col1 < 9; ++col1)
            {
                this.addSlotToContainer(new Slot(entityPlayer.inventory, col1 + col * 9 + 9, 8 + col1 * 18, 84 + col * 18));
            }
        }

        for (col = 0; col < 9; ++col)
        {
            this.addSlotToContainer(new Slot(entityPlayer.inventory, col, 8 + col * 18, 142));
        }
    }

    @Override
	public ItemStack slotClick(int slot, int button, int par3, EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP && IC2.platform.isSimulating() && slot == -999 && (button == 0 || button == 1))
        {
            ItemStack stack = player.inventory.getItemStack();

            if (stack != null)
            {
                NBTTagCompound nbtTagCompoundSlot = StackUtil.getOrCreateNbtData(stack);

                if (this.box.isThisContainer(stack))
                {
                    ((EntityPlayerMP)player).closeScreen();
                }
            }
        }
        return super.slotClick(slot, button, par3, player);
    }

    /**
     * Called when the container is closed.
     */
    @Override
	public void onContainerClosed(EntityPlayer entityPlayer)
    {
        this.box.onGuiClosed(entityPlayer);
        super.onContainerClosed(entityPlayer);
    }
}
