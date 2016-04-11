package ihl.processing.metallurgy;

import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;

public class MuffleFurnanceContainer extends MachineBaseContainer {

	public MuffleFurnanceContainer(EntityPlayer entityPlayer,
			MachineBaseTileEntity tileEntity1) {
		super(entityPlayer, tileEntity1);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 0, 51, 34));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 1, 69, 34));
	}

}
