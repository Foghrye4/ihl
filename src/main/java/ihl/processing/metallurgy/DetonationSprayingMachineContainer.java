package ihl.processing.metallurgy;

import ic2.core.ContainerBase;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class DetonationSprayingMachineContainer extends ContainerBase<DetonationSprayingMachineTileEntity> {

    protected DetonationSprayingMachineTileEntity tileEntity;
    public int lastFluidAmount = -1;
    public short lastProgress = -1;
    private final static int height=166;
    
    public DetonationSprayingMachineContainer(EntityPlayer entityPlayer, DetonationSprayingMachineTileEntity detonationSprayingMachineTileEntity){
        super(detonationSprayingMachineTileEntity);
        this.tileEntity = detonationSprayingMachineTileEntity;
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
            this.addSlotToContainer(new SlotInvSlot(detonationSprayingMachineTileEntity.input, 0, 10, 17));
            this.addSlotToContainer(new SlotInvSlot(detonationSprayingMachineTileEntity.input, 1, 98, 17));
            this.addSlotToContainer(new SlotInvSlot(detonationSprayingMachineTileEntity.input, 2, 117, 17));
    }
    
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
