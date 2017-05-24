package ihl.processing.chemistry;

import java.util.List;

import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidStack;

public class FluidizedBedReactorContainer extends BasicElectricMotorContainer<FluidizedBedReactorTileEntity> {

    protected FluidizedBedReactorTileEntity tileEntity;
    public int lastFluidsHash = -1;
    private final static int height=166;
    public List<FluidStack> fluidTankFluidList;
    
    public FluidizedBedReactorContainer(EntityPlayer entityPlayer, FluidizedBedReactorTileEntity tileEntity1){
        super(entityPlayer, tileEntity1);
        this.tileEntity = tileEntity1;
        fluidTankFluidList = this.tileEntity.getFluidTank().getFluidList();
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.fillInputSlot, 0, 102, 51));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.drainInputSlot, 0, 102, 15));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 0, 102, 33));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 0, 41, 23));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 1, 41, 41));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, 0, 76, 33));
    }

    @Override
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        if (fluidTankFluidList.hashCode() != this.lastFluidsHash)
        {
            IC2.network.get().sendContainerField(this, "fluidTankFluidList");
        }
        this.lastFluidsHash = fluidTankFluidList.hashCode();
    }
    
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
