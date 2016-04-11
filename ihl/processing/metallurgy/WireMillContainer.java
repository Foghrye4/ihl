package ihl.processing.metallurgy;

import java.util.List;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

public class WireMillContainer extends ContainerBase {

	public WireMillTileEntity tileEntity;
    public int lastProgress = -1;
	private short lastEnergy = -1;
    public int lastFluidAmount = -1;
	public int lastNumberOfFluids = -1;
    public int lastFluidAmount2 = -1;
	public int lastNumberOfFluids2 = -1;
    public int lastFluidAmount3 = -1;
	public int lastNumberOfFluids3 = -1;
    private final static int height=166;
    public List<FluidStack> fluidTankFluidList;
    public List<FluidStack> fluidTankFluidList2;
    public List<FluidStack> fluidTankFluidList3;

	public WireMillContainer(EntityPlayer entityPlayer,
			WireMillTileEntity lathePart1TileEntity) {
		super(lathePart1TileEntity);
		tileEntity=lathePart1TileEntity;
        fluidTankFluidList = this.tileEntity.waterFluidTank.getFluidList();
        fluidTankFluidList2 = this.tileEntity.oilFluidTank.getFluidList();
        fluidTankFluidList3 = this.tileEntity.metalFluidTank.getFluidList();
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
        this.addSlotToContainer(new SlotInvSlot(tileEntity.dice, 0, 99+18, 31));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.drainInputSlot, 0, 26+18, 14));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.fillInputSlot, 0, 26+18, 50));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.emptyFluidItemsSlot, 0, 26+18, 32));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.engine,0, 8, 14));	
	}

	   @Override
	public void detectAndSendChanges()
	    {
	        super.detectAndSendChanges();
	        for (int i = 0; i < this.crafters.size(); ++i)
	        {
	            ICrafting icrafting = (ICrafting)this.crafters.get(i);
	            if (this.tileEntity.progress != this.lastProgress)
	            {
	            	icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.progress);
	            }
	            if (this.tileEntity.getEnergy() != this.lastEnergy)
	            {
	            	icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.getEnergy());
	            }
	            
	            if (this.tileEntity.waterFluidTank.getFluidAmount() != this.lastFluidAmount || this.tileEntity.waterFluidTank.getNumberOfFluids() != this.lastNumberOfFluids)
	            {
	                IC2.network.get().sendContainerField(this, "fluidTankFluidList");
	            }
	            
	            if (this.tileEntity.oilFluidTank.getFluidAmount() != this.lastFluidAmount2 || this.tileEntity.oilFluidTank.getNumberOfFluids() != this.lastNumberOfFluids2)
	            {
	                IC2.network.get().sendContainerField(this, "fluidTankFluidList2");
                }
	            
	            if (this.tileEntity.metalFluidTank.getFluidAmount() != this.lastFluidAmount3 || this.tileEntity.metalFluidTank.getNumberOfFluids() != this.lastNumberOfFluids3)
	            {
	                IC2.network.get().sendContainerField(this, "fluidTankFluidList3");
                }
	        }
	        this.lastNumberOfFluids = this.tileEntity.waterFluidTank.getNumberOfFluids();
	        this.lastFluidAmount = this.tileEntity.waterFluidTank.getFluidAmount();
	        this.lastNumberOfFluids2 = this.tileEntity.oilFluidTank.getNumberOfFluids();
	        this.lastFluidAmount2 = this.tileEntity.oilFluidTank.getFluidAmount();
	        this.lastNumberOfFluids3 = this.tileEntity.metalFluidTank.getNumberOfFluids();
	        this.lastFluidAmount3 = this.tileEntity.metalFluidTank.getFluidAmount();
	        this.lastProgress = this.tileEntity.progress;
	        this.lastEnergy = (short) this.tileEntity.getEnergy();
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
	    
}
