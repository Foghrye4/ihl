package ihl.processing.invslots;

import ic2.core.slot.SlotInvSlot;
import ihl.flexible_cable.IronWorkbenchInvSlot;
import net.minecraft.entity.player.EntityPlayer;

public class SlotInvSlotOutputInProgress extends SlotInvSlot {

	public IronWorkbenchInvSlot invSlot;
	
	public SlotInvSlotOutputInProgress(IronWorkbenchInvSlot invSlot1, int index1,
			int xDisplayPosition1, int yDisplayPosition1) {
		super(invSlot1, index1, xDisplayPosition1, yDisplayPosition1);
		this.invSlot=invSlot1;
		
	}
	
	@Override
    public boolean canTakeStack(EntityPlayer player)
    {
        return this.invSlot.getCanTakeStack();
    }
}
