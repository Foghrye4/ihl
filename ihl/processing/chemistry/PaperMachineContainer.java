package ihl.processing.chemistry;

import java.util.List;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

public class PaperMachineContainer extends BasicElectricMotorContainer<PaperMachineTileEntity> {

    protected PaperMachineTileEntity tileEntity;
    public int lastFluidAmount = -1;
	public int lastNumberOfFluids = -1;
    public short lastProgress = -1;
    public short lastTemperature = -1;
    public short lastEnergy = -1;
    public List<FluidStack> fluidTankFluidList;
    
    public PaperMachineContainer(EntityPlayer entityPlayer, PaperMachineTileEntity tileEntity1){
        super(entityPlayer, tileEntity1);
        this.tileEntity = tileEntity1;
        fluidTankFluidList=this.tileEntity.getFluidTank().getFluidList();
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.fillInputSlot, 0, 78, 51));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.drainInputSlot, 0, 78, 15));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 0, 78, 33));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, 0, 122, 51));
    }

    @Override
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        if (this.tileEntity.getTankAmount() != this.lastFluidAmount || this.tileEntity.getNumberOfFluidsInTank() != this.lastNumberOfFluids)
        {
            IC2.network.get().sendContainerField(this, "fluidTankFluidList");
        }
        this.lastNumberOfFluids = this.tileEntity.getNumberOfFluidsInTank();
        this.lastFluidAmount = this.tileEntity.getTankAmount();
    }
}
