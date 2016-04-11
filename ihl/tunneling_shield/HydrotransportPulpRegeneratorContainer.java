package ihl.tunneling_shield;

import java.util.List;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

public class HydrotransportPulpRegeneratorContainer extends ContainerBase {

    protected HydrotransportPulpRegeneratorTileEntity tileEntity;
    public int lastFluidAmount = -1;
	public int lastNumberOfFluids = -1;
    public short lastProgress = -1;
    public short lastTemperature = -1;
	private int lastEnergy = -1;
	private List<FluidStack> fluidTankFluidList;
    private final static int height=166;
    
    public HydrotransportPulpRegeneratorContainer(EntityPlayer entityPlayer, HydrotransportPulpRegeneratorTileEntity tileEntity1){
        super(tileEntity1);
        this.tileEntity = tileEntity1;
		fluidTankFluidList=tileEntity.fluidTank.getFluidList();
        int col,row;
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
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.drainInputSlot, 0, 32, 13));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.emptyFluidItemsSlot, 0, 32, 51));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.engine, 0, 8, 32));
        for(col=0;col<4;col++)
        {
            for(row=0;row<3;row++)
            {
                this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, col+row*4, 92+col*18, 13+row*18));
            }
        }
    }

    @Override
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.tileEntity.fluidTank.getFluidAmount() != this.lastFluidAmount || this.tileEntity.fluidTank.getNumberOfFluids() != this.lastNumberOfFluids)
            {
                IC2.network.get().sendContainerField(this, "fluidTankFluidList");
            }
            if (this.tileEntity.progress != this.lastProgress)
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.progress);
            }
            if (this.tileEntity.getEnergy() != this.lastEnergy )
            {
            	icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.getEnergy());
            }
        }

        this.lastNumberOfFluids = this.tileEntity.fluidTank.getNumberOfFluids();
        this.lastFluidAmount = this.tileEntity.fluidTank.getFluidAmount();
        this.lastProgress = this.tileEntity.progress;
        this.lastEnergy = this.tileEntity.getEnergy();
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
	            this.tileEntity.setEnergy(value);
	            break;
        }
    }
    
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
