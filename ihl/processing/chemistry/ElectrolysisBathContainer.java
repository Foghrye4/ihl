package ihl.processing.chemistry;

import java.util.List;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

public class ElectrolysisBathContainer extends ContainerBase {

    protected ElectrolysisBathTileEntity tileEntity;
    public int lastFluidAmount = -1;
	public int lastNumberOfFluids = -1;
    public short lastProgress = -1;
    public short lastTemperature = -1;
    private final static int height=166;
    public List<FluidStack> fluidTankFluidList;
    
    public ElectrolysisBathContainer(EntityPlayer entityPlayer, ElectrolysisBathTileEntity tileEntity1){
        super(tileEntity1);
        this.tileEntity = tileEntity1;
        fluidTankFluidList = this.tileEntity.getFluidTank().getFluidList();
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
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.fillInputSlot, 0, 6, 52));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.drainInputSlot, 0, 6, 16));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 0, 6, 33));
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.input, 0, 98, 16));
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
            
            if (this.tileEntity.progress != this.lastProgress)
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.progress);
            }
            
            if (this.tileEntity.temperature != this.lastTemperature)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.temperature);
            }
        }

        this.lastNumberOfFluids = this.tileEntity.getNumberOfFluidsInTank();
        this.lastFluidAmount = this.tileEntity.getTankAmount();
        this.lastProgress = this.tileEntity.progress;
        this.lastTemperature = this.tileEntity.temperature;
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
        }
    }
    
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
