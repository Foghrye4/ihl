package ihl.processing.chemistry;

import java.util.List;

import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidStack;

public class LabElectrolyzerContainer extends BasicElectricMotorContainer<LabElectrolyzerTileEntity> {

	protected LabElectrolyzerTileEntity tileEntity;
	public short lastProgress = -1;
	public short lastTemperature = -1;
	public short lastEnergy = -1;
	public int lastFluidAmount = -1;
	public int lastNumberOfFluids = -1;
	public int lastFluidAmount2 = -1;
	public int lastNumberOfFluids2 = -1;
	public int lastFluidAmount3 = -1;
	public int lastNumberOfFluids3 = -1;
	public List<FluidStack> fluidTankFluidList;
	public List<FluidStack> fluidTankFluidList2;
	public List<FluidStack> fluidTankFluidList3;

	public LabElectrolyzerContainer(EntityPlayer entityPlayer, LabElectrolyzerTileEntity tileEntity1) {
		super(entityPlayer, tileEntity1);
		this.tileEntity = tileEntity1;
		fluidTankFluidList = this.tileEntity.getFluidTank().getFluidList();
		fluidTankFluidList2 = this.tileEntity.fluidTankAnodeOutput.getFluidList();
		fluidTankFluidList3 = this.tileEntity.fluidTankCathodeOutput.getFluidList();
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.fillInputSlot, 0, 63, 47));
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.fillInputSlotAnodeOutput, 0, 109, 47));
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.fillInputSlotCathodeOutput, 0, 29, 47));
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.drainInputSlot, 0, 63, 11));
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 0, 29, 11));
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 1, 63, 29));
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 2, 109, 11));
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, 1, 63, 65));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (this.tileEntity.getTankAmount() != this.lastFluidAmount
				|| this.tileEntity.getNumberOfFluidsInTank() != this.lastNumberOfFluids) {
			IC2.network.get().sendContainerField(this, "fluidTankFluidList");
		}

		if (this.tileEntity.fluidTankAnodeOutput.getFluidAmount() != this.lastFluidAmount2
				|| this.tileEntity.fluidTankAnodeOutput.getNumberOfFluids() != this.lastNumberOfFluids2) {
			IC2.network.get().sendContainerField(this, "fluidTankFluidList2");
		}

		if (this.tileEntity.fluidTankCathodeOutput.getFluidAmount() != this.lastFluidAmount3
				|| this.tileEntity.fluidTankCathodeOutput.getNumberOfFluids() != this.lastNumberOfFluids3) {
			IC2.network.get().sendContainerField(this, "fluidTankFluidList3");
		}

		this.lastNumberOfFluids = this.tileEntity.getNumberOfFluidsInTank();
		this.lastFluidAmount = this.tileEntity.getTankAmount();
		this.lastNumberOfFluids2 = this.tileEntity.fluidTankAnodeOutput.getNumberOfFluids();
		this.lastFluidAmount2 = this.tileEntity.fluidTankAnodeOutput.getFluidAmount();
		this.lastNumberOfFluids3 = this.tileEntity.fluidTankCathodeOutput.getNumberOfFluids();
		this.lastFluidAmount3 = this.tileEntity.fluidTankCathodeOutput.getFluidAmount();

	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
