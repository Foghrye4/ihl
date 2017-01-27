package ihl.processing.chemistry;

import java.util.List;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

public class LabElectrolyzerContainer extends ContainerBase<LabElectrolyzerTileEntity> {

    protected LabElectrolyzerTileEntity tileEntity;
    public short lastProgress = -1;
    public short lastTemperature = -1;
    public short lastEnergy = -1;
    private final static int height=166;    
    public int lastFluidAmount = -1;
	public int lastNumberOfFluids = -1;
    public int lastFluidAmount2 = -1;
	public int lastNumberOfFluids2 = -1;
    public int lastFluidAmount3 = -1;
	public int lastNumberOfFluids3 = -1;
    public List<FluidStack> fluidTankFluidList;
    public List<FluidStack> fluidTankFluidList2;
    public List<FluidStack> fluidTankFluidList3;
    
    public LabElectrolyzerContainer(EntityPlayer entityPlayer, LabElectrolyzerTileEntity tileEntity1){
        super(tileEntity1);
        this.tileEntity = tileEntity1;
        fluidTankFluidList = this.tileEntity.getFluidTank().getFluidList();
        fluidTankFluidList2 = this.tileEntity.fluidTankAnodeOutput.getFluidList();
        fluidTankFluidList3 = this.tileEntity.fluidTankCathodeOutput.getFluidList();
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
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.fillInputSlot, 0, 42, 51));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.fillInputSlotAnodeOutput, 0, 106, 51));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.fillInputSlotCathodeOutput, 0, 8, 51));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.drainInputSlot, 0, 42, 15));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 0, 8, 15));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 1, 42, 33));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 2, 106, 15));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, 1, 87, 51));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.dischargeSlot, 0, 152, 15));
    }

    @Override
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.tileEntity.getTankAmount() != this.lastFluidAmount || this.tileEntity.getNumberOfFluidsInTank() != this.lastNumberOfFluids)
            {
                IC2.network.get().sendContainerField(this, "fluidTankFluidList");
            }
            
            if (this.tileEntity.fluidTankAnodeOutput.getFluidAmount() != this.lastFluidAmount2 || this.tileEntity.fluidTankAnodeOutput.getNumberOfFluids() != this.lastNumberOfFluids2)
            {
                IC2.network.get().sendContainerField(this, "fluidTankFluidList2");
            }
            
            if (this.tileEntity.fluidTankCathodeOutput.getFluidAmount() != this.lastFluidAmount3 || this.tileEntity.fluidTankCathodeOutput.getNumberOfFluids() != this.lastNumberOfFluids3)
            {
                IC2.network.get().sendContainerField(this, "fluidTankFluidList3");
            }
            
            if (this.tileEntity.progress != this.lastProgress)
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.progress);
            }
            
            if (this.tileEntity.temperature != this.lastTemperature)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.temperature);
            }
            
            
            if ((short) this.tileEntity.energy != this.lastEnergy)
            {
                icrafting.sendProgressBarUpdate(this, 2, (short) this.tileEntity.energy);
            }
        }

        this.lastNumberOfFluids = this.tileEntity.getNumberOfFluidsInTank();
        this.lastFluidAmount = this.tileEntity.getTankAmount();
        this.lastProgress = this.tileEntity.progress;
        this.lastTemperature = this.tileEntity.temperature;
        this.lastEnergy = (short) this.tileEntity.energy;
        this.lastNumberOfFluids2 = this.tileEntity.fluidTankAnodeOutput.getNumberOfFluids();
        this.lastFluidAmount2 = this.tileEntity.fluidTankAnodeOutput.getFluidAmount();
        this.lastNumberOfFluids3 = this.tileEntity.fluidTankCathodeOutput.getNumberOfFluids();
        this.lastFluidAmount3 = this.tileEntity.fluidTankCathodeOutput.getFluidAmount();

    }
    
    @Override
	public void updateProgressBar(int index, int value)
    {
        super.updateProgressBar(index, value);
        switch (index)
        {
            case 0:
                this.tileEntity.progress=(short) value;
                break;
            case 1:
                this.tileEntity.temperature=(short) value;
                break;
            case 2:
                this.tileEntity.energy=value;
                break;
        }
    }
    
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
