package ihl.processing.chemistry;

import ic2.core.ContainerBase;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class SolarEvaporatorContainer extends ContainerBase<SolarEvaporatorTileEntity> {

	protected SolarEvaporatorTileEntity tileEntity;
	public int lastFluidAmount = -1;
	public int lastFuel = -1;
	public short lastProgress = -1;
	private final static int height = 166;
	public int lastNumberOfFluids = -1;
    public int lastVisibleFluidID = -1;
	public int lastVisibleFluidAmount = -1;

	public SolarEvaporatorContainer(EntityPlayer entityPlayer, SolarEvaporatorTileEntity tileEntity1) {
		super(tileEntity1);
		this.tileEntity = tileEntity1;
		int col;

		for (col = 0; col < 3; ++col) {
			for (int col1 = 0; col1 < 9; ++col1) {
				this.addSlotToContainer(
						new Slot(entityPlayer.inventory, col1 + col * 9 + 9, 8 + col1 * 18, height + -82 + col * 18));
			}
		}

		for (col = 0; col < 9; ++col) {
			this.addSlotToContainer(new Slot(entityPlayer.inventory, col, 8 + col * 18, height + -24));
		}
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.fluidItemsSlot, 0, 44, 14));
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 0, 44, 32));
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.fillItemsSlot, 0, 44, 50));
		this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, 0, 117, 32));

	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			if (this.tileEntity.getFluidTank().getFluid() != null
					&& this.tileEntity.getFluidTank().getFluidAmount() != this.lastFluidAmount) {
				icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.getFluidTank().getFluid().getFluid().getID());
				icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.getFluidTank().getFluidAmount());
			}

			if (this.tileEntity.fuel != this.lastFuel) {
				icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.fuel);
			}

			if (this.tileEntity.progress != this.lastProgress) {
				icrafting.sendProgressBarUpdate(this, 3, this.tileEntity.progress);
			}
            if (this.tileEntity.visibleFluidId != this.lastVisibleFluidID)
            {
                icrafting.sendProgressBarUpdate(this, 4, this.tileEntity.visibleFluidId);
            }
            if (this.tileEntity.visibleFluidAmount != this.lastVisibleFluidAmount)
            {
                icrafting.sendProgressBarUpdate(this, 5, this.tileEntity.visibleFluidAmount);
            }
		}

		this.lastFluidAmount = this.tileEntity.getFluidTank().getFluidAmount();
		this.lastFuel = this.tileEntity.fuel;
		this.lastProgress = this.tileEntity.progress;
        this.lastVisibleFluidID=this.tileEntity.visibleFluidId;
        this.lastVisibleFluidAmount=this.tileEntity.visibleFluidAmount;
	}

	@Override
	public void updateProgressBar(int index, int value) {
		super.updateProgressBar(index, value);

		switch (index) {
		case 0:
			this.tileEntity.getFluidTank().setFluid(new FluidStack(FluidRegistry.getFluid(value), 1000));
			break;
		case 1:
			this.tileEntity.getFluidTank()
					.setFluid(new FluidStack(this.tileEntity.getFluidTank().getFluid().getFluid(), value));
			break;
		case 2:
			this.tileEntity.fuel = value;
			break;
		case 3:
			this.tileEntity.progress = (short) value;
			break;
        case 4:
            this.tileEntity.visibleFluidId=value;
            break;
        case 5:
            this.tileEntity.visibleFluidAmount=value;
            break;

		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
