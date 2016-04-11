package ihl.processing.invslots;

import net.minecraft.entity.player.EntityPlayer;
import ic2.core.block.invslot.InvSlot;
import ic2.core.slot.SlotInvSlot;

public class SlotRedstoneConverterCableInvSlot extends SlotInvSlot {

	public InvSlot invSlot;
	
	public SlotRedstoneConverterCableInvSlot(InvSlot invSlot1, int index1,
			int xDisplayPosition1, int yDisplayPosition1) {
		super(invSlot1, index1, xDisplayPosition1, yDisplayPosition1);
		this.invSlot=invSlot1;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return false;
	}
	
}
