package ihl.processing.metallurgy;

import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;

public class AchesonFurnanceContainer extends MachineBaseContainer {

	public AchesonFurnanceContainer(EntityPlayer entityPlayer,
			AchesonFurnanceTileEntity tileEntity1) {
		super(entityPlayer, tileEntity1);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.inputElectrode, 0, 63, 40));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.inputElectrode, 1, 99, 40));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 0, 81, 40));		
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 1, 81, 22));		
	}

}
