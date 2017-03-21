package ihl.processing.metallurgy;

import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;

public class MuffleFurnanceContainer extends MachineBaseContainer {

	public MuffleFurnanceContainer(EntityPlayer entityPlayer,
			MuffleFurnanceTileEntity tileEntity1) {
		super(entityPlayer, tileEntity1);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 0, 40, 22));		
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 1, 40, 40));		
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, 0, 98, 32));		
	}

}
