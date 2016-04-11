package ihl.processing.metallurgy;

import ic2.core.ContainerBase;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class DrawingDeskContainer extends ContainerBase {

	public DrawingDeskTileEntity tileEntity;
    public int lastProgress = -1;
    private final static int height=166;

	public DrawingDeskContainer(EntityPlayer entityPlayer,
			DrawingDeskTileEntity tileEntity1) {
		super(tileEntity1);
		tileEntity=tileEntity1;
        int col;

        for (col = 0; col < 3; ++col)
        {
            for (int col1 = 0; col1 < 9; ++col1)
            {
                this.addSlotToContainer(new Slot(entityPlayer.inventory, col1 + col * 9 + 9, 8 + col1 * 18, height + -82 + col * 18));
            }
        }

        for (col = 0; col < 9; ++col)
        {
            this.addSlotToContainer(new Slot(entityPlayer.inventory, col, 8 + col * 18, height + -24));
        }
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 0, 8, 44));		
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.output, 0, 127, 44));		
	}
}
