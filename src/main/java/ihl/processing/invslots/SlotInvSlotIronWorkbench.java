package ihl.processing.invslots;

import ic2.core.slot.SlotInvSlot;
import ihl.flexible_cable.IronWorkbenchInvSlot;
import ihl.flexible_cable.IronWorkbenchTileEntity;
import net.minecraft.entity.player.EntityPlayer;

public class SlotInvSlotIronWorkbench extends SlotInvSlot {

    public IronWorkbenchInvSlot invSlot;
	
    public SlotInvSlotIronWorkbench(IronWorkbenchInvSlot invSlot1, int index1,
			int xDisplayPosition1, int yDisplayPosition1) {
		super(invSlot1, index1, xDisplayPosition1, yDisplayPosition1);
		this.invSlot=invSlot1;
		
    }
	
    @Override
    public boolean canTakeStack(EntityPlayer player)
    {
        return this.invSlot.getCanTakeStack();
    }


    @Override
    public void onSlotChanged()
    {
	super.onSlotChanged();
	((IronWorkbenchTileEntity)this.invSlot.base).resetOutput();
    }
}
