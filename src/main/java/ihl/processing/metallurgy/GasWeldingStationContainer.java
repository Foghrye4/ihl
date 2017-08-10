package ihl.processing.metallurgy;

import java.util.List;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

public class GasWeldingStationContainer extends ContainerBase<GasWeldingStationTileEntity> {

    protected GasWeldingStationTileEntity tileEntity;
    public short lastProgress2 = -1;
    private final static int height=166;
    public int lastFluidAmount = -1;
	public int lastNumberOfFluids = -1;
    public int lastFluidAmount2 = -1;
	public int lastNumberOfFluids2 = -1;
    public List<FluidStack> fluidTankFluidList;
    public List<FluidStack> fluidTankFluidList2;
    
    public GasWeldingStationContainer(EntityPlayer entityPlayer, GasWeldingStationTileEntity weldingStation){
        super(weldingStation);
        this.tileEntity = weldingStation;
        fluidTankFluidList = this.tileEntity.flammableGasTank.getFluidList();
        fluidTankFluidList2 = this.tileEntity.oxygenTank.getFluidList();
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
        this.addSlotToContainer(new SlotInvSlot(tileEntity.drainInputSlotOxygen, 0, 8, 15));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.drainInputSlotFlammableGas, 0, 44, 15));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.fillInputSlotOxygen, 0, 8, 51));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.fillInputSlotFlammableGas, 0, 44, 51));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.emptyFluidItemsSlot, 0, 8, 33));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.emptyFluidItemsSlot, 1, 44, 33));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.input, 0, 62, 51));
    }
    
	   @Override
	public void detectAndSendChanges()
	    {
	        super.detectAndSendChanges();
	        for (int i = 0; i < this.crafters.size(); ++i)
	        {
	            ICrafting icrafting = (ICrafting)this.crafters.get(i);
	            if (this.tileEntity.progress2 != this.lastProgress2)
	            {
	            	icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.progress2);
	            }
	            if (this.tileEntity.flammableGasTank.getFluidAmount() != this.lastFluidAmount || this.tileEntity.flammableGasTank.getNumberOfFluids() != this.lastNumberOfFluids)
	            {

	                IC2.network.get().sendContainerField(this, "fluidTankFluidList");
	            }
	            
	            if (this.tileEntity.oxygenTank.getFluidAmount() != this.lastFluidAmount2 || this.tileEntity.oxygenTank.getNumberOfFluids() != this.lastNumberOfFluids2)
	            {

	                IC2.network.get().sendContainerField(this, "fluidTankFluidList2");
	            }
	        }
	        
	        
	        this.lastProgress2 = this.tileEntity.progress2;
	        this.lastNumberOfFluids = this.tileEntity.flammableGasTank.getNumberOfFluids();
	        this.lastFluidAmount = this.tileEntity.flammableGasTank.getFluidAmount();
	        this.lastNumberOfFluids2 = this.tileEntity.oxygenTank.getNumberOfFluids();
	        this.lastFluidAmount2 = this.tileEntity.oxygenTank.getFluidAmount();
	    }
	    
	    @Override
		public void updateProgressBar(int index, int value)
	    {
	        super.updateProgressBar(index, value);
	        switch (index)
	        {
	        case 1:
	            this.tileEntity.progress2=(short) value;
	            break;
	        }
	    }
    
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
