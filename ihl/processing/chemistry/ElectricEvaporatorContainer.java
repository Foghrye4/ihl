package ihl.processing.chemistry;

import ic2.core.ContainerBase;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ElectricEvaporatorContainer extends ContainerBase {

    protected ElectricEvaporatorTileEntity tileEntity;
    public int lastFluidAmount = -1;
    public double lastEnergy = -1;
    public short lastProgress = -1;
    private final static int height=166;
    
    public ElectricEvaporatorContainer(EntityPlayer entityPlayer, ElectricEvaporatorTileEntity electricEvaporatorTileEntity){
        super(electricEvaporatorTileEntity);
        this.tileEntity = electricEvaporatorTileEntity;
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
        
            this.addSlotToContainer(new SlotInvSlot(electricEvaporatorTileEntity.fuelSlot, 0, 8, 32));
            this.addSlotToContainer(new SlotInvSlot(electricEvaporatorTileEntity.fluidItemsSlot, 0, 44, 14));
            this.addSlotToContainer(new SlotInvSlot(electricEvaporatorTileEntity.emptyFluidItemsSlot, 0, 44, 32));
            this.addSlotToContainer(new SlotInvSlot(electricEvaporatorTileEntity.fillItemsSlot, 0, 44, 50));
            this.addSlotToContainer(new SlotInvSlot(electricEvaporatorTileEntity.outputSlot, 0, 117, 32));
           	for(int row=0;row<=3;row++)
           	{
                this.addSlotToContainer(new SlotInvSlot(tileEntity.upgradeSlot, row, 152, 8+row*18));
            }

    }

    @Override
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.tileEntity.getFluidTank().getFluid()!=null && this.tileEntity.getFluidTank().getFluidAmount() != this.lastFluidAmount)
            {
            	icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.getFluidTank().getFluid().getFluid().getID());
                icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.getFluidTank().getFluidAmount());
            }
            
            if (this.tileEntity.getEnergy() != this.lastEnergy)
            {
            	icrafting.sendProgressBarUpdate(this, 2, ((int)this.tileEntity.getEnergy()>>15) & Short.MAX_VALUE);
            	icrafting.sendProgressBarUpdate(this, 3, (short)((int)this.tileEntity.getEnergy() & Short.MAX_VALUE));
            }
            
            if (this.tileEntity.progress != this.lastProgress)
            {
                icrafting.sendProgressBarUpdate(this, 4, this.tileEntity.progress);
                icrafting.sendProgressBarUpdate(this, 5, this.tileEntity.maxProgress);
            }
        }

        this.lastFluidAmount = this.tileEntity.getFluidTank().getFluidAmount();
        this.lastEnergy = this.tileEntity.getEnergy();
        this.lastProgress = this.tileEntity.progress;
    }
    
    @Override
	public void updateProgressBar(int index, int value)
    {
        super.updateProgressBar(index, value);

        switch (index)
        {
            case 0:
                this.tileEntity.getFluidTank().setFluid(new FluidStack(FluidRegistry.getFluid(value), 1000));
                break;
            case 1:
                this.tileEntity.getFluidTank().setFluid(new FluidStack(this.tileEntity.getFluidTank().getFluid().getFluid(), value));
                break;
            case 2:
                this.tileEntity.setEnergy(value<<15);
                break;
            case 3:
                this.tileEntity.setEnergy(this.tileEntity.getEnergy()+value);
                break;
            case 4:
                this.tileEntity.progress=(short) value;
                break;
            case 5:
                this.tileEntity.maxProgress=(short) value;
                break;
        }
    }
    
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
